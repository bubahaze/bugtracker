package com.poludnikiewicz.bugtracker.registration.token;

import com.poludnikiewicz.bugtracker.auth.ApplicationUser;
import com.poludnikiewicz.bugtracker.auth.ApplicationUserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

import static org.assertj.core.api.Assertions.within;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@DataJpaTest
@ExtendWith(MockitoExtension.class)
class ConfirmationTokenRepositoryTest {

    @Autowired
    ConfirmationTokenRepository confirmationTokenRepository;
    @Autowired
    ApplicationUserRepository userRepository;
    @Autowired
    EntityManager entityManager;

    @Test
    void updateConfirmedAt() {
        ApplicationUser user = new ApplicationUser("johnny", "john", "doe", "johndoe@gmail.com", "password");
        userRepository.save(user);
        ConfirmationToken token = new ConfirmationToken("123456789", LocalDateTime.now(), LocalDateTime.now().plusMinutes(20), user);
        confirmationTokenRepository.save(token);
        LocalDateTime confirmedAt = LocalDateTime.now().plusMinutes(2);
        confirmationTokenRepository.updateConfirmedAt("123456789", confirmedAt);
        entityManager.refresh(token);

        LocalDateTime actual = token.getConfirmedAt();

        assertThat(confirmedAt).isCloseTo(actual, within(1, ChronoUnit.SECONDS));
    }
}