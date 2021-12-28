package com.poludnikiewicz.bugtracker.auth;

import com.poludnikiewicz.bugtracker.security.ApplicationUserRole;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class ApplicationUserResponse {

    private String username;
    private String email;
    private ApplicationUserRole applicationUserRole;

}
