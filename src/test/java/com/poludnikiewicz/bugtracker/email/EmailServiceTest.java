package com.poludnikiewicz.bugtracker.email;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;

import javax.mail.MessagingException;

import static org.assertj.core.api.Assertions.assertThatThrownBy;

@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @Mock
    private JavaMailSender mailSender;

    @Test
    void send() {
        //TODO: This test is broken
String to = "";
String email = "email";
EmailService service = new EmailService(mailSender);
Mockito.doThrow(MessagingException.class).when(service).send(to, email);
assertThatThrownBy(() -> service.send(to, email)).isInstanceOf(MessagingException.class).hasMessage("failed to send email");
    }
}