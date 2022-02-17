package com.poludnikiewicz.bugtracker.api;


import com.fasterxml.jackson.annotation.JsonView;
import com.poludnikiewicz.bugtracker.auth.ApplicationUser;
import com.poludnikiewicz.bugtracker.auth.ApplicationUserService;
import com.poludnikiewicz.bugtracker.bug.*;
import com.poludnikiewicz.bugtracker.bug.dto.BugRequest;
import com.poludnikiewicz.bugtracker.bug.dto.BugResponse;
import com.poludnikiewicz.bugtracker.email.EmailService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.util.List;

@RestController
@RequestMapping("/manage/bugs")
@AllArgsConstructor
@Validated
@Tag(name = "Bug Management", description = "API for Admin & Staff members")
public class BugManagementController {

    private final BugService bugService;
    private final ApplicationUserService userService;
    private final EmailService emailService;

    @GetMapping("/{bugId}")
    @JsonView(Views.StaffSingleBug.class)
    @Operation(summary = "Displays bug by provided ID along with comment IDs")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<BugResponse> showById(@PathVariable Long bugId) {
        BugResponse bug = bugService.findBugResponseById(bugId);
        return new ResponseEntity<>(bug, HttpStatus.OK);
    }

    @GetMapping
    @JsonView(Views.General.class)
    @Operation(summary = "Displays list of bugs according to provided priority")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<List<BugResponse>> showByPriority(@Parameter(description = "Priorities: P1_CRITICAL," +
            " P2_IMPORTANT, P3_NORMAL, P4_MARGINAL, P5_REDUNTANT, UNSET." +
            " Also possible input: p3, marginal etc.") @RequestParam @NotBlank String priority) {
        List<BugResponse> bugsByPriority = bugService.findBugsByPriority(priority);
        return new ResponseEntity<>(bugsByPriority, HttpStatus.OK);
    }

    @GetMapping("/assigned")
    @JsonView(Views.General.class)
    @Operation(summary = "Displays list of bugs by assigned to currently logged in Staff member")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<List<BugResponse>> showBugsAssignedToPrincipal(Authentication authentication) {
        List<BugResponse> bugs = bugService.findAllBugsAssignedToApplicationUser(authentication.getName());

        return new ResponseEntity<>(bugs, HttpStatus.OK);
    }

    @GetMapping("/assigned-to")
    @JsonView(Views.General.class)
    @Operation(summary = "Displays list of bugs by assigned to the User(Staff member) passed as param")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<List<BugResponse>> showBugsAssignedToUser(@RequestParam @NotBlank String username) {
        List<BugResponse> bugs = bugService.findAllBugsAssignedToApplicationUser(username);

        return new ResponseEntity<>(bugs, HttpStatus.OK);
    }

    @GetMapping(value = "/sort", params = "key")
    @JsonView(Views.General.class)
    @Operation(summary = "Displays list of bugs sorted according to provided key")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<List<BugResponse>> sortBugsAccordingToKey(@Parameter(description = "Choose one of BugResponse fields") @RequestParam @NotBlank String key,
                                                                    @Parameter(description = "asc = ascending order," +
                                                                            " desc = descending order. Default: asc")
                                                                    @RequestParam(required = false) String direction) {

        return new ResponseEntity<>(bugService.sortBugsAccordingToKey(key, direction), HttpStatus.OK);
    }

