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
    @NotBlank(message = "Username must not be blank")
    @Size(min = 3, max = 20, message = "username must contain 3-20 characters")
    private final String username;
    @NotBlank(message = "First name must not be blank")
    private final String firstName;
    @NotBlank(message = "Last name must not be blank")
    private final String lastName;
    @Email(message = "Look like this is not a valid email")
    @NotBlank(message = "Email must not be blank")
    private final String email;
    @NotEmpty(message = "Password must not be empty")
    @Size(min = 8, message = "Password must contain at least 8 characters")
    private final String password;
}
