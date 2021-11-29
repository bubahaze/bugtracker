package com.poludnikiewicz.bugtracker.api;


import com.poludnikiewicz.bugtracker.dao.Bug;
import com.poludnikiewicz.bugtracker.service.BugService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/bugtracker")
public class BugController {

    private BugService service;

    @Autowired
    public BugController(BugService service) {
        this.service = service;
    }

    @GetMapping("/showall")
    public ResponseEntity<List<Bug>> listAllBugs() {
        List<Bug> allBugs = service.findAllBugs();
        return new ResponseEntity<>(allBugs, HttpStatus.OK);
    }

    @GetMapping("/show/{id}")
    public ResponseEntity<Bug> showById(@PathVariable Long id) {
        Bug bug = service.findById(id);
        return new ResponseEntity<>(bug, HttpStatus.OK);
    }

    @PostMapping("/new")
    public ResponseEntity<Bug> addBug(@RequestBody Bug bug) {
        Bug newBug = service.addBug(bug);
       return new ResponseEntity<>(newBug, HttpStatus.CREATED);
    }

    @PutMapping("/update")
    public ResponseEntity<Bug> updateBug(@RequestBody Bug bug) {
        Bug updatedBug = service.updateBug(bug);
        return new ResponseEntity<>(bug, HttpStatus.OK);
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteBug(@PathVariable Long id) {
        //question mark means it will not return any type in responseEntity
        service.deleteBug(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
