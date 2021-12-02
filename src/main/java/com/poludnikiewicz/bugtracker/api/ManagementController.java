package com.poludnikiewicz.bugtracker.api;


import com.poludnikiewicz.bugtracker.dao.Bug;
import com.poludnikiewicz.bugtracker.service.BugService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
    public ResponseEntity<?> deleteBug(@PathVariable Long id) {
        //question mark means it will not return any type in responseEntity
        service.deleteBug(id);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    @PutMapping("/bug")
    public ResponseEntity<Bug> updateBug(@RequestBody Bug bug, @PathVariable Long id) {

        Bug updatedBug = service.updateBug(bug, id);
        return new ResponseEntity<>(updatedBug, HttpStatus.OK);
    }

    //TODO: is method that assigns staff member only needed ? or method that changes status ?


}
