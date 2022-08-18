package ru.gur.archauth.web;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.gur.archauth.service.AuthService;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@RestController
public class UserController {

    private final AuthService authService;

    @Autowired
    public UserController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(value = "/register")
    public Map<String, UUID> register(@Valid @RequestBody UserDto userDto) {
        UUID uuid = authService.register(userDto);

        return Collections.singletonMap("userId", uuid);
    }

    @PostMapping(value = "/login")
    public Map<String, String> login(@Valid @RequestBody UserDto userDto, HttpServletResponse response) {
        String token = authService.login(userDto);

        response.setHeader("x-auth-token", token);
        response.setHeader("x-jwt-token", token);
        response.setHeader("x-username", userDto.getLogin());

        return Collections.singletonMap("token", token);
    }
}