package com.poludnikiewicz.bugtracker.api;


import com.poludnikiewicz.bugtracker.dao.Bug;
import com.poludnikiewicz.bugtracker.exception.BugNotFoundException;
import com.poludnikiewicz.bugtracker.service.BugService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Collection;
import java.util.List;

@RestController
@RequestMapping("/api/bugtracker")
public class BugController {

    private BugService service;

    @Autowired
    public BugController(BugService service) {
        this.service = service;
    }

    @GetMapping("/bug")
    public ResponseEntity<List<Bug>> listAllBugs() {
        List<Bug> allBugs = service.findAllBugs();
        return new ResponseEntity<>(allBugs, HttpStatus.OK);
    }

    @GetMapping("/bug/{id}")
    public ResponseEntity<Bug> showById(@PathVariable Long id) {
        Bug bug = service.findById(id)
                .orElseThrow(() -> new BugNotFoundException("Bug with id " + id + " not found"));
        return new ResponseEntity<>(bug, HttpStatus.OK);
    }

    @GetMapping(value = "/bug/search", params = "project")
    public ResponseEntity<Collection<Bug>> searchByProject(@RequestParam String project) {
        Collection<Bug> bugsByProject = service.findByProject(project);
        return new ResponseEntity<>(bugsByProject, HttpStatus.OK);

    }

    @PostMapping("/new")
    public ResponseEntity<Bug> addBug(@RequestBody Bug bug) {
        Bug newBug = service.addBug(bug);
       return new ResponseEntity<>(newBug, HttpStatus.CREATED);
    }

    @PutMapping("/bug")
    public ResponseEntity<Bug> updateBug(@RequestBody Bug bug, @PathVariable Long id) {

        Bug updatedBug = service.updateBug(bug, id);
        return new ResponseEntity<>(updatedBug, HttpStatus.OK);
    }

    @DeleteMapping("/bug/{id}")
    public ResponseEntity<?> deleteBug(@PathVariable Long id) {
        //question mark means it will not return any type in responseEntity
        service.deleteBug(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
