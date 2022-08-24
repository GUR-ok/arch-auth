package ru.gur.archauth.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.RequestEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import ru.gur.archauth.entity.Person;
import ru.gur.archauth.exception.InvalidPasswordException;
import ru.gur.archauth.exception.UserExistsException;
import ru.gur.archauth.exception.UserNotFoundException;
import ru.gur.archauth.persistence.PersonRepository;
import ru.gur.archauth.web.UserDto;

import java.net.URI;
import java.security.KeyPair;
import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.time.temporal.TemporalUnit;
import java.util.Map;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final PersonRepository personRepository;
    private final KeyPair keyPair;
    private final RestTemplate restTemplate = new RestTemplate();

    @Value("${interaction.profiles.uri}")
    private URI profilesUri;

    @Value("${jwt.secret}")
    private String secret;

    @Override
    @Transactional
    public Map<String, UUID> register(UserDto userDto) {
        personRepository.findByLogin(userDto.getLogin())
                .ifPresent(u -> {
                    throw new UserExistsException("User with login " +
                            u.getLogin() +
                            " already exists");
                });

        final Person person = Person.builder()
                .email(userDto.getEmail())
                .login(userDto.getLogin())
                //todo: pswd must be encrypted
                .password(userDto.getPassword())
                .build();

        final RequestEntity<String> requestEntity = RequestEntity.post(profilesUri + "/profiles").body(userDto.getEmail());

        final UUID profileUUID = restTemplate.exchange(requestEntity, UUID.class).getBody();

        person.setProfileId(profileUUID);

        personRepository.save(person);

        return Map.of("profileId", person.getProfileId(),
                "userId", person.getId());
    }

    @Override
    @Transactional
    public String login(UserDto userDto) {
        final Person person = personRepository.findByLogin(userDto.getLogin())
                .orElseThrow(UserNotFoundException::new);

        if (!userDto.getPassword().equals(person.getPassword()))
            throw new InvalidPasswordException("Password invalid");

        return Jwts.builder()
                .setIssuer("http://arch-auth-service.arch-gur")
                .setSubject("user")
                .claim("kid", "gur-id")
                .claim("profileId", person.getProfileId())
                .signWith(SignatureAlgorithm.RS256, keyPair.getPrivate())
                .setExpiration(Date.valueOf(LocalDate.now().plus(15, ChronoUnit.MINUTES)))
                .compact();
    }
}