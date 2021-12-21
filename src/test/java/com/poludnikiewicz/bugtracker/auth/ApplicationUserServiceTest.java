package com.poludnikiewicz.bugtracker.auth;

import com.poludnikiewicz.bugtracker.registration.token.ConfirmationTokenService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

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
        assertThrows(UsernameNotFoundException.class, () -> userService.loadUserByUsername(username),
                () -> username + " not found");
    }

    @Test
    @DisplayName("Should throw IllegalStateException when username exists")
    void signUpUser_1() {
        ApplicationUser user = mock(ApplicationUser.class);
        when(user.getUsername()).thenReturn(notNull());
        //when(applicationUserDao.findByUsername(user.getUsername()).isPresent()).thenReturn(true);
        assertThrows(IllegalStateException.class, () -> userService.signUpUser(user),
                () -> "This username is taken. Try another one.");


        //TODO
    }

    @Test
    @DisplayName("Should return enableApplicationUser method of ApplicationUserDao")
    void enableApplicationUser() {
        String email = "email@email.com";
        userService.enableApplicationUser(email);
        verify(applicationUserDao).enableApplicationUser(email);
    }
}