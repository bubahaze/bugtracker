package com.poludnikiewicz.bugtracker.api;


import com.fasterxml.jackson.annotation.JsonView;
import com.poludnikiewicz.bugtracker.bug.BugService;
import com.poludnikiewicz.bugtracker.bug.Views;
import com.poludnikiewicz.bugtracker.bug.dto.BugRequest;
import com.poludnikiewicz.bugtracker.bug.dto.BugResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/bugs")
@Validated
@Tag(name = "Bugtracker's users API", description = "for all registered users")
public class BugController {

    private final BugService service;

    @GetMapping
    @Operation(summary = "Displays list of all bugs")
    @JsonView(Views.General.class)
    public ResponseEntity<List<BugResponse>> showAllBugs() {
        List<BugResponse> allBugs = service.findAllBugs();
        return new ResponseEntity<>(allBugs, HttpStatus.OK);
    }

    @GetMapping("/{bugId}")
    @JsonView(Views.SingleBug.class)
    @Operation(summary = "Displays bug with provided ID")
    public ResponseEntity<BugResponse> showById(@PathVariable Long bugId) {
        BugResponse bugResponse = service.findBugResponseById(bugId);
        return new ResponseEntity<>(bugResponse, HttpStatus.OK);
    }

    @GetMapping(value = "/search/project", params = "project")
    @JsonView(Views.General.class)
    @Operation(summary = "Displays bugs relevant to provided project parameter", description = "searching in project parameter")
    public ResponseEntity<List<BugResponse>> searchByProject(@RequestParam String project) {
        List<BugResponse> bugsByProject = service.findByProject(project);
        return new ResponseEntity<>(bugsByProject, HttpStatus.OK);

    }

    @GetMapping(value = "/search/keyword", params = "keyword")
    @JsonView(Views.General.class)
    @Operation(summary = "Displays bugs relevant to provided keyword", description = "searching in every bug parameter")
    public ResponseEntity<List<BugResponse>> searchByKeyword(@RequestParam @NotBlank String keyword) {
        List<BugResponse> bugsByKeyword = service.findByKeyword(keyword);
        return new ResponseEntity<>(bugsByKeyword, HttpStatus.OK);
    }

    @GetMapping(value = "/reported")
    @JsonView(Views.General.class)
    @Operation(summary = "Displays bug posted (reported) by logged in user")
    public ResponseEntity<List<BugResponse>> showBugsReportedByPrincipal(Authentication authentication) {
        String reporterUsername = authentication.getName();
        List<BugResponse> bugsReportedByPrincipal = service.findByReporter(reporterUsername);
        return new ResponseEntity<>(bugsReportedByPrincipal, HttpStatus.OK);
    }


    @PostMapping
    @Operation(summary = "Reports new bug")
    public ResponseEntity<String> postBug(@Valid @RequestBody BugRequest bug, Authentication authentication) {
        String reporterUsername = authentication.getName();
        Long id = service.addBug(bug, reporterUsername);

        return new ResponseEntity<>("Bug successfully reported. ID of bug: " + id,
                HttpStatus.CREATED);
    }


}
