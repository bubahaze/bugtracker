package com.poludnikiewicz.bugtracker.auth;

import com.poludnikiewicz.bugtracker.exception.ApplicationUserNotFoundException;
import com.poludnikiewicz.bugtracker.exception.BugNotFoundException;
import com.poludnikiewicz.bugtracker.registration.token.ConfirmationToken;
import com.poludnikiewicz.bugtracker.registration.token.ConfirmationTokenService;
import com.poludnikiewicz.bugtracker.security.ApplicationUserRole;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ApplicationUserServiceTest {

    @Mock
    private ApplicationUserRepository applicationUserRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private ConfirmationTokenService confirmationTokenService;
    @InjectMocks
    private ApplicationUserService userService;
    final String username = "test-username";

    @Test
    void loadUserByUsername_should_invoke_findByUsername_of_ApplicationUserRepository() {
        ApplicationUser user = mock(ApplicationUser.class);

        when(applicationUserRepository.findByUsername(username)).thenReturn(Optional.of(user));
        userService.loadUserByUsername(username);

        verify(applicationUserRepository).findByUsername(username);
    }

    @Test
    void loadUserByUsername_should_throw_UsernameNotFoundException_if_no_user_has_provided_username() {
        Exception exception = assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(username));
        String expectedMessage = username + " not found";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void signUpUser_should_throw_IllegalStateException_if_username_exists() {
        ApplicationUser user = mock(ApplicationUser.class);

        when(applicationUserRepository.findByUsername(user.getUsername())).thenReturn(Optional.of(user));
        Exception exception = assertThrows(IllegalStateException.class, () -> userService.signUpUser(user));
        String expectedMessage = "This username is taken. Try another one.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(applicationUserRepository, never()).save(user);
    }

    @Test
    void signUpUser_should_throw_IllegalStateException_if_email_already_registered() {
        ApplicationUser user = mock(ApplicationUser.class);

        when(applicationUserRepository.findByEmail(user.getEmail())).thenReturn(Optional.of(user));
        Exception exception = assertThrows(IllegalStateException.class, () -> userService.signUpUser(user));
        String expectedMessage = "Email already registered";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(applicationUserRepository, never()).save(user);
    }

    @Test
    void signUpUser_should_invoke_encode_of_PasswordEncoder() {
        ApplicationUser user = mock(ApplicationUser.class);
        userService.signUpUser(user);
        verify(passwordEncoder).encode(user.getPassword());
    }

    @Test
    void signUpUser_should_invoke_save_of_ApplicationUserRepository() {
        ApplicationUser user = mock(ApplicationUser.class);
        userService.signUpUser(user);
        verify(applicationUserRepository).save(user);
    }

    @Test
    void signUpUser_should_invoke_saveConfirmationToken_of_Confirmation_TokenService() {
        ApplicationUser user = mock(ApplicationUser.class);
        userService.signUpUser(user);
        verify(confirmationTokenService).saveConfirmationToken(any(ConfirmationToken.class));
    }

    @Test
    void enableApplicationUser_should_invoke_enableApplicationUser_of_ApplicationUserRepository() {
        String email = "email@email.com";
        userService.enableApplicationUser(email);

        verify(applicationUserRepository).enableApplicationUser(email);
    }

    @Test
    void saveApplicationUser_should_invoke_save_of_ApplicationUserRepository() {
        ApplicationUser user = mock(ApplicationUser.class);
        userService.saveApplicationUser(user);

        verify(applicationUserRepository).save(user);
    }

    @Test
    void deleteApplicationUserByUsername_should_invoke_deleteById_of_ApplicationUserRepository_if_user_exists() {
        ApplicationUser user = new ApplicationUser("johnny", "john", "doe",
                "johndoe@gmail.com", "password");
        when(applicationUserRepository.findByUsername(username)).thenReturn(Optional.of(user));
        userService.deleteApplicationUserByUsername(username);
        verify(applicationUserRepository).deleteById(user.getId());
    }

    @Test
    void deleteApplicationUserByUsername_should_throw_ApplicationUserNotFoundException_if_user_not_exist() {
        Exception exception = assertThrows(ApplicationUserNotFoundException.class,
                () -> userService.deleteApplicationUserByUsername(username));
        String expectedMessage = String.format("User with username %s not found.", username);
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(applicationUserRepository, never()).deleteById(anyLong());
    }

    @Test
    void findApplicationUserResponseByUsername_should_return_ApplicationUser_with_provided_username() {
        ApplicationUser user = mock(ApplicationUser.class);
        when(applicationUserRepository.findByUsername(username)).thenReturn(Optional.of(user));
        userService.findApplicationUserResponseByUsername(username);

        verify(applicationUserRepository).findByUsername(username);
    }

    @Test
    void findApplicationUserResponseByUsername_should_throw_ApplicationUserNotFoundException_if_no_user_with_provided_username_exist() {
        Exception exception = assertThrows(ApplicationUserNotFoundException.class,
                () -> userService.findApplicationUserResponseByUsername(username));
        String expectedMessage = String.format("User with username %s not found.", username);
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }
}