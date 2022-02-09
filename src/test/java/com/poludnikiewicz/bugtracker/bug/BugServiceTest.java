package com.poludnikiewicz.bugtracker.bug;

import com.poludnikiewicz.bugtracker.auth.ApplicationUser;
import com.poludnikiewicz.bugtracker.auth.ApplicationUserRepository;
import com.poludnikiewicz.bugtracker.bug.comment.BugComment;
import com.poludnikiewicz.bugtracker.bug.dto.BugRequest;
import com.poludnikiewicz.bugtracker.bug.dto.BugResponse;
import com.poludnikiewicz.bugtracker.exception.ApplicationUserNotFoundException;
import com.poludnikiewicz.bugtracker.exception.BugNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Collections;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BugServiceTest {

    @Mock
    BugRepository bugRepository;
    @Mock
    ApplicationUserRepository userRepository;
    @InjectMocks
    BugService bugService;
    final String reporterUsername = "bugReporter_94";
    final Long bugId = 56L;


    @Test
    void addBug_should_invoke_findByUsername_of_ApplicationUserRepository() {
        BugRequest request = mock(BugRequest.class);
        ApplicationUser reporter = mock(ApplicationUser.class);
        when(userRepository.findByUsername(reporterUsername)).thenReturn(Optional.of(reporter));
        bugService.addBug(request, reporterUsername);
        verify(userRepository).findByUsername(reporterUsername);
    }

    @Test
    void addBug_should_throw_ApplicationUserNotFoundException_if_reporter_not_exist() {
        BugRequest request = mock(BugRequest.class);

        Exception exception = assertThrows(ApplicationUserNotFoundException.class, () -> bugService.addBug(request, reporterUsername));
        String expectedMessage = "No user with username " + reporterUsername + " found.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(bugRepository, never()).save(any(Bug.class));
    }

    @Test
    void addBug_should_build_Bug_from_BugRequest_and_save_it() {
        String summary = "test-summary";
        String project = "test-project";
        String description = "test-description";
        String opSystem = "Windows10";
        BugRequest request = new BugRequest(summary, project, description, opSystem);
        ApplicationUser reporter = mock(ApplicationUser.class);
        when(userRepository.findByUsername(reporterUsername)).thenReturn(Optional.of(reporter));
        ArgumentCaptor<Bug> captor = ArgumentCaptor.forClass(Bug.class);

        bugService.addBug(request, reporterUsername);
        verify(bugRepository).save(any(Bug.class));
        verify(bugRepository).save(captor.capture());
        String capturedSummary = captor.getValue().getSummary();
        String capturedProject = captor.getValue().getProject();
        String capturedDescription = captor.getValue().getDescription();
        BugStatus capturedStatus = captor.getValue().getStatus();
        String capturedOpSystem = captor.getValue().getOpSystemWhereBugOccurred();
        BugPriority capturedPriority = captor.getValue().getPriority();
        ApplicationUser capturedReporter = captor.getValue().getReporterOfBug();

        assertEquals(summary, capturedSummary);
        assertEquals(project, capturedProject);
        assertEquals(description, capturedDescription);
        assertEquals(BugStatus.REPORTED, capturedStatus);
        assertEquals(opSystem, capturedOpSystem);
        assertEquals(BugPriority.UNSET, capturedPriority);
        assertEquals(reporter, capturedReporter);
        verify(bugRepository).save(any(Bug.class));
    }

    @Test
    void updateBugByBugRequest_should_update_and_save_Bug() {
        Bug bug = Bug.builder()
                .summary("old-summary")
                .project("old-project")
                .description("old-description")
                .opSystemWhereBugOccurred("old OS")
                .build();
        String summary = "test-summary";
        String project = "test-project";
        String description = "test-description";
        String opSystem = "Windows10";
        BugRequest request = new BugRequest(summary, project, description, opSystem);
        ArgumentCaptor<Bug> captor = ArgumentCaptor.forClass(Bug.class);

        when(bugRepository.findById(bugId)).thenReturn(Optional.of(bug));
        bugService.updateBugByBugRequest(request, bugId);
        verify(bugRepository).save(captor.capture());
        String capturedSummary = captor.getValue().getSummary();
        String capturedProject = captor.getValue().getProject();
        String capturedDescription = captor.getValue().getDescription();
        String capturedOpSystem = captor.getValue().getOpSystemWhereBugOccurred();

        assertEquals(summary, capturedSummary);
        assertEquals(project, capturedProject);
        assertEquals(description, capturedDescription);
        assertEquals(opSystem, capturedOpSystem);
        verify(bugRepository).save(bug);
    }

    @Test
    void deleteBug_should_invoke_deleteById_of_BugRepository() {
        bugService.deleteBug(bugId);
        verify(bugRepository).deleteById(bugId);
    }

    @Test
    @DisplayName("Should return findById method of BugRepository")
    void findById_should_invoke_findById_of_BugRepository_if_bug_exists() {
        Bug bug = mock(Bug.class);
        when(bugRepository.findById(bugId)).thenReturn(Optional.of(bug));
        bugService.findById(bugId);
        verify(bugRepository).findById(bugId);
    }

    @Test
    void findById_should_throw_BugNotFoundException_if_bug_not_exist() {
        Exception exception = assertThrows(BugNotFoundException.class, () -> bugService.findById(bugId));
        String expectedMessage = String.format("Bug with id %d not found", bugId);
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    void findBugResponseById_should_return_bugResponse_mapped_from_passed_bug() {
        ApplicationUser reporter = new ApplicationUser("johnny", "john", "doe",
                "johndoe@gmail.com", "password");
        ApplicationUser assignee = new ApplicationUser("mariaC", "maria", "carley",
                "carleym@gmail.com", "password");
        List<BugComment> bugComments = List.of(mock(BugComment.class), mock(BugComment.class));
        String summary = "summary";
        String project = "project";
        String description = "description";
        String opSystem = "Windows10";
        BugStatus status = BugStatus.REPORTED;
        BugPriority priority = BugPriority.P2_IMPORTANT;
        Bug bug = Bug.builder()
                .id(bugId)
                .summary(summary)
                .description(description)
                .project(project)
                .opSystemWhereBugOccurred(opSystem)
                .status(status)
                .priority(priority)
                .reporterOfBug(reporter)
                .assignedStaffMember(assignee)
                .bugComments(bugComments)
                .build();
        when(bugRepository.findById(bugId)).thenReturn(Optional.of(bug));
        BugResponse response = bugService.findBugResponseById(bugId);

        //testing the behaviour of private method mapToBugResponse(Bug bug)
        assertEquals(bugId, response.getId());
        assertEquals(summary, response.getSummary());
        assertEquals(description, response.getDescription());
        assertEquals(project, response.getProject());
        assertEquals(bug.getCreationDate(), response.getCreationDate());
        assertEquals(bug.getLastChangeAt(), response.getLastChangeAt());
        assertEquals(opSystem, response.getOpSystemWhereBugOccurred());
        assertEquals(status, response.getStatus());
        assertEquals(priority, response.getPriority());
        assertEquals(reporter.getUsername(), response.getUsernameOfReporter());
        assertEquals(assignee.getUsername(), response.getUsernameOfAssignee());
        assertEquals(bugComments.size(), response.getNumberOfComments());
        assertEquals(bugComments.size(), response.getComments().size());
    }

    @Test
    void findByProject_should_return_List_of_BugResponses_with_searched_project_name() {
        ApplicationUser reporter = new ApplicationUser("johnny", "john", "doe",
                "johndoe@gmail.com", "password");
        String project = "some project";
        Bug bug1 = Bug.builder().project(project).reporterOfBug(reporter).bugComments(Collections.emptyList()).build();
        Bug bug2 = Bug.builder().project(project).reporterOfBug(reporter).bugComments(Collections.emptyList()).build();
        Bug bug3 = Bug.builder().project(project).reporterOfBug(reporter).bugComments(Collections.emptyList()).build();
        List<Bug> bugs = List.of(bug1, bug2, bug3);

        when(bugRepository.findByProjectContainingIgnoreCaseOrderByLastChangeAtDesc(project)).thenReturn(bugs);
        List<BugResponse> bugResponsesByProject = bugService.findByProject(project);

        assertEquals(bugs.size(), bugResponsesByProject.size());
        //TODO: testing mapToBugCommentResponse private method here??
    }

    @Test
    void findAllBugs() {
        bugService.findAllBugs();
        verify(bugRepository).findAll();
    }
}