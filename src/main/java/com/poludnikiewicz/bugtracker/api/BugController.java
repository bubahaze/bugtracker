package com.poludnikiewicz.bugtracker.api;


import com.poludnikiewicz.bugtracker.bug.Bug;
import com.poludnikiewicz.bugtracker.bug.dto.BugRequest;
import com.poludnikiewicz.bugtracker.bug.BugService;
import com.poludnikiewicz.bugtracker.bug.dto.BugResponse;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;
import java.util.Collection;
import java.util.List;

@AllArgsConstructor
@RestController
@RequestMapping("/bugtracker/api")
@Validated
public class BugController {

    private final BugService service;

    @GetMapping("/bug")
    public ResponseEntity<List<BugResponse>> showAllBugs() {
        List<BugResponse> allBugs = service.findAllBugs();
        return new ResponseEntity<>(allBugs, HttpStatus.OK);
    }

    @GetMapping("/bug/{uniqueCode}")
    public ResponseEntity<BugResponse> showByUniqueCode(@PathVariable String uniqueCode) {
        BugResponse bugResponse = service.findByUniqueCode(uniqueCode);
        return new ResponseEntity<>(bugResponse, HttpStatus.OK);
    }

    @GetMapping(value = "/bug/search", params = "project")
    public ResponseEntity<Collection<BugResponse>> searchByProject(@RequestParam String project) {
        List<BugResponse> bugsByProject = service.findByProject(project);
        return new ResponseEntity<>(bugsByProject, HttpStatus.OK);

    }

    @GetMapping(value = "/bug/search", params = "keyword")
    public ResponseEntity<Collection<BugResponse>> searchByKeyword(@RequestParam @NotBlank String keyword) {
        List<BugResponse> bugsByKeyword = service.findByKeyword(keyword);
        return new ResponseEntity<>(bugsByKeyword, HttpStatus.OK);
    }

    @PostMapping("/new")
    public ResponseEntity<String> postBug(@Valid @RequestBody BugRequest bug, Authentication authentication) {
        UserDetails userDetailsOfReporter = (UserDetails) authentication.getPrincipal();
        String reporterUsername = userDetailsOfReporter.getUsername();
       String uniqueCode = service.addBug(bug, reporterUsername);

       return new ResponseEntity<>(String.format("Bug successfully reported. The unique ID of reported bug is: %s", uniqueCode),
               HttpStatus.CREATED);
    }


}
