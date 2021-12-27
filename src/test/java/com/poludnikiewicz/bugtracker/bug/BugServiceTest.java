package com.poludnikiewicz.bugtracker.bug;

import com.poludnikiewicz.bugtracker.exception.BugNotFoundException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BugServiceTest {

    @Mock
    private BugRepository repository;
    @InjectMocks
    private BugService service;


    @Test
    @DisplayName("Should set UUID code for Bug")
    void addBug_1() {
        Bug bug = new Bug();
        //service.addBug(bug); rewrite method and/or test
        assertNotNull(bug.getUniqueCode());
        fail();
    }

    @Test
    @DisplayName("Should return save method of BugRepository")
    void addBug_2() {
        Bug bug = new Bug();
        //service.addBug(bug); REWRITE method and/or test
        verify(repository).save(bug);
        fail();
    }

    @Test
    void updateBug() {
        //TODO
        fail();

    }

    @Test
    @DisplayName("Should invoke deleteById method of BugRepository")
    void deleteBug() {
        Long id = 1L;
        service.deleteBug(id);
        verify(repository).deleteById(id);
    }

    @Test
    @DisplayName("Should return findAll method of BugRepository")
    void findAllBugs() {
        service.findAllBugs();
        verify(repository).findAll();
    }

    @Test
    @DisplayName("Should return findById method of BugRepository")
    void findById_1() {
        Bug bug = mock(Bug.class);
        Long id = 234L;
        when(repository.findById(id)).thenReturn(Optional.of(bug));
        service.findById(id);
        verify(repository).findById(id);
    }

    @Test
    @DisplayName("Should throw BugNotFoundException if Bug with provided id not present")
    void findById_2() {
        Long id = 1432L;
      Exception exception = assertThrows(BugNotFoundException.class, () -> service.findById(id));
        String expectedMessage = String.format("Bug with id %d not found", id);
        String actualMessage = exception.getMessage();

        assertTrue(actualMessage.contains(expectedMessage));
    }

    @Test
    @DisplayName("Should return findByProjectIgnoreCaseOrderByCreationDateDesc method of BugRepository")
    void findByProject() {
        String project = "project";
        service.findByProject(project);
        verify(repository).findByProjectIgnoreCaseOrderByCreationDateDesc(project);

    }
}