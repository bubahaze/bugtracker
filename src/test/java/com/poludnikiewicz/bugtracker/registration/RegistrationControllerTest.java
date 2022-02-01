package com.poludnikiewicz.bugtracker.registration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poludnikiewicz.bugtracker.exception.ExceptionHandler;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.containsString;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
class RegistrationControllerTest {


    @Mock
    private RegistrationService registrationService;
    @Mock
    private RegistrationRequest registrationRequest;
    MockMvc mockMvc;
    private static final ObjectMapper mapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        this.mockMvc = MockMvcBuilders.standaloneSetup(new RegistrationController(registrationService))
                .setControllerAdvice(new ExceptionHandler()).build();
    }

    @Test
    void register_should_return_201_status_code_and_success_notification_string_upon_right_registrationRequest() throws Exception {
        RegistrationRequest registrationRequest = new RegistrationRequest("test-user", "John", "Doe",
                "johndoe@gmail.com", "password");
        when(registrationService.register(any(RegistrationRequest.class)))
                .thenReturn("User successfully registered. A confirmation e-mail has been sent to you");
        String json = mapper.writeValueAsString(registrationRequest);
        mockMvc.perform(post("/registration").contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8").content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string(containsString("User successfully registered. A confirmation e-mail has been sent to you")));
    }

    @Test
    void register_should_return_400_status_code__and_fail_notification_string_upon_register_with_too_short_username() throws Exception {
        RegistrationRequest registrationRequest = new RegistrationRequest("11", "John", "Doe",
                "johndoe@gmail.com", "password");
        String json = mapper.writeValueAsString(registrationRequest);
        mockMvc.perform(post("/registration").contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8").content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("{\"message\":\"Validation Failed\",\"details\":[\"username must contain 3-20 characters\"]}")));
    }

    @Test
    void register_should_return_400_status_code__and_fail_notification_string_upon_register_with_too_long_username() throws Exception {
        RegistrationRequest registrationRequest = new RegistrationRequest("123456789012345678901", "John", "Doe",
                "johndoe@gmail.com", "password");
        String json = mapper.writeValueAsString(registrationRequest);
        mockMvc.perform(post("/registration").contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8").content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("{\"message\":\"Validation Failed\",\"details\":[\"username must contain 3-20 characters\"]}")));
    }

    @Test
    void register_should_return_400_status_code__and_fail_notification_string_upon_register_with_blank_username() throws Exception {
        RegistrationRequest registrationRequest = new RegistrationRequest("     ", "John", "Doe",
                "johndoe@gmail.com", "password");
        String json = mapper.writeValueAsString(registrationRequest);
        mockMvc.perform(post("/registration").contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8").content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("{\"message\":\"Validation Failed\",\"details\":[\"Username must not be blank\"]}")));
    }

    @Test
    void register_should_return_400_status_code__and_fail_notification_string_upon_register_with_blank_first_name() throws Exception {
        RegistrationRequest registrationRequest = new RegistrationRequest("CorrectUsername", "    ",
                "Doe", "johndoe@gmail.com", "password");
        String json = mapper.writeValueAsString(registrationRequest);
        mockMvc.perform(post("/registration").contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8").content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("{\"message\":\"Validation Failed\",\"details\":[\"First name must not be blank\"]}")));
    }

    @Test
    void register_should_return_400_status_code__and_fail_notification_string_upon_register_with_blank_last_name() throws Exception {
        RegistrationRequest registrationRequest = new RegistrationRequest("CorrectUsername", "John", "",
                "johndoe@gmail.com", "password");
        String json = mapper.writeValueAsString(registrationRequest);
        mockMvc.perform(post("/registration").contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8").content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("{\"message\":\"Validation Failed\",\"details\":[\"Last name must not be blank\"]}")));
    }

    @Test
    void register_should_return_400_status_code__and_fail_notification_string_upon_register_with_incorrect_email() throws Exception {
        RegistrationRequest registrationRequest = new RegistrationRequest("CorrectUsername", "John", "Doe",
                "incorrectemail", "password");
        String json = mapper.writeValueAsString(registrationRequest);
        mockMvc.perform(post("/registration").contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8").content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("{\"message\":\"Validation Failed\",\"details\":[\"Look like this is not a valid email\"]}")));
    }

    @Test
    void register_should_return_400_status_code__and_fail_notification_string_upon_register_with_too_short_password() throws Exception {
        RegistrationRequest registrationRequest = new RegistrationRequest("CorrectUsername", "John",
                "Doe", "johndoe@gmail.com", "pass");
        String json = mapper.writeValueAsString(registrationRequest);
        mockMvc.perform(post("/registration").contentType(MediaType.APPLICATION_JSON)
                        .characterEncoding("utf-8").content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("{\"message\":\"Validation Failed\",\"details\":[\"Password must contain at least 8 characters\"]}")));
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
    void confirm_should_return_OK_status_code_upon_passing_correct_confirmation_token() throws Exception {
        String token = "correct-token";
        String json = mapper.writeValueAsString(token);
        when(registrationService.confirmToken(token)).thenReturn("email confirmed");
        mockMvc.perform(get("/registration/confirm?token=correct-token").content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("email confirmed")));
    }

    @Test
    void confirm_should_return_OK_status_code_upon_passing_incorrect_confirmation_token() throws Exception {
        String token = "incorrect-token";
        String json = mapper.writeValueAsString(token);
        when(registrationService.confirmToken(token)).thenThrow(new IllegalStateException("token not found"));
        mockMvc.perform(get("/registration/confirm?token=incorrect-token").content(json).accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(content().string(containsString("token not found")));
    }

    @Test
    void confirm_should_return_confirmToken_method_of_registrationService() {
        RegistrationController registrationController = new RegistrationController(registrationService);
        String token = "";
        //when
        registrationController.confirm(token);
        //then
        Mockito.verify(registrationService).confirmToken(token);

    }
}