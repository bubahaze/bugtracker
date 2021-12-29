package com.poludnikiewicz.bugtracker.api;

import com.poludnikiewicz.bugtracker.auth.ApplicationUser;
import com.poludnikiewicz.bugtracker.auth.ApplicationUserResponse;
import com.poludnikiewicz.bugtracker.auth.ApplicationUserService;
import com.poludnikiewicz.bugtracker.security.ApplicationUserRole;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/manage/api/users")
@AllArgsConstructor
@Validated
public class UserManagementController {

    private final ApplicationUserService userService;

    @GetMapping("/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<ApplicationUserResponse> showById(@PathVariable Long id) {
        ApplicationUserResponse userResponse = userService.findApplicationUserResponseById(id);
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @GetMapping
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<List<ApplicationUserResponse>> showAllUsers() {
        List<ApplicationUserResponse> allUsers = userService.findAllUsers();
        return new ResponseEntity<>(allUsers, HttpStatus.OK);
    }


    @PatchMapping("/setRole")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> setRoleOfApplicationUser(@RequestParam String username,
                                                                    @Valid @RequestParam ApplicationUserRole role) {
        ApplicationUser toSetRole = (ApplicationUser) userService.loadUserByUsername(username);
        toSetRole.setApplicationUserRole(role);
        userService.saveApplicationUser(toSetRole);

        //TODO: mapping to ApplicationUserResponse ?
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
