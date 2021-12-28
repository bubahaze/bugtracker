package com.poludnikiewicz.bugtracker.bug.dto;

import com.poludnikiewicz.bugtracker.auth.ApplicationUser;
import com.poludnikiewicz.bugtracker.bug.BugPriority;
import com.poludnikiewicz.bugtracker.bug.BugStatus;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.ToString;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class BugResponse {

    private String summary;

    private String project;

    private String description;

    private LocalDateTime creationDate;

    private LocalDateTime lastChangeAt;

    private String uniqueCode;

    private BugStatus status;

    private ApplicationUser assignedStaffMember;

    private String opSystemWhereBugOccured;

    private String usernameOfReporterOfBug;

    private BugPriority priority;


}
