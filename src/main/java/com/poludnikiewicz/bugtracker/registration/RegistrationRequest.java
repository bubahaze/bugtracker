package com.poludnikiewicz.bugtracker.registration;

import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.Size;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class RegistrationRequest {
    @NotBlank
    private final String username;
    @NotBlank
    private final String firstName;
    @NotBlank
    private final String lastName;
    @Email
    @NotBlank
    private final String email;
    @NotEmpty
    @Size(min = 8)
    private final String password;
}
