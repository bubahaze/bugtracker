package com.poludnikiewicz.bugtracker.bug.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BugCommentRequest {

    @NotNull(message = "Cannot post comment without valid Bug ID")
    private long bugId;
    @NotBlank(message = "Comment content cannot be blank")
    private String content;

}
