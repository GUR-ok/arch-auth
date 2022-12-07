package ru.gur.archauth.service;

import ru.gur.archauth.service.data.LoginData;
import ru.gur.archauth.web.user.request.LoginRequest;
import ru.gur.archauth.web.user.request.RegisterRequest;

import java.util.Map;
import java.util.UUID;

public interface AuthService {

    Map<String, UUID> register(RegisterRequest registerRequest);

    LoginData login(LoginRequest loginRequest);

    void logout(String session);

    Boolean validateToken(String token);
}