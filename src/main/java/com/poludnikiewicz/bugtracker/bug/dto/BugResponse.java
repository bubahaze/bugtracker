package com.poludnikiewicz.bugtracker.bug.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.poludnikiewicz.bugtracker.bug.BugPriority;
import com.poludnikiewicz.bugtracker.bug.BugStatus;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BugResponse {

    private String summary;

    private String project;

    private String description;
    @JsonFormat(pattern="yyyy/MM/dd HH:mm:ss")
    private LocalDateTime creationDate;

    @JsonFormat(pattern="yyyy/MM/dd HH:mm:ss")
    private LocalDateTime lastChangeAt;

    private String uniqueCode;

    private BugStatus status;

    //private ApplicationUser assignedStaffMember;
    private String usernameOfAssignee;

    private String opSystemWhereBugOccurred;

    private String usernameOfReporterOfBug;

    private BugPriority priority;


}
