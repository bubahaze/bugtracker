package com.poludnikiewicz.bugtracker.registration;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@RestController
@RequestMapping("/api/registration")
@AllArgsConstructor
@Validated
@Tag(name = "Registration")
public class RegistrationController {

    private RegistrationService registrationService;

    @PostMapping
    @Operation(summary = "Registration form")
    public String register(@Valid @RequestBody RegistrationRequest request) {
        return registrationService.register(request);
    }

    @GetMapping(path="confirm")
    @Operation(summary = "Triggered by authentication link that confirms email address")
    public String confirm(@RequestParam("token") String token) {
        return registrationService.confirmToken(token);
    }
}
