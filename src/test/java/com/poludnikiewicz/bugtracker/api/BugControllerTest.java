package com.poludnikiewicz.bugtracker.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poludnikiewicz.bugtracker.bug.Bug;
import com.poludnikiewicz.bugtracker.bug.BugPriority;
import com.poludnikiewicz.bugtracker.bug.BugService;
import com.poludnikiewicz.bugtracker.bug.BugStatus;
import com.poludnikiewicz.bugtracker.bug.comment.dto.BugCommentRequest;
import com.poludnikiewicz.bugtracker.bug.dto.BugRequest;
import com.poludnikiewicz.bugtracker.bug.dto.BugResponse;
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
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import javax.validation.ConstraintViolationException;
import java.time.LocalDateTime;
import java.time.Month;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.Matchers.notNullValue;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SpringBootTest
@AutoConfigureMockMvc
@WithMockUser(roles = {"USER", "STAFF", "ADMIN"})
class BugControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    BugService bugService;
    @Autowired
    ObjectMapper mapper;
    BugResponse bugResponse1 = new BugResponse(1L, "summary1", "project1", "description1",
            LocalDateTime.of(2022, Month.FEBRUARY, 10, 14, 33, 44),
            LocalDateTime.of(2022, Month.FEBRUARY, 10, 15, 33, 44), BugStatus.RESOLVED,
            "assignee1", "Windows10", "reporter1", BugPriority.P3_NORMAL, 0, Collections.emptyList());
    BugResponse bugResponse2 = new BugResponse(2L, "summary2", "project2", "description2",
            LocalDateTime.of(2022, Month.FEBRUARY, 10, 14, 33, 44),
            LocalDateTime.of(2022, Month.FEBRUARY, 10, 15, 33, 44), BugStatus.ASSIGNED,
            "assignee2", "Windows10", "reporter2", BugPriority.P2_IMPORTANT, 0, Collections.emptyList());


    @Test
    void showAllBugs_should_display_all_bugResponses() throws Exception {
        when(bugService.findAllBugs()).thenReturn(List.of(bugResponse1, bugResponse2));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/bugs")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].summary").value(bugResponse1.getSummary()))
                .andExpect(jsonPath("$[0].project").value(bugResponse1.getProject()))
                .andExpect(jsonPath("$[0].description").value(bugResponse1.getDescription()))
                .andExpect(jsonPath("$[0].status").value(bugResponse1.getStatus().toString()))
                .andExpect(jsonPath("$[0].usernameOfAssignee").value(bugResponse1.getUsernameOfAssignee()))
                .andExpect(jsonPath("$[0].opSystemWhereBugOccurred").value(bugResponse1.getOpSystemWhereBugOccurred()))
                .andExpect(jsonPath("$[0].usernameOfReporter").value(bugResponse1.getUsernameOfReporter()))
                .andExpect(jsonPath("$[0].priority").value(bugResponse1.getPriority().toString()))
                .andExpect(jsonPath("$[1].summary").value(bugResponse2.getSummary()))
                .andExpect(jsonPath("$[1].project").value(bugResponse2.getProject()))
                .andExpect(jsonPath("$[1].description").value(bugResponse2.getDescription()))
                .andExpect(jsonPath("$[1].status").value(bugResponse2.getStatus().toString()))
                .andExpect(jsonPath("$[1].usernameOfAssignee").value(bugResponse2.getUsernameOfAssignee()))
                .andExpect(jsonPath("$[1].opSystemWhereBugOccurred").value(bugResponse2.getOpSystemWhereBugOccurred()))
                .andExpect(jsonPath("$[1].usernameOfReporter").value(bugResponse2.getUsernameOfReporter()))
                .andExpect(jsonPath("$[1].priority").value(bugResponse2.getPriority().toString()));

    }

    @Test
    void showById_should_display_bugResponse_by_id_if_exists() throws Exception {
        when(bugService.findBugResponseById(bugResponse1.getId())).thenReturn(bugResponse1);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/bugs/{bugId}", bugResponse1.getId())
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
                        .get("/bugs/{bugId}", bugResponse1.getId())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNotFound());
    }

    @Test
    void searchByProject_should_return_BugResponses_with_provided_project() throws Exception {
        String project = "project1";
        when(bugService.findByProject(project)).thenReturn(List.of(bugResponse1));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/bugs/search/project")
                        .param("project", project)
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
    void searchByProject_should_throw_ConstraintViolation_Exception_if_project_param_blank() throws Exception {
        String project = " ";

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/bugs/search/project")
                        .param("project", project)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException))
                .andExpect(result -> assertEquals("searchByProject.project: must not be blank", result.getResolvedException().getMessage()));
    }

    @Test
    void searchByKeyword_should_return_BugResponses_with_provided_keyword() throws Exception {
        String keyword = "description1";
        when(bugService.findByKeyword(keyword)).thenReturn(List.of(bugResponse1));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/bugs/search/keyword")
                        .param("keyword", keyword)
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
    void searchByKeyword_should_throw_ConstraintViolationException_if_keyword_param_blank() throws Exception {
        String keyword = " ";

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/bugs/search/keyword")
                        .param("keyword", keyword)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertTrue(result.getResolvedException() instanceof ConstraintViolationException))
                .andExpect(result -> assertEquals("searchByKeyword.keyword: must not be blank", result.getResolvedException().getMessage()));
    }

    @Test
    void showBugsReportedByPrincipal_should_return_bugResponses_reported_by_principal() throws Exception {
        String reporter = "user";
        when(bugService.findByReporter(reporter)).thenReturn(List.of(bugResponse1));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/bugs/reported")
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
    void postBug_should_invoke_addBug_of_BugService() throws Exception {
        BugRequest request = new BugRequest("summary", "project", "description", "Windows10");
        String author = "user";
        long bugId = 4L;
        when(bugService.addBug(request, author)).thenReturn(bugId);

        mockMvc.perform(MockMvcRequestBuilders.post("/bugs")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(content().string("Bug successfully reported. ID of bug: " + bugId));

        verify(bugService).addBug(request, author);
    }
}