package com.poludnikiewicz.bugtracker.registration;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/registration")
@AllArgsConstructor
public class RegistrationController {

    @Autowired
    private RegistrationService registrationService;

    @PostMapping
    public String register(@Valid @RequestBody RegistrationRequest request) {
        return registrationService.register(request);

        //TODO: think about validation, providing own class emailValidator? Better
        //TODO: https://www.yawintutor.com/how-to-customize-default-error-message-using-controlleradvice-in-spring-boot-validation/
    }

    @GetMapping(path="confirm")
    public String confirm(@RequestParam("token") String token) {
        return registrationService.confirmToken(token);
    }
}
