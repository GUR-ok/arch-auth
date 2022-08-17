package ru.gur.archauth.service;

import ru.gur.archauth.web.UserDto;

import java.util.UUID;

public interface AuthService {

    UUID register(UserDto userDto);

    String login(UserDto userDto);
}