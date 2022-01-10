package com.poludnikiewicz.bugtracker.api;

import com.poludnikiewicz.bugtracker.bug.comment.BugComment;
import com.poludnikiewicz.bugtracker.bug.comment.BugCommentService;
import com.poludnikiewicz.bugtracker.bug.comment.dto.BugCommentRequest;
import com.poludnikiewicz.bugtracker.bug.comment.dto.BugCommentResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@AllArgsConstructor
@RestController
@Validated
@Tag(name = "Bug Comment API", description = "comments for particular bugs")
public class BugCommentController {

    private final BugCommentService service;

    @PostMapping("bugtracker/api/comment")
    public ResponseEntity<String> postBugComment(@Valid @RequestBody BugCommentRequest request, Authentication authentication) {

        String author = authentication.getName();
        service.addComment(request, author);

        return new ResponseEntity<>("Comment posted to Bug with id " + request.getBugId(), HttpStatus.CREATED);

    }

    @DeleteMapping("manage/api/comment/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteBugComment(@PathVariable Long id) {

        service.deleteBugComment(id);
    }

    @PatchMapping("manage/api/comment/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void updateBugComment(@PathVariable Long id, @RequestParam String content) {
        service.updateBugComment(id, content);
    }


}
