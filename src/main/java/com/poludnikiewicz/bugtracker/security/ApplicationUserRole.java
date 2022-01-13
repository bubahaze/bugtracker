package com.poludnikiewicz.bugtracker.security;

public enum ApplicationUserRole {
    ADMIN,
    STAFF,
    USER;


    public static ApplicationUserRole sanitizeUserRole(String role) {
        role = role.toUpperCase();
        switch (role) {
            case "ADMIN": return ApplicationUserRole.ADMIN;
            case "STAFF": return ApplicationUserRole.STAFF;
            case "USER": return ApplicationUserRole.USER;
            default: throw new IllegalArgumentException("Provided role does not exist.");
        }
    }
}
