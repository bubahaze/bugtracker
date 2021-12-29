package com.poludnikiewicz.bugtracker.bug.dto;

import com.poludnikiewicz.bugtracker.auth.ApplicationUser;
import com.poludnikiewicz.bugtracker.bug.BugPriority;
import com.poludnikiewicz.bugtracker.bug.BugStatus;
import com.poludnikiewicz.bugtracker.security.ApplicationUserRole;
import lombok.*;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BugResponse {

    private String summary;

    private String project;

    private String description;

    private LocalDateTime creationDate;

    private LocalDateTime lastChangeAt;

    private String uniqueCode;

    private BugStatus status;

    //private ApplicationUser assignedStaffMember;
    private String usernameOfAssignee;

    private String opSystemWhereBugOccured;

    private String usernameOfReporterOfBug;

    private BugPriority priority;


}
