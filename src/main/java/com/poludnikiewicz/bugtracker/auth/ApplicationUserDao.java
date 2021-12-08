package com.poludnikiewicz.bugtracker.auth;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

/**
 * Very Important Interface. Enables using various implementations (like FakeAppUserDaoService).
 */

@Repository
@Transactional(readOnly = true) //?? registration tutorial 00:20:10
public interface ApplicationUserDao extends JpaRepository<ApplicationUser, Long> {

    //Optional<ApplicationUser> selectApplicationUserByUsername(String username);

    Optional<ApplicationUser> findByEmail(String email);



}
