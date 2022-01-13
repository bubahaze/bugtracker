package com.poludnikiewicz.bugtracker.api;

import com.poludnikiewicz.bugtracker.auth.ApplicationUser;
import com.poludnikiewicz.bugtracker.auth.ApplicationUserResponse;
import com.poludnikiewicz.bugtracker.auth.ApplicationUserService;
import com.poludnikiewicz.bugtracker.security.ApplicationUserRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotBlank;
import java.util.List;

@RestController
@RequestMapping("/manage/api/users")
@AllArgsConstructor
@Validated
@Tag(name = "User Management", description = "Managing users by Admin & Staff members")
public class UserManagementController {

    private final ApplicationUserService userService;

    @GetMapping("/{username}")
    @Operation(summary = "Displays user with provided username")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<ApplicationUserResponse> showByUsername(@PathVariable String username) {
        ApplicationUserResponse userResponse = userService.findApplicationUserResponseByUsername(username);
        return new ResponseEntity<>(userResponse, HttpStatus.OK);
    }

    @GetMapping
    @Operation(summary = "Displays all users")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<List<ApplicationUserResponse>> showAllUsers() {
        List<ApplicationUserResponse> allUsers = userService.findAllUsers();
        return new ResponseEntity<>(allUsers, HttpStatus.OK);
    }


    @PatchMapping("/set-role")
    @Operation(summary = "Admin sets role of user")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> setRoleOfApplicationUser(@RequestParam String username,
                                                           @Parameter(description = "admin, staff," +
                                                                   " user (case ignored)") @NotBlank @RequestParam String role) {
        ApplicationUser toSetRole = (ApplicationUser) userService.loadUserByUsername(username);
        toSetRole.setApplicationUserRole(ApplicationUserRole.sanitizeUserRole(role));
        userService.saveApplicationUser(toSetRole);

        return new ResponseEntity<>(String.format("%s has now the role of %s", username, role), HttpStatus.OK);
    }

    @DeleteMapping("/{username}")
    @Operation(summary = "Admin deletes user with provided username")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<String> deleteApplicationUser(@PathVariable String username) {
        userService.deleteApplicationUserByUsername(username);

        return new ResponseEntity<>(String.format("Application User with username %s successfully deleted", username),
                HttpStatus.OK);
    }
}
