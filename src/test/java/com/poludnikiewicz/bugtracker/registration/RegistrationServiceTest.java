package com.poludnikiewicz.bugtracker.registration;

import com.poludnikiewicz.bugtracker.auth.ApplicationUser;
import com.poludnikiewicz.bugtracker.auth.ApplicationUserService;
import com.poludnikiewicz.bugtracker.email.EmailSender;
import com.poludnikiewicz.bugtracker.registration.token.ConfirmationToken;
import com.poludnikiewicz.bugtracker.registration.token.ConfirmationTokenRepository;
import com.poludnikiewicz.bugtracker.registration.token.ConfirmationTokenService;
import org.aspectj.lang.annotation.After;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

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


    @AfterEach
    void resetMock() {
      //  reset(registrationService);
    }

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
        //registrationService.confirmToken(token);
       // BDDMockito.given(confirmationTokenService.getToken(token)).willThrow(new IllegalStateException());
      //  BDDMockito.given(registrationService.confirmToken(token)).willThrow(new IllegalStateException());
        //when(confirmationTokenService.getToken(token)).thenThrow(IllegalStateException.class);
        //confirmationTokenService.getToken(token);
        assertThatThrownBy(() -> registrationService.confirmToken(token)).isInstanceOf(IllegalStateException.class)
               .hasMessageContaining("token not found");
        verify(confirmationTokenService, never()).setConfirmedAt(token);
        verify(applicationUserService, never()).enableApplicationUser(any());

      //  assertThrows(IllegalStateException.class, () -> confirmationTokenService.getToken(token));

    }

    @Test
    @DisplayName("Should throw exception when email is already confirmed")
    void confirmToken_2() {

        //TODO: come back after gaining more experience to write working test


        ConfirmationToken confirmationToken = mock(ConfirmationToken.class);
        ConfirmationTokenService confirmationTokenService = mock(ConfirmationTokenService.class);
        ApplicationUserService userService = mock(ApplicationUserService.class);
        ApplicationUser user = mock(ApplicationUser.class);
        BDDMockito.given(userService.signUpUser(user)).willReturn("randomized-token");
        BDDMockito.given(confirmationTokenService.getToken("randomized-token")).willReturn(Optional.of(new ConfirmationToken()));
       // BDDMockito.given(registrationService.confirmToken("randomized-token")).wi
        //  when(userService.signUpUser(user)).thenReturn("randomized-token");
        //  confirmationToken.setConfirmedAt(LocalDateTime.now());
     //   doThrow(new IllegalStateException()).when(confirmationToken).getConfirmedAt();
       // when(confirmationToken.getConfirmedAt()).thenThrow(IllegalStateException.class);
       // assertThrows(IllegalStateException.class, () -> registrationService.confirmToken(anyString()));
       assertThatThrownBy(() -> registrationService.confirmToken("randomized-token")).isInstanceOf(IllegalStateException.class)
               .hasMessage("email already confirmed");

    }

    @Test
    void confirmToken_3() {

    }
}