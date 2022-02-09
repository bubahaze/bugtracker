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

    @Test
    void addComment_should_invoke_findById_of_BugRepository_if_bug_exists() {
        BugCommentRequest request = mock(BugCommentRequest.class);
        Bug bug = mock(Bug.class);
        Long bugId = 1L;
        String author = "JoeKrasinski";
        when(bugRepository.findById(bugId)).thenReturn(Optional.of(bug));
        commentService.addComment(bugId, request, author);
        verify(bugRepository).findById(bugId);
    }

    @Test
    void addComment_should_throw_BugNotFoundException_if_bug_not_exist() {
        BugCommentRequest request = mock(BugCommentRequest.class);
        Long bugId = 1L;
        String author = "JoeKrasinski";

        Exception exception = assertThrows(BugNotFoundException.class, () -> commentService.addComment(bugId, request, author));
        String expectedMessage = "Bug with id " + bugId + " not found.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(commentRepository, never()).save(any(BugComment.class));
    }

    @Test
    void addComment_should_build_BugComment_from_BugCommentRequest_and_save_if_Bug_exist() {
        BugCommentRequest request = new BugCommentRequest("content of the comment");
        Bug bug = mock(Bug.class);
        Long bugId = 45L;
        String author = "Jean-Hackman";
        ArgumentCaptor<BugComment> captor = ArgumentCaptor.forClass(BugComment.class);

        when(bugRepository.findById(bugId)).thenReturn(Optional.of(bug));
        commentService.addComment(bugId, request, author);

        verify(commentRepository).save(any(BugComment.class));
        verify(commentRepository).save(captor.capture());
        String capturedAuthor = captor.getValue().getAuthor();
        String capturedContent = captor.getValue().getContent();
        Bug capturedBug = captor.getValue().getBug();

        assertEquals(author, capturedAuthor);
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
        String content = "content of the comment";
        when(commentRepository.findById(commentId)).thenReturn(Optional.of(comment));
        commentService.updateBugComment(commentId, content);
        verify(commentRepository).findById(commentId);
    }

    @Test
    void updateBugComment_should_throw_CommentNotFoundException_if_BugComment_not_exist() {
        Long commentId = 54L;
        String content = "content of the comment";
        Exception exception = assertThrows(CommentNotFoundException.class, () -> commentService.updateBugComment(commentId, content));
        String expectedMessage = "Comment with id " + commentId + " not found.";
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
        verify(commentRepository, never()).save(any(BugComment.class));
    }

    @Test
    void updateBugComment_should_save_BugComment_with_updated_content_if_BugComment_exists() {
        Bug bug = mock(Bug.class);
        BugComment comment = new BugComment(5L, "old-content", bug, "author-of-comment");
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
        commentService.sendNotificationEmailToBugReporterAndAssignee("author-of-comment", bugId, "content of comment");
        verify(bugRepository).findById(bugId);
    }

    @Test
    void sendNotificationEmailToBugReporterAndAssignee_should_throw_BugNotFoundException_if_bug_not_exist() {
        long bugId = 67L;
        Exception exception = assertThrows(BugNotFoundException.class,
                () -> commentService.sendNotificationEmailToBugReporterAndAssignee("author-of-comment", bugId,
                        "content of comment"));
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
        String content = "content-of-the-comment";

        when(bugRepository.findById(bugId)).thenReturn(Optional.of(bug));
        commentService.sendNotificationEmailToBugReporterAndAssignee("author-of-comment", bugId, content);
        verify(emailService).sendNotificationEmail(eq(assignee.getEmail()), anyString());
        verify(emailService).sendNotificationEmail(captor.capture(), captor.capture());
        String capturedEmailAddress = captor.getAllValues().get(0);
        String capturedContent = captor.getAllValues().get(1);
        String expectedEmailContent = String.format("Someone posted a comment to the bug with ID %d assigned to you." +
                " The content of a comment: %s", bugId, content);

        assertEquals(assignee.getEmail(), capturedEmailAddress);
        assertEquals(expectedEmailContent, capturedContent);
    }

    @Test
    void sendNotificationEmailToBugReporterAndAssignee_should_not_send_email_to_assignee_if_is_the_comment_author() {
        ApplicationUser assignee = new ApplicationUser("johnny", "john", "doe",
                "johndoe@gmail.com", "password");
        long bugId = 7L;
        Bug bug = Bug.builder().id(bugId).assignedStaffMember(assignee).build();
        String content = "content-of-the-comment";

        when(bugRepository.findById(bugId)).thenReturn(Optional.of(bug));
        commentService.sendNotificationEmailToBugReporterAndAssignee(assignee.getUsername(), bugId, content);
        verify(emailService, never()).sendNotificationEmail(eq(assignee.getEmail()), anyString());
    }

    @Test
    void sendNotificationEmailToBugReporterAndAssignee_should_not_send_email_to_assignee_if_is_null() {
        long bugId = 7L;
        Bug bug = Bug.builder().id(bugId).assignedStaffMember(null).build();
        String content = "content-of-the-comment";

        when(bugRepository.findById(bugId)).thenReturn(Optional.of(bug));
        commentService.sendNotificationEmailToBugReporterAndAssignee("author-of-the-comment", bugId, content);
        verify(emailService, never()).sendNotificationEmail(anyString(), anyString());
    }

    @Test
    void sendNotificationEmailToBugReporterAndAssignee_should_send_email_to_reporter_of_bug_if_bug_exist() {
        ApplicationUser reporter = new ApplicationUser("johnny", "john", "doe",
                "johndoe@gmail.com", "password");
        long bugId = 7L;
        Bug bug = Bug.builder().id(bugId).reporterOfBug(reporter).build();
        ArgumentCaptor<String> captor = ArgumentCaptor.forClass(String.class);
        String content = "content-of-the-comment";

        when(bugRepository.findById(bugId)).thenReturn(Optional.of(bug));
        commentService.sendNotificationEmailToBugReporterAndAssignee("author-of-comment", bugId, content);
        verify(emailService).sendNotificationEmail(eq(reporter.getEmail()), anyString());
        verify(emailService).sendNotificationEmail(captor.capture(), captor.capture());
        String capturedEmailAddress = captor.getAllValues().get(0);
        String capturedContent = captor.getAllValues().get(1);
        String expectedEmailContent = String.format("Someone posted a comment to the bug with ID %d reported by you." +
                " The content of a comment: %s", bugId, content);

        assertEquals(reporter.getEmail(), capturedEmailAddress);
        assertEquals(expectedEmailContent, capturedContent);
    }

    @Test
    void sendNotificationEmailToBugReporterAndAssignee_should_not_send_email_to_reporter_if_is_the_comment_author() {
        ApplicationUser reporter = new ApplicationUser("johnny", "john", "doe",
                "johndoe@gmail.com", "password");
        long bugId = 7L;
        Bug bug = Bug.builder().id(bugId).reporterOfBug(reporter).build();
        String content = "content-of-the-comment";

        when(bugRepository.findById(bugId)).thenReturn(Optional.of(bug));
        commentService.sendNotificationEmailToBugReporterAndAssignee(reporter.getUsername(), bugId, content);
        verify(emailService, never()).sendNotificationEmail(eq(reporter.getEmail()), anyString());
    }
}