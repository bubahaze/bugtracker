package com.poludnikiewicz.bugtracker.api;


import com.fasterxml.jackson.annotation.JsonView;
import com.poludnikiewicz.bugtracker.auth.ApplicationUser;
import com.poludnikiewicz.bugtracker.auth.ApplicationUserService;
import com.poludnikiewicz.bugtracker.bug.*;
import com.poludnikiewicz.bugtracker.bug.dto.BugRequest;
import com.poludnikiewicz.bugtracker.bug.dto.BugResponse;
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

import javax.validation.constraints.NotBlank;
import java.util.List;

@RestController
@RequestMapping("/manage/api/bug")
@AllArgsConstructor
@Validated
@Tag(name = "Bug Management", description = "API for Admin & Staff members")
public class BugManagementController {

    private final BugService bugService;
    private final ApplicationUserService userService;

    @GetMapping("/{id}")
    @JsonView(Views.StaffSingleBug.class)
    @Operation(summary = "Displays bug by provided ID along with comment IDs")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<BugResponse> showById(@PathVariable Long id) {
        BugResponse bug = bugService.findBugResponseById(id);
        return new ResponseEntity<>(bug, HttpStatus.OK);
    }

    @GetMapping
    @JsonView(Views.General.class)
    @Operation(summary = "Displays list of bugs according to provided priority")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<List<BugResponse>> showByPriority(@Parameter(description = "Priorities: P1_CRITICAL," +
            " P2_IMPORTANT, P3_NORMAL, P4_MARGINAL, P5_REDUNTANT, UNSET." +
            " Also possible input: p3, marginal etc.") @RequestParam String priority) {
        List<BugResponse> bugsByPriority = bugService.findBugsByPriority(priority);
        return new ResponseEntity<>(bugsByPriority, HttpStatus.OK);
    }

    @GetMapping("/assigned")
    @JsonView(Views.General.class)
    @Operation(summary = "Displays list of bugs by assigned to currently logged in Staff member")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<List<BugResponse>> showBugsAssignedToPrincipal(Authentication authentication) {
        List<BugResponse> bugs = bugService.findAllBugsAssignedToPrincipal(authentication.getName());

        return new ResponseEntity<>(bugs, HttpStatus.OK);
    }

    @GetMapping(value = "/sort", params = "key")
    @JsonView(Views.General.class)
    @Operation(summary = "Displays list of bugs sorted according to provided key")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<List<BugResponse>> sortBugsAccordingToKey(@Parameter(description = "Choose one of BugResponse fields") @RequestParam String key,
                                                                    @Parameter(description = "asc = ascending order," +
                                                                            " desc = descending order. Default: asc")
                                                                    @RequestParam(required = false) String direction) {

        return new ResponseEntity<>(bugService.sortBugsAccordingToKey(key, direction), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Admin deletes bug with provided ID")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteBug(@PathVariable Long id) {
        bugService.deleteBug(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @PutMapping("/{id}")
    @Operation(summary = "Admin updates bug with provided id")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> updateBug(@RequestBody BugRequest bug, @PathVariable Long id) {

        bugService.updateBugByBugRequest(bug, id);

        return new ResponseEntity<>(String.format("Bug with %d successfully updated", id), HttpStatus.OK);
    }

    @PatchMapping("/assign/{id}")
    @Operation(summary = "Admin assigns bug to be solved by particular staff member")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> assignStaffToBug(@RequestParam @NotBlank String staffAssigneeUsername, @PathVariable Long id) {
        Bug toAssign = bugService.findById(id);
        ApplicationUser staffAssignee = (ApplicationUser) userService.loadUserByUsername(staffAssigneeUsername);

        if (isStaffOrAdmin(staffAssignee)) {
            assignToBugAndChangeBugStatus(toAssign, staffAssignee);
        } else {
            throw new IllegalStateException("Assignee must be of role STAFF or ADMIN");
        }
        return new ResponseEntity<>(String.format("Bug with id %d has been assigned to %s", id, staffAssigneeUsername),
                HttpStatus.OK);
    }

    private boolean isStaffOrAdmin(ApplicationUser staffAssignee) {
        return staffAssignee.getApplicationUserRole().name().equals("STAFF") ||
                staffAssignee.getApplicationUserRole().name().equals("ADMIN");

    }

    @PatchMapping("/staff/assign/{id}")
    @Operation(summary = "Staff member assigns bug with provided ID to himself/herself")
    @PreAuthorize("hasRole('ROLE_STAFF')")
    public ResponseEntity<String> assignBugToPrincipal(@PathVariable Long id, Authentication authentication) {
        String usernameOfPrincipal = authentication.getName();
        Bug toAssign = bugService.findById(id);
        ApplicationUser staffAssignee = (ApplicationUser) userService.loadUserByUsername(usernameOfPrincipal);
        assignToBugAndChangeBugStatus(toAssign, staffAssignee);

        return new ResponseEntity<>(String.format("Bug with id %d successfully assigned to you", id), HttpStatus.OK);

    }

    private void assignToBugAndChangeBugStatus(Bug bug, ApplicationUser applicationUser) {
        bug.setAssignedStaffMember(applicationUser);
        bug.setStatus(BugStatus.ASSIGNED);
        bugService.saveBug(bug);
    }

    @PatchMapping("/set-priority/{id}")
    @Operation(summary = "Admin or Staff member sets priority of bug")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<String> setPriorityOfBug(@PathVariable Long id, @Parameter(description = "Priorities: P1_CRITICAL, P2_IMPORTANT," +
            " P3_NORMAL, P4_MARGINAL, P5_REDUNTANT, UNSET." +
            " Also possible input: p3, marginal etc.") @RequestParam String priority) {
        Bug toSetPriority = bugService.findById(id);

        toSetPriority.setPriority(BugPriority.sanitizePriorityInput(priority));
        bugService.saveBug(toSetPriority);

        return new ResponseEntity<>(String.format("Priority successfully set to %s", priority), HttpStatus.OK);
    }


    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    @Operation(summary = "Admin or Staff member sets status of bug")
    public ResponseEntity<String> setStatusOfBug(@PathVariable Long id, @Parameter(description = "Options: Reported," +
            " Assigned, Resolved") @RequestParam String status) {
        Bug toSetStatus = bugService.findById(id);
        toSetStatus.setStatus(BugStatus.valueOf(status.toUpperCase()));
        bugService.saveBug(toSetStatus);

        return new ResponseEntity<>(String.format("Status successfully set to %s", status), HttpStatus.OK);
    }

}
