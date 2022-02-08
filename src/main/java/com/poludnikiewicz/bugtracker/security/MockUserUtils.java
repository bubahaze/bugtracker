package com.poludnikiewicz.bugtracker.security;

import com.poludnikiewicz.bugtracker.auth.ApplicationUser;

public class MockUserUtils {

    private MockUserUtils() {}


    public static ApplicationUser getMockUser(String username) {
        ApplicationUser user = new ApplicationUser("mockUser", "John", "Doe",
                "johndoe@gmail.com", "password");
        user.setEnabled(true);
        return user;
    }
}
