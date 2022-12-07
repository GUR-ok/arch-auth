package ru.gur.archauth.service.data;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class LoginData {

    String token;
}
