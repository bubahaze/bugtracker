package com.poludnikiewicz.bugtracker.bug.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BugCommentRequest {

    @NotBlank(message = "Comment content cannot be blank")
    private String content;

}
