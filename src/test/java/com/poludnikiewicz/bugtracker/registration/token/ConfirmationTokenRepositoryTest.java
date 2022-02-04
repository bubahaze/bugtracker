package com.poludnikiewicz.bugtracker.registration.token;

import com.poludnikiewicz.bugtracker.auth.ApplicationUser;
import com.poludnikiewicz.bugtracker.auth.ApplicationUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@ExtendWith(MockitoExtension.class)
class ConfirmationTokenRepositoryTest {

    @Autowired
    ConfirmationTokenRepository confirmationTokenRepository;
    @Autowired
    ApplicationUserRepository userRepository;

    @Test
    void updateConfirmedAt() {
        ApplicationUser user = new ApplicationUser("johnny", "john", "doe", "johndoe@gmail.com", "password");
        userRepository.save(user);
        ConfirmationToken token = new ConfirmationToken("123456789", LocalDateTime.now(), LocalDateTime.now().plusMinutes(20), user);
       confirmationTokenRepository.save(token);
        LocalDateTime confirmedAt = LocalDateTime.now().plusMinutes(2);
        confirmationTokenRepository.updateConfirmedAt("123456789", confirmedAt);

        LocalDateTime actual = token.getConfirmedAt();

        assertEquals(confirmedAt, actual);

        /*
        try to find info about Entity Manager or:
        If you are looking to load your full application configuration, but use an embedded database,
         you should consider @SpringBootTest combined with @AutoConfigureTestDatabase rather than this annotation.??
         */
    }
}