package ru.gur.archauth.web.user.request;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;

@Data
@Builder
@JsonIgnoreProperties(ignoreUnknown = true)
public class RegisterRequest {

    @Email
    @NotBlank
    private String email;

    @NotBlank
    private String login;

    @NotBlank
    private String password;
}