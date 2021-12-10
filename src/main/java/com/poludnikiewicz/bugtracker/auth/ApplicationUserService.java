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


    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {

        return applicationUserDao.findByEmail(email)
                .orElseThrow(() ->new UsernameNotFoundException(email + " not found" ));
    }

    public String signUpUser(ApplicationUser user) {
        boolean userExists = applicationUserDao
                .findByEmail(user.getEmail())
                .isPresent();

        if (userExists && user.isEnabled()) {
            //TODO: check if attributes are the same ??
            //TODO: if email not confirmed send confirmation email again

            throw new IllegalStateException("Looks like someone already uses this email");
        }
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        applicationUserDao.save(user);

        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(token, LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(20), user);
        confirmationTokenService.saveConfirmationToken(confirmationToken);
        return token;
    }


    public int enableApplicationUser(String email) {
        return applicationUserDao.enableApplicationUser(email);
    }

//    public void save(User user) {
//        userRepository.save(user);
//    } uncommented because of the change from userRepository to ApplicationUserDao
}
