package com.poludnikiewicz.bugtracker.bug.comment;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BugCommentRepository extends JpaRepository<BugComment, Long> {


}
