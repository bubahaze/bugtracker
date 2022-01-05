package com.poludnikiewicz.bugtracker.api;

import com.poludnikiewicz.bugtracker.bug.comment.BugComment;
import com.poludnikiewicz.bugtracker.bug.comment.BugCommentService;
import com.poludnikiewicz.bugtracker.bug.comment.dto.BugCommentRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

@AllArgsConstructor
@RestController
@RequestMapping("/bugtracker/api/comment")
@Validated
public class BugCommentController {

    private final BugCommentService service;


    @PostMapping("/")
    public ResponseEntity<String> postBugComment(@Valid @RequestBody BugCommentRequest request, Authentication authentication) {

        String author = authentication.getName();
        service.addComment(request, author);

        return new ResponseEntity<>("Comment posted to Bug with id " + request.getBugId(), HttpStatus.CREATED);

    }
    
    //TODO: delete mapping & put/patch mapping of comments (only admin/staff/author allowed)

}
