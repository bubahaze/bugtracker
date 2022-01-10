package com.poludnikiewicz.bugtracker.api;

import com.poludnikiewicz.bugtracker.auth.ApplicationUser;
import com.poludnikiewicz.bugtracker.auth.ApplicationUserResponse;
import com.poludnikiewicz.bugtracker.auth.ApplicationUserService;
import com.poludnikiewicz.bugtracker.security.ApplicationUserRole;
import io.swagger.v3.oas.annotations.tags.Tag;
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
@Tag(name = "User Management", description = "Managing users by Admin & Staff members")
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
    public ResponseEntity<String> setRoleOfApplicationUser(@RequestParam String username,
                                                                    @Valid @RequestParam ApplicationUserRole role) {
        ApplicationUser toSetRole = (ApplicationUser) userService.loadUserByUsername(username);
        toSetRole.setApplicationUserRole(role);
        userService.saveApplicationUser(toSetRole);

        return new ResponseEntity<>(String.format("%s has now the role of %s", username, role), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteApplicationUser(@PathVariable Long id) {
        userService.deleteApplicationUserById(id);

        return new ResponseEntity<>(String.format("Application User with id %d successfully deleted", id),
                HttpStatus.OK);
    }
}
