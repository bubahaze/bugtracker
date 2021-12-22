package com.poludnikiewicz.bugtracker.auth;

import com.poludnikiewicz.bugtracker.registration.token.ConfirmationToken;
import com.poludnikiewicz.bugtracker.registration.token.ConfirmationTokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import static org.assertj.core.api.Assertions.*;

import javax.mail.MessagingException;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ApplicationUserServiceTest {

    @Mock
    private ApplicationUserDao applicationUserDao;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ConfirmationTokenService confirmationTokenService;
    @InjectMocks
    private ApplicationUserService userService;

    @Test
    @DisplayName("Should return findByUsername method of ApplicationUserDao")
    void loadUserByUsername_1() {
        ApplicationUser user = mock(ApplicationUser.class);
        String username = "username";
        when(applicationUserDao.findByUsername(username)).thenReturn(Optional.of(user));
        userService.loadUserByUsername(username);
        verify(applicationUserDao).findByUsername(username);
    }

    @Test
    @DisplayName("Should throw UsernameNotFoundException")
    void loadUserByUsername_2() {
        String username = "username";
        Exception exception = assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(username));
        String expectedMessage = username + " not found";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    @DisplayName("Should throw IllegalStateException when username exists")
    void signUpUser_1() {
        ApplicationUser user = mock(ApplicationUser.class);
        when(applicationUserDao.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        Exception exception = assertThrows(IllegalStateException.class, () -> userService.signUpUser(user));
        String expectedMessage = "This username is taken. Try another one.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(applicationUserDao, never()).save(user);
    }

    @Test
    @DisplayName("Should throw IllegalStateException when email already registered")
    void signUpUser_2() {
        ApplicationUser user = mock(ApplicationUser.class);
        when(applicationUserDao.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        when(user.isEnabled()).thenReturn(true);
        Exception exception = assertThrows(IllegalStateException.class, () -> userService.signUpUser(user));
        String expectedMessage = "Email already registered";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(applicationUserDao, never()).save(user);
    }

    @Test
    @DisplayName("Should verify if encode method of PasswordEncoder is invoked")
    void signUpUser_3() {
        ApplicationUser user = mock(ApplicationUser.class);
        userService.signUpUser(user);
        verify(passwordEncoder).encode(user.getPassword());
    }

    @Test
    @DisplayName("Should verify if save method of ApplicationUserDao is invoked")
    void signUpUser_4() {
        ApplicationUser user = mock(ApplicationUser.class);
        userService.signUpUser(user);
        verify(applicationUserDao).save(user);
    }

    @Test
    @DisplayName("Should verify if saveConfirmationToken method of ConfirmationTokenService is invoked")
    void signUpUser_6() {
        ApplicationUser user = mock(ApplicationUser.class);
        userService.signUpUser(user);
        verify(confirmationTokenService).saveConfirmationToken(any(ConfirmationToken.class));
    }

    @Test
    @DisplayName("Should return enableApplicationUser method of ApplicationUserDao")
    void enableApplicationUser() {
        String email = "email@email.com";
        userService.enableApplicationUser(email);
        verify(applicationUserDao).enableApplicationUser(email);
    }
}