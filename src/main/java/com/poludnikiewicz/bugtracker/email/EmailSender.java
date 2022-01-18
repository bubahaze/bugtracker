package com.poludnikiewicz.bugtracker.email;

public interface EmailSender {

    void sendConfirmationEmail(String emailAddress, String content);

    void sendNotificationEmail(String emailAddress, String content);
}
