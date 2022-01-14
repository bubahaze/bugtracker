package com.poludnikiewicz.bugtracker.auth;

import com.poludnikiewicz.bugtracker.bug.Bug;
import com.poludnikiewicz.bugtracker.bug.dto.BugShorterResponse;
import com.poludnikiewicz.bugtracker.exception.ApplicationUserNotFoundException;
import com.poludnikiewicz.bugtracker.registration.token.ConfirmationToken;
import com.poludnikiewicz.bugtracker.registration.token.ConfirmationTokenService;
import com.poludnikiewicz.bugtracker.security.ApplicationUserRole;
import lombok.AllArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class ApplicationUserService implements UserDetailsService {

    private final ApplicationUserRepository applicationUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final ConfirmationTokenService confirmationTokenService;


    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return applicationUserRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username + " not found"));
    }

    public String signUpUser(ApplicationUser user) {
        boolean userExists = applicationUserRepository
                .findByEmail(user.getEmail())
                .isPresent();

        boolean usernameExists = applicationUserRepository.findByUsername(user.getUsername()).isPresent();

        if (usernameExists) {
            throw new IllegalStateException("This username is taken. Try another one.");
        }

        if (userExists) {
            throw new IllegalStateException("Email already registered.");
        }
        String encodedPassword = passwordEncoder.encode(user.getPassword());
        user.setPassword(encodedPassword);
        applicationUserRepository.save(user);

        String token = UUID.randomUUID().toString();
        ConfirmationToken confirmationToken = new ConfirmationToken(token, LocalDateTime.now(),
                LocalDateTime.now().plusMinutes(30), user);
        confirmationTokenService.saveConfirmationToken(confirmationToken);
        return token;
    }


    public int enableApplicationUser(String email) {
        return applicationUserRepository.enableApplicationUser(email);
    }

    public ApplicationUser saveApplicationUser(ApplicationUser user) {
        return applicationUserRepository.save(user);
    }

    public List<ApplicationUserResponse> findAllUsers() {
        return applicationUserRepository.findAll()
                .stream()
                .map(this::mapToApplicationUserResponse)
                .collect(Collectors.toList());
    }

    public void deleteApplicationUserByUsername(String username) {
        ApplicationUser user = applicationUserRepository.findByUsername(username)
                .orElseThrow(() -> new ApplicationUserNotFoundException(String.format("User with username %s not found.", username)));
        applicationUserRepository.deleteById(user.getId());
    }

    public List<ApplicationUserResponse> findByRole(String role) {
        ApplicationUserRole userRole = ApplicationUserRole.sanitizeUserRole(role);
       return applicationUserRepository.findByApplicationUserRole(userRole).stream()
               .map(this::mapToApplicationUserResponse).collect(Collectors.toList());
    }

    public ApplicationUserResponse findApplicationUserResponseByUsername(String username) {
        ApplicationUser applicationUser = applicationUserRepository.findByUsername(username)
                .orElseThrow(() -> new ApplicationUserNotFoundException(String.format("User with username %s not found.", username)));
        return mapToApplicationUserResponse(applicationUser);
    }

    private ApplicationUserResponse mapToApplicationUserResponse(ApplicationUser user) {
        ApplicationUserResponse userResponse = new ApplicationUserResponse();
        userResponse.setUsername(user.getUsername());
        userResponse.setEmail(user.getEmail());
        userResponse.setApplicationUserRole(user.getApplicationUserRole());
        userResponse.setEnabled(user.isEnabled());
        if (!user.getAssignedBugs().isEmpty()) {
            userResponse.setAssignedBugs(user.getAssignedBugs().stream()
                    .map(this::mapToBugShorterResponse)
                    .collect(Collectors.toList()));
        }
        return userResponse;
    }

    private BugShorterResponse mapToBugShorterResponse(Bug bug) {
        return BugShorterResponse.builder()
                .summary(bug.getSummary())
                .lastChangeAt(bug.getLastChangeAt())
                .status(bug.getStatus())
                .priority(bug.getPriority()).build();
    }

}
