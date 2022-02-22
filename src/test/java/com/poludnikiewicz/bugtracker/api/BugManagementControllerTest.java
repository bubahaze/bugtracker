package com.poludnikiewicz.bugtracker.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poludnikiewicz.bugtracker.auth.ApplicationUser;
import com.poludnikiewicz.bugtracker.auth.ApplicationUserService;
import com.poludnikiewicz.bugtracker.bug.Bug;
import com.poludnikiewicz.bugtracker.bug.BugPriority;
import com.poludnikiewicz.bugtracker.bug.BugService;
import com.poludnikiewicz.bugtracker.bug.BugStatus;
import com.poludnikiewicz.bugtracker.bug.dto.BugRequest;
import com.poludnikiewicz.bugtracker.bug.dto.BugResponse;
import com.poludnikiewicz.bugtracker.exception.BugNotFoundException;
import com.poludnikiewicz.bugtracker.security.ApplicationUserRole;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = {"STAFF", "ADMIN"})
public class BugManagementControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    @MockBean
    BugService bugService;
    @MockBean
    ApplicationUserService userService;
    BugResponse bugResponse1 = new BugResponse(1L, "a-summary", "project1", "description1",
            LocalDateTime.of(2022, Month.FEBRUARY, 10, 14, 33, 44),
            LocalDateTime.of(2022, Month.FEBRUARY, 10, 15, 33, 44), BugStatus.RESOLVED,
            "user", "Windows10", "reporter1", BugPriority.P3_NORMAL,
            0, Collections.emptyList());
    BugResponse bugResponse2 = new BugResponse(2L, "b-summary", "project2", "description2",
            LocalDateTime.of(2022, Month.FEBRUARY, 10, 14, 33, 44),
            LocalDateTime.of(2022, Month.FEBRUARY, 10, 15, 33, 44), BugStatus.ASSIGNED,
            "assignee2", "Windows10", "reporter2", BugPriority.P2_IMPORTANT,
            0, Collections.emptyList());
    long bugId = 4L;
    ApplicationUser assignee = new ApplicationUser("CJ", "Carl", "Johnson", "johnsonc@gmail.com", "password");
    Bug bug = Bug.builder().status(BugStatus.REPORTED).build();


    @Test
    void showById_should_display_bugResponse_by_id_if_exists() throws Exception {
        when(bugService.findBugResponseById(bugResponse1.getId())).thenReturn(bugResponse1);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/manage/bugs/{bugId}", bugResponse1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.summary").value(bugResponse1.getSummary()))
                .andExpect(jsonPath("$.project").value(bugResponse1.getProject()))
                .andExpect(jsonPath("$.description").value(bugResponse1.getDescription()))
                .andExpect(jsonPath("$.status").value(bugResponse1.getStatus().toString()))
                .andExpect(jsonPath("$.usernameOfAssignee").value(bugResponse1.getUsernameOfAssignee()))
                .andExpect(jsonPath("$.opSystemWhereBugOccurred").value(bugResponse1.getOpSystemWhereBugOccurred()))
                .andExpect(jsonPath("$.usernameOfReporter").value(bugResponse1.getUsernameOfReporter()))
                .andExpect(jsonPath("$.priority").value(bugResponse1.getPriority().toString()));
    }

    @Test
    void showById_should_return_statusCode_NotFound_if_bugResponse_not_exist() throws Exception {
        when(bugService.findBugResponseById(bugResponse1.getId())).thenThrow(BugNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/manage/bugs/{bugId}", bugResponse1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void showByPriority_should_display_bugResponses_by_priority() throws Exception {
        String priority = "p2";
        when(bugService.findBugsByPriority(priority)).thenReturn(List.of(bugResponse2));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/manage/bugs")
                        .param("priority", priority)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].summary").value(bugResponse2.getSummary()))
                .andExpect(jsonPath("$[0].project").value(bugResponse2.getProject()))
                .andExpect(jsonPath("$[0].description").value(bugResponse2.getDescription()))
                .andExpect(jsonPath("$[0].status").value(bugResponse2.getStatus().toString()))
                .andExpect(jsonPath("$[0].usernameOfAssignee").value(bugResponse2.getUsernameOfAssignee()))
                .andExpect(jsonPath("$[0].opSystemWhereBugOccurred").value(bugResponse2.getOpSystemWhereBugOccurred()))
                .andExpect(jsonPath("$[0].usernameOfReporter").value(bugResponse2.getUsernameOfReporter()))
                .andExpect(jsonPath("$[0].priority").value(bugResponse2.getPriority().toString()));
    }

    @Test
    void showByPriority_should_throw_ConstraintViolationException_if_priority_param_blank() throws Exception {
        String priority = " ";

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/manage/bugs")
                        .param("priority", priority)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException))
                .andExpect(result -> assertEquals("showByPriority.priority: must not be blank", result.getResolvedException().getMessage()));
    }

    @Test
    void showBugsAssignedToPrincipal_should_display_bugResponses_assigned_to_principal() throws Exception {
        String principalName = "user";
        when(bugService.findAllBugsAssignedToApplicationUser(principalName)).thenReturn(List.of(bugResponse1));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/manage/bugs/assigned")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].summary").value(bugResponse1.getSummary()))
                .andExpect(jsonPath("$[0].project").value(bugResponse1.getProject()))
                .andExpect(jsonPath("$[0].description").value(bugResponse1.getDescription()))
                .andExpect(jsonPath("$[0].status").value(bugResponse1.getStatus().toString()))
                .andExpect(jsonPath("$[0].usernameOfAssignee").value(bugResponse1.getUsernameOfAssignee()))
                .andExpect(jsonPath("$[0].opSystemWhereBugOccurred").value(bugResponse1.getOpSystemWhereBugOccurred()))
                .andExpect(jsonPath("$[0].usernameOfReporter").value(bugResponse1.getUsernameOfReporter()))
                .andExpect(jsonPath("$[0].priority").value(bugResponse1.getPriority().toString()));
    }

    @Test
    void showBugsAssignedToUser_should_display_bugResponses_assigned_to_provided_user() throws Exception {

        String assignee = bugResponse2.getUsernameOfAssignee();
        when(bugService.findAllBugsAssignedToApplicationUser(assignee)).thenReturn(List.of(bugResponse2));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/manage/bugs/assigned-to")
                        .param("username", assignee)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].summary").value(bugResponse2.getSummary()))
                .andExpect(jsonPath("$[0].project").value(bugResponse2.getProject()))
                .andExpect(jsonPath("$[0].description").value(bugResponse2.getDescription()))
                .andExpect(jsonPath("$[0].status").value(bugResponse2.getStatus().toString()))
                .andExpect(jsonPath("$[0].usernameOfAssignee").value(assignee))
                .andExpect(jsonPath("$[0].opSystemWhereBugOccurred").value(bugResponse2.getOpSystemWhereBugOccurred()))
                .andExpect(jsonPath("$[0].usernameOfReporter").value(bugResponse2.getUsernameOfReporter()))
                .andExpect(jsonPath("$[0].priority").value(bugResponse2.getPriority().toString()));
    }

    @Test
    void showBugsAssignedToUser_should_throw_ConstraintViolationException_if_username_param_blank() throws Exception {
        String assignee = " ";

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/manage/bugs/assigned-to")
                        .param("username", assignee)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException))
                .andExpect((result -> assertEquals("showBugsAssignedToUser.username: must not be blank",
                        result.getResolvedException().getMessage())));
    }

    @Test
    void sortBugsAccordingToKey_should_display_bugs_according_to_key_and_direction() throws Exception {
        String key = "summary";
        String direction = "desc";
        when(bugService.sortBugsAccordingToKey(key, direction)).thenReturn(List.of(bugResponse2, bugResponse1));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/manage/bugs/sort")
                        .param("key", key)
                        .param("direction", direction)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].summary").value(bugResponse2.getSummary()))
                .andExpect(jsonPath("$[0].project").value(bugResponse2.getProject()))
                .andExpect(jsonPath("$[0].description").value(bugResponse2.getDescription()))
                .andExpect(jsonPath("$[0].status").value(bugResponse2.getStatus().toString()))
                .andExpect(jsonPath("$[0].usernameOfAssignee").value(bugResponse2.getUsernameOfAssignee()))
                .andExpect(jsonPath("$[0].opSystemWhereBugOccurred").value(bugResponse2.getOpSystemWhereBugOccurred()))
                .andExpect(jsonPath("$[0].usernameOfReporter").value(bugResponse2.getUsernameOfReporter()))
                .andExpect(jsonPath("$[0].priority").value(bugResponse2.getPriority().toString()))
                .andExpect(jsonPath("$[1].summary").value(bugResponse1.getSummary()))
                .andExpect(jsonPath("$[1].project").value(bugResponse1.getProject()))
                .andExpect(jsonPath("$[1].description").value(bugResponse1.getDescription()))
                .andExpect(jsonPath("$[1].status").value(bugResponse1.getStatus().toString()))
                .andExpect(jsonPath("$[1].usernameOfAssignee").value(bugResponse1.getUsernameOfAssignee()))
                .andExpect(jsonPath("$[1].opSystemWhereBugOccurred").value(bugResponse1.getOpSystemWhereBugOccurred()))
                .andExpect(jsonPath("$[1].usernameOfReporter").value(bugResponse1.getUsernameOfReporter()))
                .andExpect(jsonPath("$[1].priority").value(bugResponse1.getPriority().toString()));
    }

    @Test
    void sortBugsAccordingToKey_should_throw_ConstraintViolationException_if_key_param_blank() throws Exception {
        String key = " ";
        String direction = "desc";

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/manage/bugs/sort")
                        .param("key", key)
                        .param("direction", direction)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException))
                .andExpect((result -> assertEquals("sortBugsAccordingToKey.key: must not be blank",
                        result.getResolvedException().getMessage())));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteBug_should_invoke_deleteBug_of_BugService() throws Exception {
        Bug mockBug = mock(Bug.class);
        when(bugService.findById(bugId)).thenReturn(mockBug);
        doNothing().when(bugService).deleteBug(bugId);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/manage/bugs/{bugId}", bugId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(bugService).deleteBug(bugId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteBug_should_return_statusCode_NotFound_if_bug_not_exist() throws Exception {
        when(bugService.findById(bugId)).thenThrow(BugNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/manage/bugs/{bugId}", bugId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());

        verify(bugService, never()).deleteBug(bugId);
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void updateBug_should_update_bug_with_provided_id() throws Exception {
        BugRequest request = new BugRequest("updated-summary", "updated-project", "update-description",
                "Windows10");

        mockMvc.perform(MockMvcRequestBuilders
                        .put("/manage/bugs/{bugId}", bugId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isOk())
                .andExpect(content().string(String.format("Bug with %d successfully updated", bugId)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void assignStaffToBug_should_assign_bug_and_change_its_status_to_Assigned() throws Exception {
        assignee.setApplicationUserRole(ApplicationUserRole.STAFF);
        String assigneeUsername = "username of assignee";
        when(bugService.findById(bugId)).thenReturn(bug);
        when(userService.loadUserByUsername(assigneeUsername)).thenReturn(assignee);

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/manage/bugs/assignee/{bugId}", bugId)
                        .param("staffAssigneeUsername", assigneeUsername)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(String.format("Bug with id %d has been assigned to %s", bugId, assigneeUsername)));

        assertEquals(BugStatus.ASSIGNED, bug.getStatus());
        assertEquals(assignee, bug.getAssignedStaffMember());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void assignStaffToBug_should_assign_throw_IllegalStateException_if_assignee_not_staff_or_admin() throws Exception {
        bug.setStatus(BugStatus.REPORTED);
        assignee.setApplicationUserRole(ApplicationUserRole.USER);
        String assigneeUsername = "username of assignee";
        when(bugService.findById(bugId)).thenReturn(bug);
        when(userService.loadUserByUsername(assigneeUsername)).thenReturn(assignee);

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/manage/bugs/assignee/{bugId}", bugId)
                        .param("staffAssigneeUsername", assigneeUsername)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof IllegalStateException))
                .andExpect(result -> assertEquals("Assignee must be of role STAFF or ADMIN",
                        result.getResolvedException().getMessage()));

        assertEquals(BugStatus.REPORTED, bug.getStatus());
        assertNull(bug.getAssignedStaffMember());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void assignStaffToBug_should_assign_throw_ConstraintViolationException_if_param_blank() throws Exception {
        bug.setStatus(BugStatus.REPORTED);
        assignee.setApplicationUserRole(ApplicationUserRole.USER);
        String assigneeUsername = " ";
        when(bugService.findById(bugId)).thenReturn(bug);
        when(userService.loadUserByUsername(assigneeUsername)).thenReturn(assignee);

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/manage/bugs/assignee/{bugId}", bugId)
                        .param("staffAssigneeUsername", assigneeUsername)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException))
                .andExpect(result -> assertEquals("assignStaffToBug.staffAssigneeUsername: must not be blank",
                        result.getResolvedException().getMessage()));

        assertEquals(BugStatus.REPORTED, bug.getStatus());
        assertNull(bug.getAssignedStaffMember());
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void assignBugToPrincipal_should_assign_bug_with_provided_id_to_principal() throws Exception {
        bug.setStatus(BugStatus.REPORTED);
        assignee.setApplicationUserRole(ApplicationUserRole.STAFF);
        String assigneeUsername = "user";
        when(bugService.findById(bugId)).thenReturn(bug);
        when(userService.loadUserByUsername(assigneeUsername)).thenReturn(assignee);

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/manage/bugs/staff/assignee/{bugId}", bugId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(String.format("Bug with id %d has been assigned to you", bugId)));

        assertEquals(BugStatus.ASSIGNED, bug.getStatus());
        assertEquals(assignee, bug.getAssignedStaffMember());
    }

    @Test
    void setPriorityOfBug_should_set_priority_from_param_and_save_bug() throws Exception {
        String priority = "p1";
        when(bugService.findById(bugId)).thenReturn(bug);

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/manage/bugs/priority/{bugId}", bugId)
                        .param("priority", priority)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(String.format("Priority successfully set to %s", priority)));

        assertEquals(BugPriority.P1_CRITICAL, bug.getPriority());
        verify(bugService).saveBug(bug);
    }

    @Test
    void setPriorityOfBug_should_throw_ConstraintViolationException_if_priority_param_blank() throws Exception {
        bug.setPriority(BugPriority.UNSET);
        String priority = " ";
        when(bugService.findById(bugId)).thenReturn(bug);

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/manage/bugs/priority/{bugId}", bugId)
                        .param("priority", priority)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException))
                .andExpect(result -> assertEquals("setPriorityOfBug.priority: must not be blank",
                        result.getResolvedException().getMessage()));

        assertEquals(BugPriority.UNSET, bug.getPriority());
        verify(bugService, never()).saveBug(bug);
    }

    @Test
    void setPriorityOfBug_should_throw_IllegalArgumentException_if_priority_param_incorrect() throws Exception {
        bug.setPriority(BugPriority.UNSET);
        String priority = "crucial";
        when(bugService.findById(bugId)).thenReturn(bug);

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/manage/bugs/priority/{bugId}", bugId)
                        .param("priority", priority)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof IllegalArgumentException))
                .andExpect(result -> assertEquals("Provided priority type does not exist",
                        result.getResolvedException().getMessage()));

        verify(bugService, never()).saveBug(bug);
    }

    @Test
    void setStatusOfBug_should_set_status_from_param_and_saveBug() throws Exception {
        String status = "resolved";
        when(bugService.findById(bugId)).thenReturn(bug);

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/manage/bugs/{bugId}", bugId)
                        .param("status", status)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(String.format("Status successfully set to %s", status.toUpperCase())));

        assertEquals(BugStatus.RESOLVED, bug.getStatus());
        verify(bugService).saveBug(bug);
    }

    @Test
    void setStatusOfBug_should_throw_ConstraintViolationException_if_status_param_blank() throws Exception {
        bug.setStatus(BugStatus.REPORTED);
        String status = " ";
        when(bugService.findById(bugId)).thenReturn(bug);

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/manage/bugs/{bugId}", bugId)
                        .param("status", status)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException))
                .andExpect(result -> assertEquals("setStatusOfBug.status: must not be blank",
                        result.getResolvedException().getMessage()));

        assertEquals(BugStatus.REPORTED, bug.getStatus());
        verify(bugService, never()).saveBug(bug);
    }

    @Test
    void setStatusOfBug_should_throw_Exception_if_status_param_incorrect() throws Exception {
        bug.setStatus(BugStatus.REPORTED);
        String status = "qwerty";
        when(bugService.findById(bugId)).thenReturn(bug);

        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/manage/bugs/{bugId}", bugId)
                        .param("status", status)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof IllegalArgumentException));

        assertEquals(BugStatus.REPORTED, bug.getStatus());
        verify(bugService, never()).saveBug(bug);
    }
}