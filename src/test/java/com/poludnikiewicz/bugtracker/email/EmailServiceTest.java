package com.poludnikiewicz.bugtracker.email;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.mail.javamail.JavaMailSender;

import static org.junit.jupiter.api.Assertions.*;

class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Test
    void send() {

    }
}