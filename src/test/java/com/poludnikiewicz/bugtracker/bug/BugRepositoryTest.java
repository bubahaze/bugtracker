package com.poludnikiewicz.bugtracker.bug;

import com.poludnikiewicz.bugtracker.auth.ApplicationUser;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.annotation.DirtiesContext;

import javax.persistence.EntityManager;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertIterableEquals;

@DataJpaTest
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
class BugRepositoryTest {

    @Autowired
    BugRepository bugRepository;
    @Autowired
    EntityManager entityManager;

    @Test
    void findByProjectContainingIgnoreCaseOrderByLastChangeAtDesc_should_return_list_of_bugsBy_project_orderBy_latest_change() throws InterruptedException {
        Bug bug1 = Bug.builder().project("test-project").build();
      //  Thread.sleep(50);
        Bug bug2 = Bug.builder().project("project").build();
      //  Thread.sleep(50);
        Bug bug3 = Bug.builder().project("test-project").build();
       // Thread.sleep(50);
        Bug bug4 = Bug.builder().project("test-project").build();

        bugRepository.save(bug1);
        bugRepository.save(bug2);
        bugRepository.save(bug3);
        bugRepository.save(bug4);
        bug1.setStatus(BugStatus.ASSIGNED);
        bug3.setStatus(BugStatus.ASSIGNED);
        bug4.setStatus(BugStatus.ASSIGNED);
        bugRepository.save(bug1);
        bugRepository.save(bug3);
        bugRepository.save(bug4);
        entityManager.refresh(bug1);
        entityManager.refresh(bug3);
        entityManager.refresh(bug4);

        List<Bug> actual = bugRepository.findByProjectContainingIgnoreCaseOrderByLastChangeAtDesc("test");
        List<Bug> expected = List.of(bug4, bug3, bug1);

        assertIterableEquals(expected, actual);
    }

    @Test
    void findByProjectContainingIgnoreCaseOrderByLastChangeAtDesc_should_return_emptyList_if_project_not_exist() {
        Bug bug1 = Bug.builder().project("test-project").build();
        Bug bug2 = Bug.builder().project("test-project").build();
        Bug bug3 = Bug.builder().project("project").build();

        bugRepository.save(bug1);
        bugRepository.save(bug2);
        bugRepository.save(bug3);

        List<Bug> actual = bugRepository.findByProjectContainingIgnoreCaseOrderByLastChangeAtDesc("not-existing");
        List<Bug> expected = Collections.emptyList();

        assertIterableEquals(expected, actual);
    }

    @Test
    void findByKeyword_should_return_list_of_bugs_containing_keyword() {
        ApplicationUser reporter = new ApplicationUser("johnny", "john", "doe", "johndoe@gmail.com", "password");
        Bug bug1 = Bug.builder().summary("sEarCheD-keyword").reporterOfBug(reporter).build();
        Bug bug2 = Bug.builder().project("sEarCheD-keyword").reporterOfBug(reporter).build();
        Bug bug3 = Bug.builder().description("sEarCheD-keyword").reporterOfBug(reporter).build();
        Bug bug4 = Bug.builder().opSystemWhereBugOccurred("sEarCheD-keyword").reporterOfBug(reporter).build();
        Bug bug5 = Bug.builder().reporterOfBug(reporter).build();
        Bug bug6 = Bug.builder().description("other-description").reporterOfBug(reporter).build();
        Bug bug7 = Bug.builder().project("other-project").reporterOfBug(reporter).build();
        Bug bug8 = Bug.builder().summary("other-summary").reporterOfBug(reporter).build();

        bugRepository.save(bug1);
        bugRepository.save(bug2);
        bugRepository.save(bug3);
        bugRepository.save(bug4);
        bugRepository.save(bug5);
        bugRepository.save(bug6);
        bugRepository.save(bug7);
        bugRepository.save(bug8);

        List<Bug> actual = bugRepository.findByKeyword("keyword");
        List<Bug> expected = List.of(bug1, bug2, bug3, bug4);

        assertIterableEquals(expected, actual);
    }

    @Test
    void findByKeyword_should_return_empty_list_if_no_bugs_contain_keyword() {
        ApplicationUser reporter = new ApplicationUser("johnny", "john", "doe", "johndoe@gmail.com", "password");
        Bug bug1 = Bug.builder().summary("sEarCheD-keyword").reporterOfBug(reporter).build();
        Bug bug2 = Bug.builder().project("sEarCheD-keyword").reporterOfBug(reporter).build();
        Bug bug3 = Bug.builder().description("sEarCheD-keyword").reporterOfBug(reporter).build();

        bugRepository.save(bug1);
        bugRepository.save(bug2);
        bugRepository.save(bug3);

        List<Bug> actual = bugRepository.findByKeyword("test");
        List<Bug> expected = Collections.emptyList();

        assertIterableEquals(expected, actual);
    }

