package com.poludnikiewicz.bugtracker.bug.comment;

import com.poludnikiewicz.bugtracker.auth.ApplicationUser;
import com.poludnikiewicz.bugtracker.bug.Bug;
import com.poludnikiewicz.bugtracker.bug.BugRepository;
import com.poludnikiewicz.bugtracker.bug.comment.dto.BugCommentRequest;
import com.poludnikiewicz.bugtracker.email.EmailService;
import com.poludnikiewicz.bugtracker.exception.BugNotFoundException;
import com.poludnikiewicz.bugtracker.exception.CommentNotFoundException;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BugCommentServiceTest {

    @Mock
    BugCommentRepository commentRepository;
    @Mock
    BugRepository bugRepository;
    @Mock
    EmailService emailService;
    @InjectMocks
    BugCommentService commentService;
    final String AUTHOR_OF_COMMENT = "JoeKrasinski";
    final String CONTENT_OF_COMMENT = "this is the content of the comment";

    @Test
    void addComment_should_invoke_findById_of_BugRepository_if_bug_exists() {
        BugCommentRequest request = mock(BugCommentRequest.class);
        Bug bug = mock(Bug.class);
        Long bugId = 1L;
        when(bugRepository.findById(bugId)).thenReturn(Optional.of(bug));
        commentService.addComment(bugId, request, AUTHOR_OF_COMMENT);
        verify(bugRepository).findById(bugId);
    }

    @Test
    void addComment_should_throw_BugNotFoundException_if_bug_not_exist() {
        BugCommentRequest request = mock(BugCommentRequest.class);
        Long bugId = 1L;

        Exception exception = assertThrows(BugNotFoundException.class, () -> commentService.addComment(bugId, request, AUTHOR_OF_COMMENT));
        String expectedMessage = "Bug with id " + bugId + " not found.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(commentRepository, never()).save(any(BugComment.class));
    }

    @Test
    void addComment_should_build_BugComment_from_BugCommentRequest_and_save_if_Bug_exist() {
        BugCommentRequest request = new BugCommentRequest(CONTENT_OF_COMMENT);
        Bug bug = mock(Bug.class);
        Long bugId = 45L;
        ArgumentCaptor<BugComment> captor = ArgumentCaptor.forClass(BugComment.class);

        when(bugRepository.findById(bugId)).thenReturn(Optional.of(bug));
        commentService.addComment(bugId, request, AUTHOR_OF_COMMENT);

        verify(commentRepository).save(any(BugComment.class));
        verify(commentRepository).save(captor.capture());
        String capturedAuthor = captor.getValue().getAuthor();
        String capturedContent = captor.getValue().getContent();
        Bug capturedBug = captor.getValue().getBug();

        assertEquals(AUTHOR_OF_COMMENT, capturedAuthor);
        assertEquals(request.getContent(), capturedContent);
        assertEquals(bug, capturedBug);

    }

    @Test
    void deleteBugComment_should_invoke_deleteById_of_BugCommentRepository() {
        Long commentId = 3L;
        commentService.deleteBugComment(commentId);
        verify(commentRepository).deleteById(commentId);
    }

    @Test
    void updateBugComment_should_invoke_findById_of_BugCommentRepository_if_BugComment_exists() {
        BugComment comment = mock(BugComment.class);
        Long commentId = 54L;
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        commentService.updateBugComment(commentId, CONTENT_OF_COMMENT);
        verify(commentRepository).findById(commentId);
    }

    @Test
    void updateBugComment_should_throw_CommentNotFoundException_if_BugComment_not_exist() {
        Long commentId = 54L;
        Exception exception = assertThrows(CommentNotFoundException.class, () -> commentService.updateBugComment(commentId, CONTENT_OF_COMMENT));
        String expectedMessage = "Comment with id " + commentId + " not found.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(commentRepository, never()).save(any(BugComment.class));
    }

    @Test
    void updateBugComment_should_save_BugComment_with_updated_content_if_BugComment_exists() {
        Bug bug = mock(Bug.class);
        BugComment comment = new BugComment(5L, CONTENT_OF_COMMENT, bug, AUTHOR_OF_COMMENT);
        Long commentId = 54L;
        String updatedContent = "updated content of the comment";
        ArgumentCaptor<BugComment> captor = ArgumentCaptor.forClass(BugComment.class);

        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        commentService.updateBugComment(commentId, updatedContent);

        verify(commentRepository).save(any(BugComment.class));
        verify(commentRepository).save(captor.capture());
        String capturedContent = captor.getValue().getContent();
        assertEquals(updatedContent, capturedContent);
    }

    @Test
    void sendNotificationEmailToBugReporterAndAssignee_should_invoke_findById_of_BugRepository_if_bug_exists() {
        Bug bug = mock(Bug.class);
        long bugId = 23L;
        when(bugRepository.findById(bugId)).thenReturn(Optional.of(bug));
        commentService.sendNotificationEmailToBugReporterAndAssignee(AUTHOR_OF_COMMENT, bugId, CONTENT_OF_COMMENT);
        verify(bugRepository).findById(bugId);
    }

    @Test
    void sendNotificationEmailToBugReporterAndAssignee_should_throw_BugNotFoundException_if_bug_not_exist() {
        long bugId = 67L;
        Exception exception = assertThrows(BugNotFoundException.class,
                () -> commentService.sendNotificationEmailToBugReporterAndAssignee(AUTHOR_OF_COMMENT, bugId,
                        CONTENT_OF_COMMENT));
        String expectedMessage = "Bug with id " + bugId + " not found.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(emailService, never()).sendNotificationEmail(anyString(), anyString());
    }

    @Test
    void sendNotificationEmailToBugReporterAndAssignee_should_send_email_to_assignee_of_bug_if_bug_exist() {
        ApplicationUser assignee = new ApplicationUser("johnny", "john", "doe",
                "johndoe@gmail.com", "password");
        long bugId = 7L;
        Bug bug = Bug.builder().id(bugId).assignedStaffMember(assignee).build();
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        when(bugRepository.findById(bugId)).thenReturn(Optional.of(bug));
        commentService.sendNotificationEmailToBugReporterAndAssignee(AUTHOR_OF_COMMENT, bugId, CONTENT_OF_COMMENT);
        verify(emailService).sendNotificationEmail(eq(assignee.getEmail()), anyString());
        verify(emailService).sendNotificationEmail(captor.capture(), captor.capture());
        String capturedEmailAddress = captor.getAllValues().get(0);
        String capturedContent = captor.getAllValues().get(1);
        String expectedEmailContent = String.format("Someone posted a comment to the bug with ID %d assigned to you." +
                " The content of a comment: %s", bugId, CONTENT_OF_COMMENT);

        assertEquals(assignee.getEmail(), capturedEmailAddress);
        assertEquals(expectedEmailContent, capturedContent);
    }

    @Test
    void sendNotificationEmailToBugReporterAndAssignee_should_not_send_email_to_assignee_if_is_the_comment_author() {
        ApplicationUser assignee = new ApplicationUser("johnny", "john", "doe",
                "johndoe@gmail.com", "password");
        long bugId = 7L;
        Bug bug = Bug.builder().id(bugId).assignedStaffMember(assignee).build();

        when(bugRepository.findById(bugId)).thenReturn(Optional.of(bug));
        commentService.sendNotificationEmailToBugReporterAndAssignee(assignee.getUsername(), bugId, CONTENT_OF_COMMENT);
        verify(emailService, never()).sendNotificationEmail(eq(assignee.getEmail()), anyString());
    }

    @Test
    void sendNotificationEmailToBugReporterAndAssignee_should_not_send_email_to_assignee_if_is_null() {
        long bugId = 7L;
        Bug bug = Bug.builder().id(bugId).assignedStaffMember(null).build();

        when(bugRepository.findById(bugId)).thenReturn(Optional.of(bug));
        commentService.sendNotificationEmailToBugReporterAndAssignee(AUTHOR_OF_COMMENT, bugId, CONTENT_OF_COMMENT);
        verify(emailService, never()).sendNotificationEmail(anyString(), anyString());
    }

    @Test
    void sendNotificationEmailToBugReporterAndAssignee_should_send_email_to_reporter_of_bug_if_bug_exist() {
        ApplicationUser reporter = new ApplicationUser("johnny", "john", "doe",
                "johndoe@gmail.com", "password");
        long bugId = 7L;
        Bug bug = Bug.builder().id(bugId).reporterOfBug(reporter).build();
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);

        when(bugRepository.findById(bugId)).thenReturn(Optional.of(bug));
        commentService.sendNotificationEmailToBugReporterAndAssignee(AUTHOR_OF_COMMENT, bugId, CONTENT_OF_COMMENT);
        verify(emailService).sendNotificationEmail(eq(reporter.getEmail()), anyString());
        verify(emailService).sendNotificationEmail(captor.capture(), captor.capture());
        String capturedEmailAddress = captor.getAllValues().get(0);
        String capturedContent = captor.getAllValues().get(1);
        String expectedEmailContent = String.format("Someone posted a comment to the bug with ID %d reported by you." +
                " The content of a comment: %s", bugId, CONTENT_OF_COMMENT);

        assertEquals(reporter.getEmail(), capturedEmailAddress);
        assertEquals(expectedEmailContent, capturedContent);
    }

    @Test
    void sendNotificationEmailToBugReporterAndAssignee_should_not_send_email_to_reporter_if_is_the_comment_author() {
        ApplicationUser reporter = new ApplicationUser("johnny", "john", "doe",
                "johndoe@gmail.com", "password");
        long bugId = 7L;
        Bug bug = Bug.builder().id(bugId).reporterOfBug(reporter).build();

        when(bugRepository.findById(bugId)).thenReturn(Optional.of(bug));
        commentService.sendNotificationEmailToBugReporterAndAssignee(reporter.getUsername(), bugId, CONTENT_OF_COMMENT);
        verify(emailService, never()).sendNotificationEmail(eq(reporter.getEmail()), anyString());
    }
}