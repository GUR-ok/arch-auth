package ru.gur.archauth.service;

import lombok.Builder;
import lombok.Value;

@Value
@Builder
public class ProfileDto {

    String fullName;

    Integer age;
}
