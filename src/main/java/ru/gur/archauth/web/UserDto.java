package ru.gur.archauth.web;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class UserDto {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String login;

    @NotBlank
    private String password;

    private String fullName;

    private Integer age;
}