package com.poludnikiewicz.bugtracker.bug.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@NoArgsConstructor
@Data
public class BugCommentRequest {

    private long bugId;

    private String content;

    private String author;


}
