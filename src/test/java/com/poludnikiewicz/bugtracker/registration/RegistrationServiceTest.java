package com.poludnikiewicz.bugtracker.registration;

import com.poludnikiewicz.bugtracker.auth.ApplicationUser;
import com.poludnikiewicz.bugtracker.auth.ApplicationUserService;
import com.poludnikiewicz.bugtracker.email.EmailSender;
import com.poludnikiewicz.bugtracker.registration.token.ConfirmationToken;
import com.poludnikiewicz.bugtracker.registration.token.ConfirmationTokenService;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@ExtendWith(MockitoExtension.class)
class RegistrationServiceTest {

    @Mock
    private RegistrationRequest registrationRequest;
    @Mock
    private ApplicationUserService applicationUserService;
    @Mock
    private ConfirmationTokenService confirmationTokenService;
    @Mock
    private EmailSender emailSender;
    @InjectMocks
    private RegistrationService registrationService;


    @Test
    void register_should_invoke_signUpUser_of_ApplicationUserService_class_upon_providing_RegistrationRequest() {
        registrationService.register(registrationRequest);
        verify(applicationUserService).signUpUser(new ApplicationUser(
                registrationRequest.getUsername(),
                registrationRequest.getFirstName(),
                registrationRequest.getLastName(),
                registrationRequest.getEmail(),
                registrationRequest.getPassword()));
    }

    @Test
    void register_should_invoke_sendConfirmationEmail_of_EmailSender() {
        registrationService.register(registrationRequest);
        verify(emailSender).sendConfirmationEmail(eq(registrationRequest.getEmail()), anyString());
    }

    @Test
    void register_should_return_string_if_success() {
        String expected = "User successfully registered. A confirmation e-mail has been sent to you";
        String actual = registrationService.register(registrationRequest);
        assertEquals(expected, actual);
    }

    @Test
    void confirmToken_should_throw_exception_when_providing_wrong_token() {
        String token = anyString();
        assertThatThrownBy(() -> registrationService.confirmToken(token)).isInstanceOf(IllegalStateException.class)
               .hasMessageContaining("token not found");
        verify(confirmationTokenService, never()).setConfirmedAt(token);
        verify(applicationUserService, never()).enableApplicationUser(any());

    }

    @Nested
    class RegistrationServiceTestNested {

        ConfirmationToken confirmationToken;
        String token;

        @BeforeEach
        void setup() {
            ApplicationUser user = mock(ApplicationUser.class);
            token = UUID.randomUUID().toString();
            confirmationToken = new ConfirmationToken(token, LocalDateTime.now(),
                    LocalDateTime.now().plusMinutes(20), user);
            confirmationTokenService.saveConfirmationToken(confirmationToken);
            when(confirmationTokenService.getToken(token)).thenReturn(Optional.of(confirmationToken));
        }


        @Test
        void confirmToken_should_throw_exception_when_email_is_already_confirmed() {
            confirmationToken.setConfirmedAt(LocalDateTime.now());

            assertThatThrownBy(() -> registrationService.confirmToken(token)).isInstanceOf(IllegalStateException.class)
                    .hasMessage("email already confirmed");
            verify(confirmationTokenService, never()).setConfirmedAt(token);
            verify(applicationUserService, never()).enableApplicationUser(any());

        }

        @Test
        void confirmToken_should_throw_exception_when_token_has_expired() {
            confirmationToken.setExpiresAt(LocalDateTime.now().minusMinutes(2));
            assertThatThrownBy(() -> registrationService.confirmToken(token)).isInstanceOf(IllegalStateException.class)
                    .hasMessage("token expired");
            verify(confirmationTokenService, never()).setConfirmedAt(token);
            verify(applicationUserService, never()).enableApplicationUser(any());

        }

        @Test
        void confirmToken_should_invoke_setConfirmedAt_of_ConfirmationTokenService_class() {
            registrationService.confirmToken(token);
            verify(confirmationTokenService).setConfirmedAt(token);
        }

        @Test
        void confirmToken_should_invoke_enableApplicationUser_of_ApplicationUserService_class() {
            registrationService.confirmToken(token);
            verify(applicationUserService).enableApplicationUser(confirmationToken.getApplicationUser().getEmail());

        }

        @Test
        void confirmToken_should_return_string() {
            String expected = "email confirmed";
            String actual = registrationService.confirmToken(token);
            assertEquals(expected, actual);
        }

    }
}