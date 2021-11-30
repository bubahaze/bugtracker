package com.poludnikiewicz.bugtracker.dao;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface BugRepository extends JpaRepository<Bug, Long> {

    //Bug findById(Long id);

    Collection<Bug> findByProjectIgnoreCaseOrderByCreationDateDesc(String project);

}
