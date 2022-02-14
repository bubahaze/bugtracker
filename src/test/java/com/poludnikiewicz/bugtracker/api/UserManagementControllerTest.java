package com.poludnikiewicz.bugtracker.api;

import com.poludnikiewicz.bugtracker.auth.ApplicationUserResponse;
import com.poludnikiewicz.bugtracker.auth.ApplicationUserService;
import com.poludnikiewicz.bugtracker.security.ApplicationUserRole;
import org.junit.jupiter.api.Assertions;
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
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.List;

import static org.mockito.Mockito.doNothing;
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
    ApplicationUserResponse user1 = new ApplicationUserResponse("johnny", "johndoe@gmail.com",
            ApplicationUserRole.USER, true, Collections.emptyList(), Collections.emptyList());
    ApplicationUserResponse user2 = new ApplicationUserResponse("maryc", "mariacaley@gmail.com",
            ApplicationUserRole.USER, true, Collections.emptyList(), Collections.emptyList());

    @Test
    @WithMockUser(roles = {"STAFF", "ADMIN"})
    void showByUsername_should_return_user_by_username_for_staff_or_admin_users() throws Exception {
        when(userService.findApplicationUserResponseByUsername(user1.getUsername())).thenReturn(user1);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/manage/users/{username}", "johnny")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("johnny"))
                .andExpect(jsonPath("$.email").value("johndoe@gmail.com"))
                .andExpect(jsonPath("$.applicationUserRole").value("USER"))
                .andExpect(jsonPath("$.enabled").value(true));
    }

    @Test
    @WithMockUser(roles = {"STAFF", "ADMIN"})
    void showAllUsers_should_return_all_users_for_staff_or_admin_users() throws Exception {
        when(userService.findAllUsers()).thenReturn(List.of(user1, user2));

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/manage/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].username").value("johnny"))
                .andExpect(jsonPath("$[0].email").value("johndoe@gmail.com"))
                .andExpect(jsonPath("$[0].applicationUserRole").value("USER"))
                .andExpect(jsonPath("$[0].enabled").value(true))
                .andExpect(jsonPath("$[1].username").value("maryc"))
                .andExpect(jsonPath("$[1].email").value("mariacaley@gmail.com"))
                .andExpect(jsonPath("$[1].applicationUserRole").value("USER"))
                .andExpect(jsonPath("$[1].enabled").value(true));
    }

    @Test
    @WithMockUser(roles = {"STAFF", "ADMIN"})
    void showByRole_should_show_users_with_role_provided_for_staff_and_admin_users() throws Exception {
        when(userService.findByRole("user")).thenReturn(List.of(user1, user2));
        mockMvc.perform(MockMvcRequestBuilders
                        .get("/manage/users/role")
                        .param("role", "user")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.size()").value(2))
                .andExpect(jsonPath("$[0].username").value("johnny"))
                .andExpect(jsonPath("$[0].email").value("johndoe@gmail.com"))
                .andExpect(jsonPath("$[0].applicationUserRole").value("USER"))
                .andExpect(jsonPath("$[0].enabled").value(true))
                .andExpect(jsonPath("$[1].username").value("maryc"))
                .andExpect(jsonPath("$[1].email").value("mariacaley@gmail.com"))
                .andExpect(jsonPath("$[1].applicationUserRole").value("USER"))
                .andExpect(jsonPath("$[1].enabled").value(true));

    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void setRoleOfApplicationUser() throws Exception {
        when(userService.loadUserByUsername(user1.getUsername())).thenReturn((UserDetails) user1);
        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/manage/users/role?username=johnny?role=staff")
                        .param("username", "johnny")
                        .param("role", "staff")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isOk());
        Assertions.fail();
        //TODO:


    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteApplicationUser_should_delete_user_by_id_for_admin_users() {
      //  doNothing(userService.deleteApplicationUserByUsername(user2.getUsername())).
    }
}