package com.poludnikiewicz.bugtracker.bug;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface BugRepository extends JpaRepository<Bug, Long> {

    List<Bug> findByProjectContainingIgnoreCaseOrderByLastChangeAtDesc(String project);

    @Query("select b from Bug b where lower(b.summary) like lower(concat('%', ?1,'%'))" +
            " or lower(b.description) like lower(concat('%', ?1,'%')) or lower(b.project) like" +
            " lower(concat('%', ?1,'%')) or lower(b.opSystemWhereBugOccurred) like lower(concat('%', ?1,'%')) " +
            "or lower(b.reporterOfBug.username) like lower(concat('%', ?1,'%'))")
    List<Bug> findByKeyword(String keyword);

    @Query("select b from Bug b where b.assignedStaffMember.username = ?1")
    List<Bug> findAllBugsAssignedToApplicationUser(String username);

    List<Bug> findByPriority(BugPriority priority);

    @Query("select b from Bug b where b.reporterOfBug.username = ?1")
    List<Bug> findByUsernameOfReporter(String reporterUsername);
}