    @DeleteMapping("/{bugId}")
    @Operation(summary = "Admin deletes bug with provided ID")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteBug(@PathVariable Long bugId) {
        Bug bug = bugService.findById(bugId);
        if (bug.getReporterOfBug() != null) {
            drawNotificationEmail(bug.getReporterOfBug().getEmail(), "bug deleted");
        }
        bugService.deleteBug(bugId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @PutMapping("/{bugId}")
    @Operation(summary = "Admin updates bug with provided ID")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> updateBug(@RequestBody @Valid BugRequest bug, @PathVariable Long bugId) {

        bugService.updateBugByBugRequest(bug, bugId);

        return new ResponseEntity<>(String.format("Bug with %d successfully updated", bugId), HttpStatus.OK);
    }

    @PatchMapping("/assignee/{bugId}")
    @Operation(summary = "Admin assigns bug to be solved by particular staff member")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> assignStaffToBug(@RequestParam @NotBlank String staffAssigneeUsername, @PathVariable Long bugId) {
        Bug bug = bugService.findById(bugId);
        ApplicationUser staffAssignee = (ApplicationUser) userService.loadUserByUsername(staffAssigneeUsername);

        if (isStaffOrAdmin(staffAssignee)) {
            assignToBugAndChangeBugStatus(bug, staffAssignee);
            if (bug.getReporterOfBug() != null) {
                drawNotificationEmail(bug.getReporterOfBug().getEmail(), staffAssignee);
            }
        } else {
            throw new IllegalStateException("Assignee must be of role STAFF or ADMIN");
        }
        return new ResponseEntity<>(String.format("Bug with id %d has been assigned to %s", bugId, staffAssigneeUsername),
                HttpStatus.OK);
    }

    private boolean isStaffOrAdmin(ApplicationUser staffAssignee) {
        return staffAssignee.getApplicationUserRole().name().equals("STAFF") ||
                staffAssignee.getApplicationUserRole().name().equals("ADMIN");

    }

    @PatchMapping("/staff/assignee/{bugId}")
    @Operation(summary = "Staff member assigns bug with provided ID to himself/herself")
    @PreAuthorize("hasRole('ROLE_STAFF')")
    public ResponseEntity<String> assignBugToPrincipal(@PathVariable Long bugId, Authentication authentication) {
        String usernameOfPrincipal = authentication.getName();
        Bug bug = bugService.findById(bugId);
        ApplicationUser staffAssignee = (ApplicationUser) userService.loadUserByUsername(usernameOfPrincipal);
        assignToBugAndChangeBugStatus(bug, staffAssignee);
        if (bug.getReporterOfBug() != null) {
            drawNotificationEmail(bug.getReporterOfBug().getEmail(), staffAssignee);
        }

        return new ResponseEntity<>(String.format("Bug with id %d has been assigned to you", bugId), HttpStatus.OK);

    }

    private void assignToBugAndChangeBugStatus(Bug bug, ApplicationUser applicationUser) {
        bug.setAssignedStaffMember(applicationUser);
        bug.setStatus(BugStatus.ASSIGNED);
        bugService.saveBug(bug);
    }

    @PatchMapping("/priority/{bugId}")
    @Operation(summary = "Admin or Staff member sets priority of bug")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<String> setPriorityOfBug(@PathVariable Long bugId, @Parameter(description = "Priorities: P1_CRITICAL, P2_IMPORTANT," +
            " P3_NORMAL, P4_MARGINAL, P5_REDUNDANT, UNSET." +
            " Also possible input: p3, marginal etc.") @RequestParam @NotBlank String priority) {
        Bug bug = bugService.findById(bugId);

        BugPriority priorityToSet = BugPriority.sanitizePriorityInput(priority);
        bug.setPriority(priorityToSet);
        bugService.saveBug(bug);
        if (bug.getReporterOfBug() != null) {
            drawNotificationEmail(bug.getReporterOfBug().getEmail(), priorityToSet);
        }

        return new ResponseEntity<>(String.format("Priority successfully set to %s", priority), HttpStatus.OK);
    }


    @PatchMapping("/{bugId}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    @Operation(summary = "Admin or Staff member sets status of bug")
    public ResponseEntity<String> setStatusOfBug(@PathVariable Long bugId, @Parameter(description = "Options: Reported," +
            " Assigned, Resolved") @RequestParam @NotBlank String status) {
        Bug bug = bugService.findById(bugId);
        BugStatus statusToSet = BugStatus.valueOf(status.toUpperCase());
        bug.setStatus(statusToSet);
        bugService.saveBug(bug);
        if (bug.getReporterOfBug() != null) {
            drawNotificationEmail(bug.getReporterOfBug().getEmail(), statusToSet);
        }
        return new ResponseEntity<>(String.format("Status successfully set to %s", statusToSet), HttpStatus.OK);
    }

    private void drawNotificationEmail(String email, Object objectOfChange) {
        String detail = "";
        if (objectOfChange instanceof BugStatus) {
            detail = "Current bug status: " + ((BugStatus) objectOfChange).name();
        } else if (objectOfChange instanceof BugPriority) {
            detail = "Current bug priority: " + ((BugPriority) objectOfChange).name();
        } else if (objectOfChange instanceof ApplicationUser) {
            detail = "The bug is now assigned to one of our skilled staff members that will work at resolving it";
        } else if (objectOfChange instanceof String) {
            detail = "The administrator decided to delete your reported bug from our database. If you need more information" +
                    " do not hesitate to contact us";
        }

        String content = String.format("The bug you have reported has recently been updated by our Staff. %s.", detail);

        emailService.sendNotificationEmail(email, content);

    }

}
