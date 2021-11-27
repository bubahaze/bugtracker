package com.poludnikiewicz.bugtracker.service;

import com.poludnikiewicz.bugtracker.dao.Bug;
import com.poludnikiewicz.bugtracker.dao.BugRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class BugService {
    private final BugRepository bugRepo;

    @Autowired
    public BugService(BugRepository bugRepo) {
        this.bugRepo = bugRepo;
    }

    public Bug addBug(Bug bug) {
        //setting unique code for a bug
        bug.setUniqueCode(UUID.randomUUID().toString());
        return bugRepo.save(bug);
    }
}
