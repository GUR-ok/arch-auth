package ru.gur.archauth.web;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.gur.archauth.service.AuthService;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Collections;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
public class UserController {

    private final AuthService authService;

    @Autowired
    public UserController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping(value = "/register")
    public Map<String, UUID> register(@Valid @RequestBody UserDto userDto) {
        Map<String, UUID> ids = authService.register(userDto);

        return ids;
    }

    @PostMapping(value = "/login")
    public Map<String, String> login(@Valid @RequestBody UserDto userDto, HttpServletResponse response) {
        String token = authService.login(userDto);

        response.setHeader("x-auth-token", token);
        response.setHeader("x-jwt-token", token);
        response.setHeader("x-username", userDto.getLogin());

        return Collections.singletonMap("token", token);
    }

    /**
     * Методы для envoy фильтра Istio. Ничего не делают, созданы для проверки работы фильтра.
     * Для POST/GET/PATCH требуется создание своего маппинга.
     * Тут можно добавить проверку сессии, валидации прихраненного токена и т.д.
     */
    @GetMapping(value = "/auth/istio/**")
    public void istio(HttpServletRequest request, HttpServletResponse response) {
        log.info("Method: GET");

        response.setHeader("x-auth-token", request.getHeader("x-auth-token"));

        log.info("Token: " + request.getHeader("x-auth-token"));
    }

    @PatchMapping(value = "/auth/istio/**")
    public void istioPatch(HttpServletRequest request, HttpServletResponse response) {
        log.info("Method: PATCH");

        response.setHeader("x-auth-token", request.getHeader("x-auth-token"));

        log.info("Token: " + request.getHeader("x-auth-token"));
    }
}