    @Test
    void findAllBugsAssignedToPrincipal_should_return_list_of_bugs_assigned_to_provided_user() {
        ApplicationUser assignee1 = new ApplicationUser("johnny", "john", "doe", "johndoe@gmail.com", "password");
        ApplicationUser assignee2 = new ApplicationUser("mary_95", "maria", "brown", "mariabrown@gmail.com", "password");
        ApplicationUser assignee3 = new ApplicationUser("elephant", "elsa", "lark", "elsalark@gmail.com", "password");

        Bug bug1 = Bug.builder().assignedStaffMember(assignee2).build();
        Bug bug2 = Bug.builder().assignedStaffMember(assignee1).build();
        Bug bug3 = Bug.builder().assignedStaffMember(assignee2).build();
        Bug bug4 = Bug.builder().assignedStaffMember(assignee3).build();

        bugRepository.save(bug1);
        bugRepository.save(bug2);
        bugRepository.save(bug3);
        bugRepository.save(bug4);

        List<Bug> actual = bugRepository.findAllBugsAssignedToApplicationUser("mary_95");
        List<Bug> expected = List.of(bug1, bug3);

        assertIterableEquals(expected, actual);
    }

    @Test
    void findAllBugsAssignedToPrincipal_should_return_empty_list_when_no_bugs_assigned_to_provided_user() {
        ApplicationUser assignee1 = new ApplicationUser("johnny", "john", "doe", "johndoe@gmail.com", "password");
        ApplicationUser assignee2 = new ApplicationUser("mary_95", "maria", "brown", "mariabrown@gmail.com", "password");
        ApplicationUser assignee3 = new ApplicationUser("elephant", "elsa", "lark", "elsalark@gmail.com", "password");

        Bug bug1 = Bug.builder().assignedStaffMember(assignee2).build();
        Bug bug2 = Bug.builder().assignedStaffMember(assignee1).build();
        Bug bug3 = Bug.builder().assignedStaffMember(assignee2).build();
        Bug bug4 = Bug.builder().assignedStaffMember(assignee3).build();

        bugRepository.save(bug1);
        bugRepository.save(bug2);
        bugRepository.save(bug3);
        bugRepository.save(bug4);

        List<Bug> actual = bugRepository.findAllBugsAssignedToApplicationUser("");
        List<Bug> expected = Collections.emptyList();

        assertIterableEquals(expected, actual);
    }

    @Test
    void findByUsernameOfReporter_should_return_list_of_bugs_reportedBy_provided_user() {
        ApplicationUser reporter1 = new ApplicationUser("johnny", "john", "doe", "johndoe@gmail.com", "password");
        ApplicationUser reporter2 = new ApplicationUser("mary_95", "maria", "brown", "maria.brown@gmail.com", "password");
        ApplicationUser reporter3 = new ApplicationUser("elephant", "elsa", "lark", "elsalark@gmail.com", "password");
        Bug bug1 = Bug.builder().reporterOfBug(reporter1).build();
        Bug bug2 = Bug.builder().reporterOfBug(reporter2).build();
        Bug bug3 = Bug.builder().reporterOfBug(reporter3).build();
        Bug bug4 = Bug.builder().reporterOfBug(reporter1).build();

        bugRepository.save(bug1);
        bugRepository.save(bug2);
        bugRepository.save(bug3);
        bugRepository.save(bug4);

        List<Bug> actual = bugRepository.findByUsernameOfReporter("johnny");
        List<Bug> expected = List.of(bug1, bug4);

        assertIterableEquals(expected, actual);
    }

    @Test
    void findByUsernameOfReporter_should_return_empty_list_when_provided_reporter_not_having_bugs() {
        ApplicationUser reporter1 = new ApplicationUser("mary_95", "maria", "brown", "maria.brown@gmail.com", "password");
        ApplicationUser reporter2 = new ApplicationUser("elephant", "elsa", "lark", "elsalark@gmail.com", "password");
        Bug bug1 = Bug.builder().reporterOfBug(reporter1).build();
        Bug bug2 = Bug.builder().reporterOfBug(reporter1).build();
        Bug bug3 = Bug.builder().reporterOfBug(reporter2).build();
        Bug bug4 = Bug.builder().reporterOfBug(reporter2).build();

        bugRepository.save(bug1);
        bugRepository.save(bug2);
        bugRepository.save(bug3);
        bugRepository.save(bug4);

        List<Bug> actual = bugRepository.findByUsernameOfReporter("johnny");
        List<Bug> expected = Collections.emptyList();

        assertIterableEquals(expected, actual);
    }
}