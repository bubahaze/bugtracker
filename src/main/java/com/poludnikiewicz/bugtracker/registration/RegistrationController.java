package com.poludnikiewicz.bugtracker.registration;

import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/registration")
@AllArgsConstructor
@Validated
public class RegistrationController {


    private RegistrationService registrationService;

    @PostMapping
    public String register(@Valid @RequestBody RegistrationRequest request) {
        return registrationService.register(request);
    }

    @GetMapping(path="confirm")
    public String confirm(@RequestParam("token") String token) {
        return registrationService.confirmToken(token);
    }
}
