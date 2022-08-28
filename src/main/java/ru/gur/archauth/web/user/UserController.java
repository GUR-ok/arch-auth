package ru.gur.archauth.web.user;

import ru.gur.archauth.web.user.request.LoginRequest;
import ru.gur.archauth.web.user.request.RegisterRequest;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Map;
import java.util.UUID;

public interface UserController {

    Map<String, UUID> register(RegisterRequest registerRequest);

    public Map<String, String> login(LoginRequest loginRequest, HttpServletResponse response);

    void logout(HttpServletRequest request);

    void istio(HttpServletRequest request, HttpServletResponse response);

    void istioPatch(HttpServletRequest request, HttpServletResponse response);
}
