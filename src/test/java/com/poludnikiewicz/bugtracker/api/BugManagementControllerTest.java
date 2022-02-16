package com.poludnikiewicz.bugtracker.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poludnikiewicz.bugtracker.auth.ApplicationUserService;
import com.poludnikiewicz.bugtracker.bug.Bug;
import com.poludnikiewicz.bugtracker.bug.BugPriority;
import com.poludnikiewicz.bugtracker.bug.BugService;
import com.poludnikiewicz.bugtracker.bug.BugStatus;
import com.poludnikiewicz.bugtracker.bug.dto.BugRequest;
import com.poludnikiewicz.bugtracker.bug.dto.BugResponse;
import com.poludnikiewicz.bugtracker.email.EmailService;
import com.poludnikiewicz.bugtracker.exception.BugNotFoundException;
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
import org.springframework.security.core.Authentication;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.validation.ConstraintViolationException;
import java.security.Principal;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.notNullValue;
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
    EmailService emailService;
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


    @Test
    void showById_should_show_bugResponse_by_id_if_exists() throws Exception {
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
    void showByPriority_should_show_bugResponses_by_priority() throws Exception {
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
    void showByPriority_should_return_statusCode_BadRequest_if_priority_param_blank() throws Exception {
        String priority = " ";
        when(bugService.findBugsByPriority(priority)).thenThrow(ConstraintViolationException.class);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/manage/bugs")
                        .param("priority", priority)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void showBugsAssignedToPrincipal_should_show_bugResponses_assigned_to_principal() throws Exception {
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
    void showBugsAssignedToUser_should_show_bugResponses_assigned_to_provided_user() throws Exception {

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
    void showBugsAssignedToUser_should_return_statusCode_BadRequest_if_user_param_blank() throws Exception {
        String assignee = " ";
        when(bugService.findAllBugsAssignedToApplicationUser(assignee)).thenThrow(ConstraintViolationException.class);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/manage/bugs/assigned-to")
                        .param("username", assignee)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void sortBugsAccordingToKey_should_show_bugs_according_to_key_and_direction() throws Exception {
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
    void sortBugsAccordingToKey_should_return_statusCode_BadRequest_when_key_param_blank() throws Exception {
        String key = " ";
        String direction = "desc";
        when(bugService.sortBugsAccordingToKey(key, direction)).thenThrow(ConstraintViolationException.class);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/manage/bugs/sort")
                        .param("key", key)
                        .param("direction", direction)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteBug_should_invoke_deleteBug_of_BugService() throws Exception {
        Bug bug = mock(Bug.class);
        when(bugService.findById(bugId)).thenReturn(bug);
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
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(content().string(String.format("Bug with %d successfully updated", bugId)));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void assignStaffToBug() {
    }

    @Test
    void assignBugToPrincipal() {
    }

    @Test
    void setPriorityOfBug() {
    }

    @Test
    void setStatusOfBug() {
    }
}