package com.poludnikiewicz.bugtracker.bug;

import com.poludnikiewicz.bugtracker.auth.ApplicationUser;
import com.poludnikiewicz.bugtracker.bug.dto.BugRequest;
import com.poludnikiewicz.bugtracker.bug.dto.BugResponse;
import com.poludnikiewicz.bugtracker.exception.BugNotFoundException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

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
                .opSystemWhereBugOccurred(request.getOpSystemWhereBugOccurred())
                .usernameOfReporter(reporterUsername)
                .priority(BugPriority.UNSET)
                .build();

        bugRepo.save(bug);
        return uniqueCode;
    }

    public void updateBugByBugRequest(BugRequest bug, long id) {
        Bug bugToUpdate = findById(id);
        bugToUpdate.setSummary(bug.getSummary());
        bugToUpdate.setProject(bug.getProject());
        bugToUpdate.setDescription(bug.getDescription());
        bugToUpdate.setOpSystemWhereBugOccurred(bug.getOpSystemWhereBugOccurred());
        bugRepo.save(bugToUpdate);
    }

    public void deleteBug(Long id) {
        bugRepo.deleteById(id);
    }

    public Bug findById(Long id) {
        return bugRepo.findById(id)
                .orElseThrow(() -> new BugNotFoundException("Bug with id " + id + " not found."));
    }

    public BugResponse findBugResponseById(Long id) {
        Bug bug = bugRepo.findById(id)
                .orElseThrow(() -> new BugNotFoundException("Bug with id " + id + " not found."));
        return mapToBugResponse(bug);
    }

    public List<BugResponse> findByProject(String project) {
        return bugRepo
                .findByProjectContainingIgnoreCaseOrderByLastChangeAtDesc(project)
                .stream()
                .map(this::mapToBugResponse)
                .collect(Collectors.toList());

    }

    public List<BugResponse> findByKeyword(String keyword) {
        return bugRepo
                .findByKeyword(keyword)
                .stream()
                .map(this::mapToBugResponse)
                .collect(Collectors.toList());
    }

    public Bug saveBug(Bug bug) {
        return bugRepo.save(bug);
    }

    public BugResponse findByUniqueCode(String uniqueCode) {
        Bug bug = bugRepo.findByUniqueCode(uniqueCode)
                .orElseThrow(() -> new BugNotFoundException("Bug with unique code " + uniqueCode + " not found."));
            return mapToBugResponse(bug);
    }

    public List<BugResponse> findAllBugs() {
        return (bugRepo
                .findAll())
                .stream()
                .map(this::mapToBugResponse)
                .collect(Collectors.toList());
    }

    public List<BugResponse> findAllBugsAssignedToPrincipal(String username) {
        return bugRepo.findAllBugsAssignedToPrincipal(username).stream()
                .map(this::mapToBugResponse)
                .collect(Collectors.toList());
    }

    public List<BugResponse> sortBugsAccordingToKey(String key, String direction) {
        Sort.Direction sortDir = Sort.Direction.DESC;
        if (direction == null || direction.equalsIgnoreCase("ASC")) {
            sortDir = Sort.Direction.ASC;
        }
                return bugRepo.findAll(Sort.by(sortDir, key))
                .stream()
                .map(this::mapToBugResponse)
                .collect(Collectors.toList());
    }

    public List<BugResponse> findBugsByPriority(String priority) {
        BugPriority bugPriority = sanitize(priority);

        return bugRepo.findByPriority(bugPriority)
                .stream()
                .map(this::mapToBugResponse)
                .collect(Collectors.toList());
    }

    private BugPriority sanitize(String priority) {
        priority = priority.toUpperCase();
        switch (priority) {
            case "P1": return BugPriority.P1_CRITICAL;
            case "CRITICAL": return BugPriority.P1_CRITICAL;
            case "P1_CRITICAL": return BugPriority.P1_CRITICAL;
            case "P2": return BugPriority.P2_IMPORTANT;
            case "IMPORTANT": return BugPriority.P2_IMPORTANT;
            case "P2_IMPORTANT": return BugPriority.P2_IMPORTANT;
            case "P3": return BugPriority.P3_NORMAL;
            case "NORMAL": return BugPriority.P3_NORMAL;
            case "P3_NORMAL": return BugPriority.P3_NORMAL;
            case "P4": return BugPriority.P4_MARGINAL;
            case "MARGINAL": return BugPriority.P4_MARGINAL;
            case "P4_MARGINAL": return BugPriority.P4_MARGINAL;
            case "P5": return BugPriority.P5_REDUNTANT;
            case "REDUNTANT": return BugPriority.P5_REDUNTANT;
            case "P5_REDUNTANT": return BugPriority.P5_REDUNTANT;
            default : return BugPriority.UNSET;
        }
    }

    private BugResponse mapToBugResponse(Bug bug) {
        BugResponse bugResponse = new BugResponse();
        bugResponse.setSummary(bug.getSummary());
        bugResponse.setDescription(bug.getDescription());
        bugResponse.setProject(bug.getProject());
        bugResponse.setCreationDate(bug.getCreationDate());
        bugResponse.setLastChangeAt(bug.getLastChangeAt());
        bugResponse.setUniqueCode(bug.getUniqueCode());
        bugResponse.setStatus(bug.getStatus());
        bugResponse.setOpSystemWhereBugOccurred(bug.getOpSystemWhereBugOccurred());
        bugResponse.setUsernameOfReporter(bug.getUsernameOfReporter());
        bugResponse.setPriority(bug.getPriority());
        if (bug.getAssignedStaffMember() != null) {
            ApplicationUser assignee = bug.getAssignedStaffMember();
            bugResponse.setUsernameOfAssignee(assignee.getUsername());
        }
        return bugResponse;
    }

}
