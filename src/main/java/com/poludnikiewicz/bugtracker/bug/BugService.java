package com.poludnikiewicz.bugtracker.bug;

import com.poludnikiewicz.bugtracker.auth.ApplicationUser;
import com.poludnikiewicz.bugtracker.auth.ApplicationUserRepository;
import com.poludnikiewicz.bugtracker.bug.comment.BugComment;
import com.poludnikiewicz.bugtracker.bug.comment.dto.BugCommentResponse;
import com.poludnikiewicz.bugtracker.bug.dto.BugRequest;
import com.poludnikiewicz.bugtracker.bug.dto.BugResponse;
import com.poludnikiewicz.bugtracker.exception.BugNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@AllArgsConstructor
public class BugService {

    private final BugRepository bugRepository;

    public void addBug(BugRequest request, String reporterUsername) {

        Bug bug = Bug.builder()
                .summary(request.getSummary())
                .project(request.getProject())
                .description(request.getDescription())
                .status(BugStatus.REPORTED)
                .opSystemWhereBugOccurred(request.getOpSystemWhereBugOccurred())
                .usernameOfReporter(reporterUsername)
                .priority(BugPriority.UNSET)
                .build();

        bugRepository.save(bug);
    }

    public void updateBugByBugRequest(BugRequest bug, long id) {
        Bug bugToUpdate = findById(id);
        bugToUpdate.setSummary(bug.getSummary());
        bugToUpdate.setProject(bug.getProject());
        bugToUpdate.setDescription(bug.getDescription());
        bugToUpdate.setOpSystemWhereBugOccurred(bug.getOpSystemWhereBugOccurred());
        bugRepository.save(bugToUpdate);
    }

    public void deleteBug(Long id) {
        bugRepository.deleteById(id);
    }

    public Bug findById(Long id) {
        return bugRepository.findById(id)
                .orElseThrow(() -> new BugNotFoundException("Bug with id " + id + " not found."));
    }

    public BugResponse findBugResponseById(Long id) {
        Bug bug = bugRepository.findById(id)
                .orElseThrow(() -> new BugNotFoundException("Bug with id " + id + " not found."));
        return mapToBugResponse(bug);
    }

    public List<BugResponse> findByProject(String project) {
        return bugRepository
                .findByProjectContainingIgnoreCaseOrderByLastChangeAtDesc(project)
                .stream()
                .map(this::mapToBugResponse)
                .collect(Collectors.toList());

    }

    public List<BugResponse> findByKeyword(String keyword) {
        return bugRepository
                .findByKeyword(keyword)
                .stream()
                .map(this::mapToBugResponse)
                .collect(Collectors.toList());
    }

    public Bug saveBug(Bug bug) {
        return bugRepository.save(bug);
    }


    public List<BugResponse> findAllBugs() {
        return (bugRepository
                .findAll())
                .stream()
                .map(this::mapToBugResponse)
                .collect(Collectors.toList());
    }

    public List<BugResponse> findAllBugsAssignedToPrincipal(String username) {
        return bugRepository.findAllBugsAssignedToPrincipal(username).stream()
                .map(this::mapToBugResponse)
                .collect(Collectors.toList());
    }

    public List<BugResponse> sortBugsAccordingToKey(String key, String direction) {
        Sort.Direction sortDir = Sort.Direction.DESC;
        if (direction == null || direction.equalsIgnoreCase("ASC")) {
            sortDir = Sort.Direction.ASC;
        }
        return bugRepository.findAll(Sort.by(sortDir, key))
                .stream()
                .map(this::mapToBugResponse)
                .collect(Collectors.toList());
    }

    public List<BugResponse> findBugsByPriority(String priority) {
        BugPriority bugPriority = BugPriority.sanitizePriorityInput(priority);

        return bugRepository.findByPriority(bugPriority)
                .stream()
                .map(this::mapToBugResponse)
                .collect(Collectors.toList());
    }

    public List<BugResponse> findByReporter(String reporterUsername) {
        return bugRepository.findByUsernameOfReporter(reporterUsername)
                .stream()
                .map(this::mapToBugResponse)
                .collect(Collectors.toList());
    }

    private BugResponse mapToBugResponse(Bug bug) {
        BugResponse bugResponse = BugResponse.builder()
                .id(bug.getId())
                .summary(bug.getSummary())
                .description(bug.getDescription())
                .project(bug.getProject())
                .creationDate(bug.getCreationDate())
                .lastChangeAt(bug.getLastChangeAt())
                .status(bug.getStatus())
                .opSystemWhereBugOccurred(bug.getOpSystemWhereBugOccurred())
                .usernameOfReporter(bug.getUsernameOfReporter())
                .priority(bug.getPriority())
                .build();

        if (!bug.getBugComments().isEmpty()) {
            bugResponse.setComments(bug.getBugComments().stream()
                    .map(this::mapToBugCommentResponse)
                    .collect(Collectors.toList()));
        }
        if (bug.getAssignedStaffMember() != null) {
            ApplicationUser assignee = bug.getAssignedStaffMember();
            bugResponse.setUsernameOfAssignee(assignee.getUsername());
        }
        return bugResponse;
    }

    private BugCommentResponse mapToBugCommentResponse(BugComment comment) {
        BugCommentResponse commentResponse = new BugCommentResponse();
        commentResponse.setId(comment.getId());
        commentResponse.setAuthor(comment.getAuthor());
        commentResponse.setContent(comment.getContent());
        return commentResponse;
    }

}
