package com.poludnikiewicz.bugtracker.bug.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonView;
import com.poludnikiewicz.bugtracker.bug.BugPriority;
import com.poludnikiewicz.bugtracker.bug.BugStatus;
import com.poludnikiewicz.bugtracker.bug.Views;
import com.poludnikiewicz.bugtracker.bug.comment.dto.BugCommentResponse;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class BugResponse {

    @JsonView(Views.General.class)
    private long id;

    @JsonView(Views.General.class)
    private String summary;

    @JsonView(Views.General.class)
    private String project;

    @JsonView(Views.General.class)
    private String description;

    @JsonFormat(pattern="yyyy/MM/dd HH:mm:ss")
    @JsonView(Views.General.class)
    private LocalDateTime creationDate;

    @JsonFormat(pattern="yyyy/MM/dd HH:mm:ss")
    @JsonView(Views.General.class)
    private LocalDateTime lastChangeAt;

    @JsonView(Views.General.class)
    private String uniqueCode;

    @JsonView(Views.General.class)
    private BugStatus status;

    @JsonView(Views.General.class)
    private String usernameOfAssignee;

    @JsonView(Views.General.class)
    private String opSystemWhereBugOccurred;

    @JsonView(Views.General.class)
    private String usernameOfReporter;

    @JsonView(Views.General.class)
    private BugPriority priority;

    @JsonView(Views.Single.class)
    private List<BugCommentResponse> comments;


}
