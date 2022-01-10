package com.poludnikiewicz.bugtracker.bug.comment.dto;

import com.fasterxml.jackson.annotation.JsonView;
import com.poludnikiewicz.bugtracker.bug.Views;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonView(Views.SingleBug.class)
public class BugCommentResponse {

    @JsonView(Views.StaffSingleBug.class)
    private Long id;
    private String content;
    private String author;

}
