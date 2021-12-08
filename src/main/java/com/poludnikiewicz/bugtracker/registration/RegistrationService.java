package com.poludnikiewicz.bugtracker.registration;

import com.poludnikiewicz.bugtracker.auth.ApplicationUser;
import com.poludnikiewicz.bugtracker.auth.ApplicationUserService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class RegistrationService {

    private final ApplicationUserService applicationUserService;


    public String register(RegistrationRequest request) {
        return applicationUserService.signUpUser(
                new ApplicationUser(request.getUsername(),
                        request.getFirstName(), request.getLastName(),
                        request.getEmail(), request.getPassword())
        );
    }
}
