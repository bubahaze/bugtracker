package com.poludnikiewicz.bugtracker.exception;

public class ApplicationUserNotFoundException extends RuntimeException{

    public ApplicationUserNotFoundException(String message) {
        super(message);
    }
}
