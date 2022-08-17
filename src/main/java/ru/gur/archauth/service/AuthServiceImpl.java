package ru.gur.archauth.service;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.gur.archauth.entity.Person;
import ru.gur.archauth.exception.InvalidPasswordException;
import ru.gur.archauth.exception.UserExistsException;
import ru.gur.archauth.exception.UserNotFoundException;
import ru.gur.archauth.persistence.PersonRepository;
import ru.gur.archauth.web.UserDto;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final PersonRepository personRepository;
    @Value("${jwt.secret}")
    private String secret;

    @Override
    @Transactional
    public UUID register(UserDto userDto) {
        personRepository.findByLogin(userDto.getLogin())
                .ifPresent(u -> {
                    throw new UserExistsException("User with login " +
                            u.getLogin() +
                            " already exists");
                });

        final Person person = Person.builder()
                .email(userDto.getEmail())
                .login(userDto.getLogin())
                .password(userDto.getPassword())
                .build();

        personRepository.save(person);

        return person.getId();
    }

    @Override
    @Transactional
    public String login(UserDto userDto) {
        final Person person = personRepository.findByLogin(userDto.getLogin())
                .orElseThrow(UserNotFoundException::new);

        if (!userDto.getPassword().equals(person.getPassword()))
            throw new InvalidPasswordException("Password invalid");

        System.out.println("! " + secret);

        return Jwts.builder()
                .setSubject(userDto.getLogin())
                .setId(person.getId().toString())
                .signWith(SignatureAlgorithm.HS512, secret)
                .compact();
    }
}