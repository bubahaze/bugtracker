package com.poludnikiewicz.bugtracker.auth;

import com.google.common.collect.Lists;
import com.poludnikiewicz.bugtracker.security.ApplicationUserRole;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * exemplary repo that creates explicitly few users to save them in database
 */

@Repository("fake")
public class FakeApplicationUserDaoService implements ApplicationUserDao {

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public FakeApplicationUserDaoService(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public Optional<ApplicationUser> selectApplicationUserByUsername(String username) {
        return getApplicationUsers().stream()
                .filter(applicationUser -> username.equals(applicationUser.getUsername()))
                .findFirst();
    }

    private List<ApplicationUser> getApplicationUsers() {
        List<ApplicationUser> applicationUsers = Lists.newArrayList(
                new ApplicationUser(
                        "administrator",
                        passwordEncoder.encode("adminPass"),
                        ApplicationUserRole.ADMIN,
                        true,
                        true,
                        true,
                        true
                ),
                new ApplicationUser(
                        "engineer",
                        passwordEncoder.encode("engineerPass"),
                        ApplicationUserRole.STAFF,
                        true,
                        true,
                        true,
                        true
                ),
                new ApplicationUser(
                        "userOfApp",
                        passwordEncoder.encode("userPass"),
                        ApplicationUserRole.USER,
                        true,
                        true,
                        true,
                        true
                )


        );
        return applicationUsers;
    }
}
