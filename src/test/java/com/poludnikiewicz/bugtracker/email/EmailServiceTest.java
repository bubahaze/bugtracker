package com.poludnikiewicz.bugtracker.email;

import com.icegreen.greenmail.configuration.GreenMailConfiguration;
import com.icegreen.greenmail.junit5.GreenMailExtension;
import com.icegreen.greenmail.util.ServerSetupTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.RegisterExtension;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.IOException;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@SpringBootTest
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class EmailServiceTest {

    @RegisterExtension
    static GreenMailExtension greenMail = new GreenMailExtension(ServerSetupTest.SMTP)
            .withConfiguration(GreenMailConfiguration.aConfig().withUser("admin", "root"))
            .withPerMethodLifecycle(true);

    @Autowired
    EmailSender emailSender;
    String recipient = "testuser@testing.com";
    String content = "test content";
    String sender = "igor.poludnikiewicz@gmail.com";

    @Test
    void sendConfirmationEmail_should_send_confirmation_email() throws MessagingException, IOException {
        String subject = "Confirm your email";
        emailSender.sendConfirmationEmail(recipient, content);
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();

        assertThat(receivedMessages.length).isEqualTo(1);

        await().atMost(2, SECONDS).untilAsserted(() -> {
            MimeMessage receivedMessage = receivedMessages[0];
            assertThat(receivedMessage.getAllRecipients()[0].toString()).isEqualTo(recipient);
            assertThat(receivedMessage.getFrom()[0].toString()).isEqualTo(sender);
            assertThat(receivedMessage.getSubject()).isEqualTo(subject);
            assertThat(receivedMessage.getContent().toString()).contains(content);
        });
    }

    @Test
    void sendNotificationEmail_should_send_notification_email() throws MessagingException, IOException {
        String subject = "Notification about recent changes to issue reported by you";
        emailSender.sendNotificationEmail(recipient, content);
        MimeMessage[] receivedMessages = greenMail.getReceivedMessages();

        assertThat(receivedMessages.length).isEqualTo(1);

        await().atMost(2, SECONDS).untilAsserted(() -> {
            MimeMessage receivedMessage = receivedMessages[0];
            assertThat(receivedMessage.getAllRecipients()[0].toString()).isEqualTo(recipient);
            assertThat(receivedMessage.getFrom()[0].toString()).isEqualTo(sender);
            assertThat(receivedMessage.getSubject()).isEqualTo(subject);
            assertThat(receivedMessage.getContent().toString()).contains(content);
        });
    }
}
