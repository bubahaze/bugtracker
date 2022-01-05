package com.poludnikiewicz.bugtracker.bug.comment.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BugCommentResponse {

    private String content;
    private String author;

}
