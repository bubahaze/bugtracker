package com.poludnikiewicz.bugtracker.bug;

import com.poludnikiewicz.bugtracker.bug.dto.BugResponse;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface BugRepository extends JpaRepository<Bug, Long> {

    Optional<Bug> findByUniqueCode(String uniqueCode);

    //@Query("select b from Bug b where b.project like '%?1%'")
    List<Bug> findByProjectContainingIgnoreCaseOrderByLastChangeAtDesc(String project);

    @Query("select b from Bug b where lower(b.summary) like lower(concat('%', ?1,'%'))" +
            " or lower(b.description) like lower(concat('%', ?1,'%')) or lower(b.project) like" +
            " lower(concat('%', ?1,'%')) or lower(b.opSystemWhereBugOccurred) like lower(concat('%', ?1,'%')) " +
            "or lower(b.usernameOfReporter) like lower(concat('%', ?1,'%'))")
    List<Bug> findByKeyword(String keyword);

    @Query("select b from Bug b where b.assignedStaffMember.username = ?1")
    List<Bug> findAllBugsAssignedToPrincipal(String username);

}
