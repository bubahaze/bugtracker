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
@JsonView(Views.General.class)
public class BugResponse {

    private long id;

    private String summary;

    private String project;

    private String description;

    @JsonFormat(pattern="yyyy/MM/dd HH:mm:ss")
    private LocalDateTime creationDate;

    @JsonFormat(pattern="yyyy/MM/dd HH:mm:ss")
    private LocalDateTime lastChangeAt;

    private String uniqueCode;

    private BugStatus status;

    private String usernameOfAssignee;

    private String opSystemWhereBugOccurred;

    private String usernameOfReporter;

    private BugPriority priority;

    @JsonView(Views.SingleBug.class)
    private List<BugCommentResponse> comments;


}
