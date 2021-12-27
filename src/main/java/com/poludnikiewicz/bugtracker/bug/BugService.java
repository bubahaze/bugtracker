package com.poludnikiewicz.bugtracker.bug;

import com.poludnikiewicz.bugtracker.bug.Bug;
import com.poludnikiewicz.bugtracker.bug.BugRepository;
import com.poludnikiewicz.bugtracker.exception.BugNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
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

    public String addBug(BugRequest request, String reporterUsername) {

        //TODO: build Bug through factory not through overbose constructor and rewrite test for this method

        String uniqueCode = UUID.randomUUID().toString();
        Bug bug = new Bug(request.getSummary(), request.getProject(), request.getDescription(),
                uniqueCode, BugStatus.REPORTED,
                request.getOpSystemWhereBugOccured(), reporterUsername, BugPriority.UNSET);
        bugRepo.save(bug);
        return uniqueCode;
    }

    public Bug updateBug(Bug bug, long id) {
        Bug bugToUpdate = findById(id);
            bugToUpdate.setSummary(bug.getSummary());
            bugToUpdate.setProject(bug.getProject());
            bugToUpdate.setDescription(bug.getDescription());
            bugToUpdate.setOpSystemWhereBugOccured(bug.getOpSystemWhereBugOccured());
            bugToUpdate.setPriority(bug.getPriority());
            bugToUpdate.setStatus(bug.getStatus());

        return bugRepo.save(bugToUpdate);
    }

    public void deleteBug(Long id) {
        bugRepo.deleteById(id);
    }

    public List<Bug> findAllBugs() {
        return bugRepo.findAll();
    }

    public Bug findById(Long id) {
        return bugRepo.findById(id)
                .orElseThrow(() -> new BugNotFoundException("Bug with id " + id + " not found."));
    }

    public Collection<Bug> findByProject(String project) {
        return bugRepo.findByProjectIgnoreCaseOrderByCreationDateDesc(project);

    }

    public Bug saveBug(Bug bug) {
        return bugRepo.save(bug);
    }

    public Bug findByUniqueCode(String uniqueCode) {
        return bugRepo.findByUniqueCode(uniqueCode)
                .orElseThrow(() -> new BugNotFoundException("Bug with unique code " + uniqueCode + " not found."));
    }

    //TODO: QUERY METHODS
}
