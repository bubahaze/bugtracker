package com.poludnikiewicz.bugtracker.email;

public interface EmailSender {

    void send(String to, String email);
}
