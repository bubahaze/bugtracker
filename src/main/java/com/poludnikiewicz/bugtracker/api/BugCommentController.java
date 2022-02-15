package com.poludnikiewicz.bugtracker.api;

import com.poludnikiewicz.bugtracker.bug.comment.BugCommentService;
import com.poludnikiewicz.bugtracker.bug.comment.dto.BugCommentRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@RestController
@Validated
@Tag(name = "Bug Comment API", description = "Comments for particular bugs")
public class BugCommentController {

    private final BugCommentService commentService;

    @PostMapping("{bugId}/comments")
    @Operation(summary = "Any user posts a comment to particular bug")
    public ResponseEntity<String> postBugComment(@PathVariable Long bugId, @Valid @RequestBody BugCommentRequest request, Authentication authentication) {

        String author = authentication.getName();
        commentService.addComment(bugId, request, author);
        commentService.sendNotificationEmailToBugReporterAndAssignee(author, bugId, request.getContent());

        return new ResponseEntity<>("Comment posted to Bug with id " + bugId, HttpStatus.CREATED);

    }

    @DeleteMapping("/manage/comments/{commentId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    @Operation(summary = "Admin or Staff member delete comment with provided ID")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBugComment(@PathVariable Long commentId) {

        commentService.deleteBugComment(commentId);
    }

    @PatchMapping("/manage/comments/{commentId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    @Operation(summary = "Admin or Staff member edit the content of comment with provided ID")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateBugComment(@PathVariable Long commentId, @NotBlank @RequestParam String content) {
        commentService.updateBugComment(commentId, content);
    }


}
