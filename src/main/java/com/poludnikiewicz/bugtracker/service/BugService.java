package com.poludnikiewicz.bugtracker.service;

import com.poludnikiewicz.bugtracker.dao.Bug;
import com.poludnikiewicz.bugtracker.dao.BugRepository;
import com.poludnikiewicz.bugtracker.exception.BugNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@Transactional
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

    public Bug updateBug(Bug bug) {
        //sure it is enough to update a bug?
        return bugRepo.save(bug);
    }

    public void deleteBug(Long id) { //@Requestparam here ?
        bugRepo.deleteById(id);
    }

    public List<Bug> findAllBugs() {
        return bugRepo.findAll();
    }

    public Bug findById(Long id) {
        //return type Optional instead of type Bug?
        return bugRepo.findById(id)
                .orElseThrow(() -> new BugNotFoundException("Bug with id " + id + " not found"));
    }

    //UPDATE METHOD
    //QUERY METHODS
}
