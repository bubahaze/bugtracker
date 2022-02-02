package com.poludnikiewicz.bugtracker.security;

import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class ApplicationUserRoleTest {

    @Test
    void sanitizeUserRole_should_return_ApplicationUserRole_USER_when_passing_string_user() {
        ApplicationUserRole actual = ApplicationUserRole.sanitizeUserRole("user");
        ApplicationUserRole expected = ApplicationUserRole.USER;
        assertEquals(expected, actual);
    }

    @Test
    void sanitizeUserRole_should_return_ApplicationUserRole_STAFF_when_passing_string_staff() {
        ApplicationUserRole actual = ApplicationUserRole.sanitizeUserRole("stAff");
        ApplicationUserRole expected = ApplicationUserRole.STAFF;
        assertEquals(expected, actual);
    }

    @Test
    void sanitizeUserRole_should_return_ApplicationUserRole_ADMIN_when_passing_string_admin() {
        ApplicationUserRole actual = ApplicationUserRole.sanitizeUserRole("ADMIN");
        ApplicationUserRole expected = ApplicationUserRole.ADMIN;
        assertEquals(expected, actual);
    }

    @Test
    void sanitizeUserRole_should_throw_IllegalArgumentException_when_passing_null() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> ApplicationUserRole.sanitizeUserRole(null));
        assertEquals("Provided role cannot be null.", exception.getMessage());
    }

    @Test
    void sanitizeUserRole_should_throw_IllegalArgumentException_when_passing_incorrect_role() {
        Throwable exception = assertThrows(IllegalArgumentException.class, () -> ApplicationUserRole.sanitizeUserRole("director"));
        assertEquals("Provided role does not exist.", exception.getMessage());
    }
}