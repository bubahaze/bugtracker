package com.poludnikiewicz.bugtracker.api;


import com.poludnikiewicz.bugtracker.bug.Bug;
import com.poludnikiewicz.bugtracker.bug.dto.BugRequest;
import com.poludnikiewicz.bugtracker.bug.BugService;
import com.poludnikiewicz.bugtracker.bug.dto.BugResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/bugtracker/api")
@Validated
public class BugController {

    private final BugService service;

    @Autowired
    public BugController(BugService service) {
        this.service = service;
    }

    @GetMapping("/bug")
    public ResponseEntity<List<BugResponse>> showAllBugs() {
        List<BugResponse> allBugs = service.findAllBugs();
        return new ResponseEntity<>(allBugs, HttpStatus.OK);
    }

    @GetMapping("/bug/{uniqueCode}")
    public ResponseEntity<Bug> showByUniqueCode(@PathVariable String uniqueCode) {
        Bug bug = service.findByUniqueCode(uniqueCode);
        return new ResponseEntity<>(bug, HttpStatus.OK);
    }

    @GetMapping(value = "/bug/search", params = "project")
    public ResponseEntity<Collection<Bug>> searchByProject(@RequestParam String project) {
        Collection<Bug> bugsByProject = service.findByProject(project);
        return new ResponseEntity<>(bugsByProject, HttpStatus.OK);

    }

    //TODO: getMapping searching other params

    @PostMapping("/new")
    public ResponseEntity<String> postBug(@Valid @RequestBody BugRequest bug, Authentication authentication) {
        UserDetails userDetailsOfReporter = (UserDetails) authentication.getPrincipal();
        String reporterUsername = userDetailsOfReporter.getUsername();
       String uniqueCode = service.addBug(bug, reporterUsername);

       return new ResponseEntity<>(String.format("Bug successfully reported. The unique ID of reported bug is: %s", uniqueCode),
               HttpStatus.CREATED);
    }


}
