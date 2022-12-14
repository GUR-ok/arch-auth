package ru.gur.archauth.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.springframework.web.client.RestTemplate;
import ru.gur.archauth.crypt.CryptUtils;
import ru.gur.archauth.entity.Person;
import ru.gur.archauth.entity.Session;
import ru.gur.archauth.exception.InvalidPasswordException;
import ru.gur.archauth.exception.UserExistsException;
import ru.gur.archauth.exception.UserNotFoundException;
import ru.gur.archauth.persistence.PersonRepository;
import ru.gur.archauth.persistence.RedisRepository;
import ru.gur.archauth.service.data.LoginData;
import ru.gur.archauth.web.user.request.LoginRequest;
import ru.gur.archauth.web.user.request.RegisterRequest;

import java.net.URI;
import java.security.KeyPair;
import java.sql.Date;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final PersonRepository personRepository;
    private final KeyPair keyPair;
    private final RestTemplate restTemplate = new RestTemplate();
    private final RedisRepository redisRepository;

    @Value("${interaction.profiles.uri}")
    private URI profilesUri;
    @Value("${app.ttl:120000}") //120 sec
    private Long ttl;

//    @Value("${jwt.secret}")
//    private String secret;

    @Override
    @Transactional
    public Map<String, UUID> register(RegisterRequest registerRequest) {
        personRepository.findByLogin(registerRequest.getLogin())
                .ifPresent(u -> {
                    throw new UserExistsException("User with login " +
                            u.getLogin() +
                            " already exists");
                });

        final Person person = Person.builder()
                .email(registerRequest.getEmail())
                .login(registerRequest.getLogin())
                .password(CryptUtils.hash(registerRequest.getPassword()))
                .build();

        final RequestEntity<String> requestEntity = RequestEntity.post(profilesUri + "/profiles").body(registerRequest.getEmail());

        final UUID profileUUID = restTemplate.exchange(requestEntity, UUID.class).getBody();
//        final UUID profileUUID = UUID.randomUUID();

        person.setProfileId(profileUUID);

        personRepository.save(person);

        return Map.of("profileId", person.getProfileId(),
                "userId", person.getId());
    }

    @Override
    @Transactional
    public LoginData login(LoginRequest loginRequest) {
        final Person person = personRepository.findByLogin(loginRequest.getLogin())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!CryptUtils.verifyAndUpdateHash(loginRequest.getPassword(), person.getPassword()))
            throw new InvalidPasswordException("Password invalid");

        String token = Jwts.builder()
                .setIssuer("http://arch-auth-service.arch-gur")
                .setSubject("user")
                .claim("kid", "gur-id")
                .claim("profileId", person.getProfileId())
                .signWith(SignatureAlgorithm.RS256, keyPair.getPrivate())
                .setExpiration(new Date(System.currentTimeMillis() + 300000)) //300 sec
                .compact();

        redisRepository.save(new Session(token, ttl));

        return LoginData.builder()
                .token(token)
                .build();
    }

    @Override
    public void logout(final String session) {
        Assert.hasText(session, "session must not be blank");

        redisRepository.deleteById(session);
    }

    @Override
    public Boolean validateToken(final String token) {
        Assert.hasText(token, "token must not be blank");

        final Optional<Session> optionalSession = redisRepository.findById(token);
        final Boolean isValid = optionalSession
                .map(Session::getJwt)
                .map(x -> Objects.equals(x, token))
                .orElse(false);

        //Refresh session
        if (isValid) {
            optionalSession.ifPresent(os -> {
                os.setTimeout(ttl);
                redisRepository.save(os);
            });
        }
        return isValid;
    }
}