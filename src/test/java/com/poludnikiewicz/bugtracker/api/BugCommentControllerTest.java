package com.poludnikiewicz.bugtracker.api;

import com.poludnikiewicz.bugtracker.bug.comment.BugCommentService;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.junit.jupiter.api.Assertions.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
@WebMvcTest(BugCommentController.class)
class BugCommentControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    BugCommentService commentService;

    @Test
    void postBugComment() {
    }

    @Test
    void deleteBugComment() {
    }

    @Test
    void updateBugComment() {
    }
}