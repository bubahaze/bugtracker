package com.poludnikiewicz.bugtracker.registration;

import com.poludnikiewicz.bugtracker.auth.ApplicationUser;
import com.poludnikiewicz.bugtracker.auth.ApplicationUserService;
import com.poludnikiewicz.bugtracker.email.EmailSender;
import com.poludnikiewicz.bugtracker.registration.token.ConfirmationToken;
import com.poludnikiewicz.bugtracker.registration.token.ConfirmationTokenRepository;
import com.poludnikiewicz.bugtracker.registration.token.ConfirmationTokenService;
import org.aspectj.lang.annotation.After;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

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
    @DisplayName("Should return string upon register")
    void register() {
        String expected = "User successfully registered. A confirmation e-mail has been sent to you";
        String actual = registrationService.register(registrationRequest);
        assertEquals(expected, actual);
    }

    @Test
    @DisplayName("Should throw exception when providing wrong token")
    void confirmToken_1() {
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
        @DisplayName("Should throw exception when email is already confirmed")
        void confirmToken_2() {
            confirmationToken.setConfirmedAt(LocalDateTime.now());

            assertThatThrownBy(() -> registrationService.confirmToken(token)).isInstanceOf(IllegalStateException.class)
                    .hasMessage("email already confirmed");
            verify(confirmationTokenService, never()).setConfirmedAt(token);
            verify(applicationUserService, never()).enableApplicationUser(any());

        }

        @Test
        @DisplayName("Should throw exception when token has expired")
        void confirmToken_3() {
            confirmationToken.setExpiresAt(LocalDateTime.now().minusMinutes(2));
            assertThatThrownBy(() -> registrationService.confirmToken(token)).isInstanceOf(IllegalStateException.class)
                    .hasMessage("token expired");
            verify(confirmationTokenService, never()).setConfirmedAt(token);
            verify(applicationUserService, never()).enableApplicationUser(any());

        }

        @Test
        @DisplayName("Should invoke setConfirmedAt method of ConfirmationTokenService class")
        void confirmToken_4() {
            registrationService.confirmToken(token);
            verify(confirmationTokenService).setConfirmedAt(token);
        }

        @Test
        @DisplayName("Should invoke enableApplicationUser method of ApplicationUserService class")
        void confirmToken_5() {
            registrationService.confirmToken(token);
            verify(applicationUserService).enableApplicationUser(confirmationToken.getApplicationUser().getEmail());

        }

        @Test
        @DisplayName("Should return string 'email confirmed'")
        void confirmToken_6() {
            String expected = "email confirmed";
            String actual = registrationService.confirmToken(token);
            assertEquals(expected, actual);
        }

    }
}