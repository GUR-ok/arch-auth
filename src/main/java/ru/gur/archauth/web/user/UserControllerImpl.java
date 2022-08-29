package ru.gur.archauth.web.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import ru.gur.archauth.exception.UnauthorizedException;
import ru.gur.archauth.service.AuthService;
import ru.gur.archauth.service.data.LoginData;
import ru.gur.archauth.web.user.request.LoginRequest;
import ru.gur.archauth.web.user.request.RegisterRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Enumeration;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
public class UserControllerImpl implements UserController {

    private final AuthService authService;

    @PostMapping(value = "/register")
    public Map<String, UUID> register(@Valid @RequestBody RegisterRequest registerRequest) {
        Map<String, UUID> ids = authService.register(registerRequest);

        return ids;
    }

    @PostMapping(value = "/login")
    public Map<String, String> login(@Valid @RequestBody LoginRequest loginRequest, HttpServletResponse response) {
        LoginData loginData = authService.login(loginRequest);

        response.setHeader("x-auth-token", loginData.getToken());
        response.setHeader("x-username", loginRequest.getLogin());

        return Map.of("token", loginData.getToken(), "session", loginData.getSession());
    }

    @PostMapping(value = "/logout")
    public void logout(HttpServletRequest request) {
        authService.logout(request.getHeader("x-auth-session"));
    }

    /**
     * Методы для envoy фильтра Istio.
     * Для POST/GET/PATCH требуется создание своего маппинга.
     * Тут можно добавить проверку сессии, валидации прихраненного токена и т.д.
     */
    @GetMapping(value = "/auth/istio/**")
    public void istio(HttpServletRequest request, HttpServletResponse response) {
        log.info("Method: GET");
        validate(request, response);
    }

    @PatchMapping(value = "/auth/istio/**")
    public void istioPatch(HttpServletRequest request, HttpServletResponse response) {
        log.info("Method: PATCH");
        validate(request, response);
    }

    @PostMapping(value = "/auth/istio/**")
    public void istioPost(HttpServletRequest request, HttpServletResponse response) {
        log.info("Method: POST");
        validate(request, response);
    }

    private void validate(final HttpServletRequest request, final HttpServletResponse response) {
        log.info("Header x-auth-token " + request.getHeader("x-auth-token"));
        log.info("Header x-auth-session " + request.getHeader("x-auth-session"));
        Enumeration headerNames = request.getHeaderNames();
        while (headerNames.hasMoreElements()) {
            String key = (String) headerNames.nextElement();
            String value = request.getHeader(key);
            log.info(key + " " + value);
        }

        if (authService.validateToken(request.getHeader("x-auth-token"), request.getHeader("x-auth-session"))) {
            log.info("Token: is active!");
            //Фильтр добавляет хедер, который потом передается в микросервисы. Хедер д.б. разрешен в EnvoyFilter
            //            authorization_response:
            //              allowed_upstream_headers:
            //                  patterns:
            //                    - exact: "x-auth-token"
            //                    - exact: "x-custom"
            response.setHeader("x-custom", "test");
        } else {
            log.info("Token: is revoked or invalid!");
            throw new UnauthorizedException("Session invalid!");
        }
    }
}