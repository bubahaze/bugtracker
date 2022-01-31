package com.poludnikiewicz.bugtracker.registration;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.poludnikiewicz.bugtracker.auth.ApplicationUser;
import com.poludnikiewicz.bugtracker.exception.ExceptionHandler;
import com.poludnikiewicz.bugtracker.security.MockUserUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
class RegistrationControllerTest {


    MockMvc mockMvc;
    @Mock
    private RegistrationService registrationService;
    @Mock
    private RegistrationRequest registrationRequest;

    private static final ApplicationUser user = MockUserUtils.getMockUser("mockUser");
    private static final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(new RegistrationController(registrationService))
                .setControllerAdvice(new ExceptionHandler()).build();
    }

    @Test
    void register_should_return_201_status_code_and_success_notification_string_upon_right_registrationRequest() throws Exception {
        RegistrationRequest registrationRequest = new RegistrationRequest("test-user", "John", "Doe", "johndoe@gmail.com", "password");
        Mockito.when(registrationService.register(any(RegistrationRequest.class))).thenReturn("User successfully registered. A confirmation e-mail has been sent to you");
        String json = mapper.writeValueAsString(registrationRequest);
        mockMvc.perform(post("/registration").contentType(MediaType.APPLICATION_JSON)
                .characterEncoding("utf-8").content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("User successfully registered. A confirmation e-mail has been sent to you")));
    }

    @Test
    void register_should_return_400_status_code__and_fail_notification_string_upon_register_with_invalid_username() throws Exception {
        RegistrationRequest registrationRequest = new RegistrationRequest("11", "John", "Doe", "johndoe@gmail.com", "password");
        String json = mapper.writeValueAsString(registrationRequest);
        mockMvc.perform(post("/registration").contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8").content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("{\"message\":\"Validation Failed\",\"details\":[\"username must contain 3-20 characters\"]}")));
    }


    @Test
    void register_should_return_register_method_of_registrationService() {
        RegistrationController registrationController = new RegistrationController(registrationService);
        //when
        registrationController.register(registrationRequest);
        //then
        Mockito.verify(registrationService).register(registrationRequest);

    }

    @Test
    void should_return_confirmToken_method_of_registrationService() {
        RegistrationController registrationController = new RegistrationController(registrationService);
        String token = "";
        //when
        registrationController.confirm(token);
        //then
        Mockito.verify(registrationService).confirmToken(token);

    }
}