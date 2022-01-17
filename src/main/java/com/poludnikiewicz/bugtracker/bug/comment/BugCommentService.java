package com.poludnikiewicz.bugtracker.bug.comment;

import com.poludnikiewicz.bugtracker.auth.ApplicationUser;
import com.poludnikiewicz.bugtracker.bug.Bug;
import com.poludnikiewicz.bugtracker.bug.BugRepository;
import com.poludnikiewicz.bugtracker.bug.comment.dto.BugCommentRequest;
import com.poludnikiewicz.bugtracker.email.EmailService;
import com.poludnikiewicz.bugtracker.exception.BugNotFoundException;
import com.poludnikiewicz.bugtracker.exception.CommentNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional
@AllArgsConstructor
public class BugCommentService {

    private final BugCommentRepository commentRepository;
    private final BugRepository bugRepository;
    private final EmailService emailService;


    public void addComment(BugCommentRequest request, String author) {
        Long id = request.getBugId();
        Bug bug = bugRepository.findById(id)
                .orElseThrow(() -> new BugNotFoundException("Bug with id " + id + " not found."));

        BugComment comment = BugComment.builder()
                .content(request.getContent())
                .author(author)
                .bug(bug)
                .build();

        commentRepository.save(comment);
    }

    public void deleteBugComment(Long id) {
        commentRepository.deleteById(id);
    }

    public void updateBugComment(Long commentId, String content) {
        BugComment commentToUpdate = commentRepository.findById(commentId)
                .orElseThrow(() -> new CommentNotFoundException("Comment with id " + commentId + " not found."));
       commentToUpdate.setContent(content);
       commentRepository.save(commentToUpdate);
    }

    public void sendNotificationEmailToBugReporterAndAssignee(String author, long bugId, String content) {
        Bug bug = bugRepository.findById(bugId).orElseThrow(() -> new BugNotFoundException(String.format("Bug with id %d not found.", bugId)));
        ApplicationUser assignedStaffMember = bug.getAssignedStaffMember();
        if (assignedStaffMember != null && !assignedStaffMember.getUsername().equals(author)) {
            String emailOfAssignee = bug.getAssignedStaffMember().getEmail();
            String contentForAssignee = String.format("Someone posted a comment to the bug with ID %d assigned to you. The content of a comment: %s", bugId, content);
            emailService.sendNotificationEmail(emailOfAssignee, contentForAssignee);
        }

        ApplicationUser reporterOfBug = bug.getReporterOfBug();
        if (reporterOfBug != null && !reporterOfBug.getUsername().equals(author)) {
            String emailOfReporter = reporterOfBug.getEmail();
            String contentForReporter = String.format("Someone posted a comment to the bug with ID %d assigned to you. The content of a comment: %s", bugId, content);
            emailService.sendNotificationEmail(emailOfReporter, contentForReporter);
        }
    }
}
