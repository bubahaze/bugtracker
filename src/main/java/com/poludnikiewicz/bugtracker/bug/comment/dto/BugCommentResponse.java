package com.poludnikiewicz.bugtracker.bug.comment.dto;

import com.fasterxml.jackson.annotation.JsonView;
import com.poludnikiewicz.bugtracker.bug.Views;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BugCommentResponse {

    @JsonView(Views.Single.class)
    private String content;
    @JsonView(Views.Single.class)
    private String author;

}
