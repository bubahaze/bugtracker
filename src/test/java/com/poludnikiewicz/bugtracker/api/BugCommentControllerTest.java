package com.poludnikiewicz.bugtracker.api;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.poludnikiewicz.bugtracker.bug.comment.BugCommentService;
import com.poludnikiewicz.bugtracker.bug.comment.dto.BugCommentRequest;
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

import static org.hamcrest.Matchers.notNullValue;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@SpringBootTest
@AutoConfigureMockMvc
class BugCommentControllerTest {

    @Autowired
    MockMvc mockMvc;
    @Autowired
    ObjectMapper mapper;
    @MockBean
    BugCommentService commentService;
    long commentId = 5L;
    String content = "content of comment";

    @Test
    @WithMockUser(roles = {"USER", "STAFF", "ADMIN"})
    void postBugComment_should_addComment_and_sendNotificationEmailToBugReporterAndAssignee() throws Exception {
        BugCommentRequest request = new BugCommentRequest(content);
        long bugId = 3L;
        String author = "user";

        mockMvc.perform(MockMvcRequestBuilders.post("/{bugId}/comments", bugId)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .content(mapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$", notNullValue()))
                .andExpect(content().string("Comment posted to Bug with id " + bugId));

        verify(commentService).addComment(bugId, request, author);
        verify(commentService).sendNotificationEmailToBugReporterAndAssignee(author, bugId, request.getContent());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "STAFF"})
    void deleteBugComment_should_invoke_deleteBugComment_of_BugCommentService() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.delete("/manage/comments/{commentId}", commentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isNoContent());

        verify(commentService).deleteBugComment(commentId);
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "STAFF"})
    void deleteBugComment_should_return_statusCode_badRequest_if_comment_not_exist() throws Exception {
        doThrow(IllegalArgumentException.class).when(commentService).deleteBugComment(commentId);

        mockMvc.perform(MockMvcRequestBuilders.delete("/manage/comments/{commentId}", commentId)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "STAFF"})
    void updateBugComment_should_invoke_updateBugComment_of_BugCommentService() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders.patch("/manage/comments/{commentId}", commentId)
                        .param("content", content)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isNoContent());

        verify(commentService).updateBugComment(commentId, content);
    }

    @Test
    @WithMockUser(roles = {"ADMIN", "STAFF"})
    void updateBugComment_should_return_statusCode_BadRequest_if_content_is_blank() throws Exception {
        String blankContent = " ";

        mockMvc.perform(MockMvcRequestBuilders.patch("/manage/comments/{commentId}", commentId)
                        .param("content", blankContent)
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest());

        verify(commentService, never()).updateBugComment(commentId, content);
    }
}