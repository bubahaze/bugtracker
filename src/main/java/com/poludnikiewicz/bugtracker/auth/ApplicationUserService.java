package com.poludnikiewicz.bugtracker.auth;

import com.poludnikiewicz.bugtracker.registration.token.ConfirmationToken;
import com.poludnikiewicz.bugtracker.registration.token.ConfirmationTokenService;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@AllArgsConstructor
public class ApplicationUserService implements UserDetailsService {

    private final ApplicationUserDao applicationUserDao;
    private final PasswordEncoder passwordEncoder;
    private final ConfirmationTokenService confirmationTokenService;


    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        return applicationUserDao.findByUsername(username)
                .orElseThrow(() ->new UsernameNotFoundException(username + " not found" ));
    }

    public String signUpUser(ApplicationUser user) {
        boolean userExists = applicationUserDao
                .findByEmail(user.getEmail())
                .isPresent();

        boolean usernameExists = applicationUserDao.findByUsername(user.getUsername()).isPresent();

        if (usernameExists) {
            throw new IllegalStateException("This username is taken. Try another one.");
        }

        if (userExists && user.isEnabled()) {
            //TODO: check if attributes are the same ??


            throw new IllegalStateException("Someone already uses this email");
        } else if (userExists && !user.isEnabled()) {
            //TODO: if email not confirmed send confirmation email again
            throw new IllegalStateException("Email already registered");
        }
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        applicationUserDao.save(user);

        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(token, LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(30), user);
        confirmationTokenService.saveConfirmationToken(confirmationToken);
        return token;
    }


    public int enableApplicationUser(String email) {
        return applicationUserDao.enableApplicationUser(email);
    }
}
