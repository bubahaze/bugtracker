package com.poludnikiewicz.bugtracker.exception;

public class BugNotFoundException extends RuntimeException {

    public BugNotFoundException(String message) {
        super(message);
    }
}
