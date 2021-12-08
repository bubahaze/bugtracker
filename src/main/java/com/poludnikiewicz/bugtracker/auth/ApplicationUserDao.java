package com.poludnikiewicz.bugtracker.auth;

import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Very Important Interface. Enables using various implementations (like FakeAppUserDaoService).
 */

@Repository
//@Transactional(readOnly = true) ?? registration tutorial 00:20:10
public interface ApplicationUserDao {

    Optional<ApplicationUser> selectApplicationUserByUsername(String username);
}
