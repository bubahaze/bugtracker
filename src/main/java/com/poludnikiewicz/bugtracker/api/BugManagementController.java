package com.poludnikiewicz.bugtracker.api;


import com.poludnikiewicz.bugtracker.auth.ApplicationUser;
import com.poludnikiewicz.bugtracker.auth.ApplicationUserService;
import com.poludnikiewicz.bugtracker.bug.Bug;
import com.poludnikiewicz.bugtracker.bug.BugPriority;
import com.poludnikiewicz.bugtracker.bug.BugService;
import com.poludnikiewicz.bugtracker.bug.BugStatus;
import com.poludnikiewicz.bugtracker.bug.dto.BugResponse;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/manage/api/bug")
@AllArgsConstructor
@Validated
public class BugManagementController {

    private final BugService bugService;
    private final ApplicationUserService userService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<BugResponse> showById(@PathVariable Long id) {
        BugResponse bug = bugService.findBugResponseById(id);
        return new ResponseEntity<>(bug, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteBug(@PathVariable Long id) {
        bugService.deleteBug(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<Bug> updateBug(@RequestBody Bug bug, @PathVariable Long id) {

        Bug toUpdate = bugService.updateBug(bug, id);
        return new ResponseEntity<>(toUpdate, HttpStatus.OK);
    }

    @PatchMapping("/assign/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> assignStaffToBug(@RequestParam String staffAssigneeUsername, @PathVariable Long id) {
        Bug toAssign = bugService.findById(id);
        ApplicationUser staffAssignee = (ApplicationUser) userService.loadUserByUsername(staffAssigneeUsername);
        boolean isStaffOrAdmin = staffAssignee.getApplicationUserRole().name().equals("STAFF") ||
                staffAssignee.getApplicationUserRole().name().equals("ADMIN");
        if (isStaffOrAdmin) {
            toAssign.setAssignedStaffMember(staffAssignee);
            toAssign.setStatus(BugStatus.ASSIGNED);
            bugService.saveBug(toAssign);
        } else {
            throw new IllegalStateException("Assignee must be of role STAFF or ADMIN");
        }
        return new ResponseEntity<>(String.format("Bug with id %d has been assigned to %s", id, staffAssigneeUsername),
                HttpStatus.NO_CONTENT);
    }

    @PatchMapping("/setPriority/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<String> setPriorityOfBug(@RequestParam BugPriority priority, @PathVariable Long id) {
        Bug toSetPriority = bugService.findById(id);
        toSetPriority.setPriority(priority);
        bugService.saveBug(toSetPriority);

        return new ResponseEntity<>(String.format("Priority successfully set to %s", priority), HttpStatus.OK);
    }


    @PatchMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<String> setStatusOfBug(@PathVariable Long id, @RequestParam BugStatus status) {
        Bug toSetStatus = bugService.findById(id);
        toSetStatus.setStatus(status);
        bugService.saveBug(toSetStatus);

        return new ResponseEntity<>(String.format("Status successfully set to %s", status), HttpStatus.OK);
    }

}
