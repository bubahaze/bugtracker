package com.poludnikiewicz.bugtracker.bug;

import com.poludnikiewicz.bugtracker.bug.Bug;
import com.poludnikiewicz.bugtracker.bug.BugRepository;
import com.poludnikiewicz.bugtracker.exception.BugNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Collection;
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
        bug.setUniqueCode(UUID.randomUUID().toString());
        return bugRepo.save(bug);
    }

    public Bug updateBug(Bug bug, Long id) {
        Bug bugToUpdate = findById(id);
        bugToUpdate.setSummary(bug.getSummary());
        bugToUpdate.setProject(bug.getProject());
        bugToUpdate.setDescription(bug.getDescription());
        bugToUpdate.setCreationDate(LocalDateTime.now());
        bugToUpdate.setStatus(bug.getStatus());
        //bugToUpdate.setAssignedStaffMember(bug.getAssignedStaffMember());

        return bugRepo.save(bugToUpdate);
    }

    public void deleteBug(Long id) { //@Requestparam here ?
        bugRepo.deleteById(id);
    }

    public List<Bug> findAllBugs() {
        return bugRepo.findAll();
    }

    public Bug findById(Long id) {
        return bugRepo.findById(id)
                .orElseThrow(() -> new BugNotFoundException("Bug with id " + id + " not found"));
    }

    public Collection<Bug> findByProject(String project) {
        return bugRepo.findByProjectIgnoreCaseOrderByCreationDateDesc(project);

    }

    //TODO: QUERY METHODS
}
