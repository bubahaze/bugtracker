package com.poludnikiewicz.bugtracker.registration;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class RegistrationControllerTest {

    @Mock
    private RegistrationService registrationService;
    @Mock
    private RegistrationRequest registrationRequest;

    @Test
    void should_return_register_method_of_registrationService() {
        RegistrationController registrationController = new RegistrationController(registrationService);
        //when
        registrationController.register(registrationRequest);
        //then
        Mockito.verify(registrationService.register(registrationRequest));

    }

    @Test
    void confirm() {
    }
}