package com.poludnikiewicz.bugtracker.auth;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;

import javax.persistence.EntityManager;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
class ApplicationUserRepositoryTest {

    @Autowired
    ApplicationUserRepository userRepository;
    @Autowired
    EntityManager entityManager;

    @Test
    void enableApplicationUser_should_set_isEnabled_from_default_false_to_true() {
        ApplicationUser user = new ApplicationUser("johnny", "john", "doe", "johndoe@gmail.com", "password");
        userRepository.save(user);

        userRepository.enableApplicationUser(user.getEmail());
        entityManager.refresh(user);

        assertTrue(user.isEnabled());



    }
}