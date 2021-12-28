package com.poludnikiewicz.bugtracker.bug;

import com.poludnikiewicz.bugtracker.bug.dto.BugRequest;
import com.poludnikiewicz.bugtracker.exception.BugNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collection;
import java.util.List;
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

        String uniqueCode = UUID.randomUUID().toString();
        Bug bug = Bug.builder()
                .summary(request.getSummary())
                .project(request.getProject())
                .description(request.getDescription())
                .uniqueCode(uniqueCode)
                .status(BugStatus.REPORTED)
                .opSystemWhereBugOccured(request.getOpSystemWhereBugOccured())
                .usernameOfReporterOfBug(reporterUsername)
                .priority(BugPriority.UNSET)
                .build();

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
            bugToUpdate.setAssignedStaffMember(bug.getAssignedStaffMember());

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
