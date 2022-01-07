package com.poludnikiewicz.bugtracker.bug.comment.dto;

import com.fasterxml.jackson.annotation.JsonView;
import com.poludnikiewicz.bugtracker.bug.Views;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonView(Views.Single.class)
public class BugCommentResponse {

    private UUID id;
    private String content;
    private String author;

}
