package com.poludnikiewicz.bugtracker.bug;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface BugRepository extends JpaRepository<Bug, Long> {

    Optional<Bug> findByUniqueCode(String uniqueCode);

    Collection<Bug> findByProjectIgnoreCaseOrderByCreationDateDesc(String project);

    @Query("select b from Bug b where b.assignedStaffMember.username = ?1")
    List<Bug> findAllBugsAssignedToPrincipal(String username);

}
