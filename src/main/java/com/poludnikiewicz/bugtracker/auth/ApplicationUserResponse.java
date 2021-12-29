package com.poludnikiewicz.bugtracker.auth;

import com.poludnikiewicz.bugtracker.security.ApplicationUserRole;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationUserResponse {

    private String username;
    private String email;
    private ApplicationUserRole applicationUserRole;

}
