package com.poludnikiewicz.bugtracker.api;


import com.poludnikiewicz.bugtracker.bug.Bug;
import com.poludnikiewicz.bugtracker.bug.BugService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/manage/api")
public class ManagementController {

    private final BugService service;

    @Autowired
    public ManagementController(BugService service) {
        this.service = service;
    }

    @DeleteMapping("/bug/{id}")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    public ResponseEntity<?> deleteBug(@PathVariable Long id) {
        service.deleteBug(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/bug/{id}")
    @PreAuthorize("hasAnyRole('ROLE_ADMIN', 'ROLE_STAFF')")
    public ResponseEntity<Bug> updateBug(@RequestBody Bug bug, @PathVariable Long id) {

        Bug updatedBug = service.updateBug(bug, id);
        return new ResponseEntity<>(updatedBug, HttpStatus.OK);
    }


}
