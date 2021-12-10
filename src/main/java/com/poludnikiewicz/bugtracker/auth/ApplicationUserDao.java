package com.poludnikiewicz.bugtracker.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Very Important Interface. Enables using various implementations (like FakeAppUserDaoService).
 */

@Repository
@Transactional(readOnly = true)
public interface ApplicationUserDao extends JpaRepository<ApplicationUser, Long> {

    //Optional<ApplicationUser> selectApplicationUserByUsername(String username);

    Optional<ApplicationUser> findByEmail(String email);

    @Transactional
    @Modifying
    @Query("UPDATE ApplicationUser a " +
            "SET a.isEnabled = TRUE WHERE a.email = ?1")
    int enableApplicationUser(String email);




}
