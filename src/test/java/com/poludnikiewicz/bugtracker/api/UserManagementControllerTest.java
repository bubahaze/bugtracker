package com.poludnikiewicz.bugtracker.api;

import com.poludnikiewicz.bugtracker.auth.ApplicationUser;
import com.poludnikiewicz.bugtracker.auth.ApplicationUserResponse;
import com.poludnikiewicz.bugtracker.auth.ApplicationUserService;
import com.poludnikiewicz.bugtracker.bug.comment.BugCommentService;
import com.poludnikiewicz.bugtracker.security.ApplicationUserRole;
import org.junit.jupiter.api.DisplayNameGeneration;
import org.junit.jupiter.api.DisplayNameGenerator;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
//@WebMvcTest(UserManagementController.class)
@SpringBootTest
@AutoConfigureMockMvc
class UserManagementControllerTest {

    @Autowired
    MockMvc mockMvc;
    @MockBean
    ApplicationUserService userService;

    @Test
    void showByUsername() {
    }

    @Test
    @WithMockUser(roles = "STAFF")
    void showAllUsers_should_return_all_users_for_staff_or_admin_users() throws Exception {
        ApplicationUserResponse user = new ApplicationUserResponse("johnny", "johndoe@gmail.com",
                ApplicationUserRole.USER, true, Collections.emptyList(), Collections.emptyList());

        when(userService.findAllUsers()).thenReturn(List.of(user));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/manage/users"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(1))
                .andExpect(jsonPath("$[0].username").value("johnny"))
                .andExpect(jsonPath("$[0].email").value("johndoe@gmail.com"))
                .andExpect(jsonPath("$[0].applicationUserRole").value("USER"))
                .andExpect(jsonPath("$[0].enabled").value(true));
    }

    @Test
    void showByRole() {
    }

    @Test
    void setRoleOfApplicationUser() {
    }

    @Test
    void deleteApplicationUser() {
    }
}