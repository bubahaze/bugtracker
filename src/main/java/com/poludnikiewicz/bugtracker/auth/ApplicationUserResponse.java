package com.poludnikiewicz.bugtracker.auth;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.poludnikiewicz.bugtracker.bug.dto.BugShorterResponse;
import com.poludnikiewicz.bugtracker.security.ApplicationUserRole;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ApplicationUserResponse {

    private String username;
    private String email;
    private ApplicationUserRole applicationUserRole;
    private boolean isEnabled;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<BugShorterResponse> assignedBugs;
    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private List<BugShorterResponse> reportedBugs;

}
