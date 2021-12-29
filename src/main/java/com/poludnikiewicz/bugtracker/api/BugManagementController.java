package com.poludnikiewicz.bugtracker.api;


import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.github.fge.jsonpatch.JsonPatch;
import com.github.fge.jsonpatch.JsonPatchException;
import com.poludnikiewicz.bugtracker.auth.ApplicationUser;
import com.poludnikiewicz.bugtracker.auth.ApplicationUserService;
import com.poludnikiewicz.bugtracker.bug.Bug;
import com.poludnikiewicz.bugtracker.bug.BugPriority;
import com.poludnikiewicz.bugtracker.bug.BugService;
import com.poludnikiewicz.bugtracker.bug.BugStatus;
import com.poludnikiewicz.bugtracker.security.ApplicationUserRole;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@RequestMapping("/manage/api/bug")
@AllArgsConstructor
@Validated
public class ManagementController {

    private final BugService bugService;
    private final ApplicationUserService userService;
    private final ObjectMapper objectMapper;

    @GetMapping("/bug/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<Bug> showById(@PathVariable Long id) {
        Bug bug = bugService.findById(id);
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
            bugService.updateBug(toAssign, id);
        } else {
            throw new IllegalStateException("Assignee must be of role STAFF or ADMIN");
        }
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

//    @PatchMapping("/setPriority/{id}")
//    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
//    public ResponseEntity<Bug> setPriorityOfBug(@RequestParam BugPriority priority, @PathVariable Long id) {
//        Bug toSetPriority = bugService.findById(id);
//        toSetPriority.setPriority(priority);
//
//        return new ResponseEntity<>(toSetPriority, HttpStatus.OK);
//    }

    @PatchMapping("/setRole")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<ApplicationUser> setRoleOfApplicationUser(@RequestParam String username,
                                                                    @Valid @RequestParam ApplicationUserRole role) {
        ApplicationUser toSetRole = (ApplicationUser) userService.loadUserByUsername(username);
        toSetRole.setApplicationUserRole(role);
        userService.saveApplicationUser(toSetRole);

        //TODO: mapping to ApplicationUserResponse ?
        return new ResponseEntity<>(toSetRole, HttpStatus.OK);
    }

//    @PatchMapping("/{id}")
//    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
//    public ResponseEntity<Bug> setStatusOfBug(@PathVariable Long id, @RequestParam BugStatus status) {
//        Bug toSetStatus = bugService.findById(id);
//        toSetStatus.setStatus(status);
////
////        return new ResponseEntity<>(toSetStatus, HttpStatus.OK);
//
//        return new ResponseEntity<>(toSetStatus, HttpStatus.OK);
//    }

    @PatchMapping(path = "/{id}", consumes = "application/json-patch+json")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<?> updateBug(@PathVariable Long id, @RequestBody JsonPatch patch) {
        try {
            Bug bug = bugService.findById(id);
            Bug bugPatched = applyPatchToBug(patch, bug);
            bugService.updateBug(bugPatched, id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (JsonPatchException | JsonProcessingException e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    private Bug applyPatchToBug(JsonPatch patch, Bug targetBug) throws JsonPatchException,
            JsonProcessingException {
        //objectMapper.configure(SerializationFeature.FAIL_ON_EMPTY_BEANS, false);
        JsonNode patched = patch.apply(objectMapper.convertValue(targetBug, JsonNode.class));
        return objectMapper.treeToValue(patched, Bug.class);
    }

}
