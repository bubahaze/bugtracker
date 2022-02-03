package com.poludnikiewicz.bugtracker.email;

import lombok.AllArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@AllArgsConstructor
public class EmailService implements EmailSender {

    private final static Logger LOGGER = LoggerFactory
            .getLogger(EmailService.class);

    private final JavaMailSender mailSender;

    @Async
    public void sendConfirmationEmail(String emailAddress, String content) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(mimeMessage, "utf-8");
            helper.setText(content, true);
            helper.setTo(emailAddress);
            helper.setSubject("Confirm your email");
            helper.setFrom("igor.poludnikiewicz@gmail.com");
            mailSender.send(mimeMessage);
        } catch (MessagingException e) {
            LOGGER.error("failed to send confirmation email", e);
            throw new IllegalStateException("failed to send confirmation email");
        }
    }

    @Async
    public void sendNotificationEmail(String emailAddress, String content) {
        SimpleMailMessage message = new SimpleMailMessage();
        try {
            message.setFrom("igor.poludnikiewicz@gmail.com");
            message.setTo(emailAddress);
            message.setSubject("Notification about recent changes to issue reported by you");
            message.setText(content);
            mailSender.send(message);
        } catch (Exception e) {
            LOGGER.error("failed to send notification email", e);
            throw new IllegalStateException("failed to send notification email");
        }
    }
}
