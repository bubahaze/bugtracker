package com.poludnikiewicz.bugtracker.bug.comment;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface BugCommentRepository extends JpaRepository<BugComment, Long> {


    void deleteById(UUID id);
}
