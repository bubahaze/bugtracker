package com.poludnikiewicz.bugtracker.api;

import com.poludnikiewicz.bugtracker.auth.ApplicationUser;
import com.poludnikiewicz.bugtracker.auth.ApplicationUserResponse;
import com.poludnikiewicz.bugtracker.auth.ApplicationUserService;
import com.poludnikiewicz.bugtracker.exception.ApplicationUserNotFoundException;
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
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(MockitoExtension.class)
@DisplayNameGeneration(DisplayNameGenerator.ReplaceUnderscores.class)
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
                        .get("/manage/users/{username}", user1.getUsername())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("johnny"))
                .andExpect(jsonPath("$.email").value("johndoe@gmail.com"))
                .andExpect(jsonPath("$.applicationUserRole").value("USER"))
                .andExpect(jsonPath("$.enabled").value(true));
    }

    @Test
    @WithMockUser(roles = {"STAFF", "ADMIN"})
    void showByUsername_should_return_statusCode_BadRequest_if_user_not_exist() throws Exception {
        String username = "not-existing-user";
        when(userService.findApplicationUserResponseByUsername(username))
                .thenThrow(ApplicationUserNotFoundException.class);

        mockMvc.perform(MockMvcRequestBuilders
                        .get("/manage/users/{username}", username)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
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
    void showByRole_should_display_users_with_role_provided_for_staff_and_admin_users() throws Exception {
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
    void setRoleOfApplicationUser_should_set_role_for_admin_users() throws Exception {
        ApplicationUser user = new ApplicationUser("johnny", "John", "Doe", "johndoe@gmail.com", "password");
        when(userService.loadUserByUsername(user.getUsername())).thenReturn(user);
        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/manage/users/role")
                        .param("username", user.getUsername())
                        .param("role", "staff")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isOk())
                .andExpect(content().string(String.format("%s has now the role of %s", user.getUsername(), "staff")));

        verify(userService).saveApplicationUser(any(ApplicationUser.class));
        assertEquals(ApplicationUserRole.STAFF, user.getApplicationUserRole());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void setRoleOfApplicationUser_should_return_statusCode_badRequest_if_user_not_exist() throws Exception {
        when(userService.loadUserByUsername(user2.getUsername())).thenThrow(UsernameNotFoundException.class);
        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/manage/users/role")
                        .param("username", user2.getUsername())
                        .param("role", "staff")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest());

        verify(userService, never()).saveApplicationUser(any(ApplicationUser.class));
        assertEquals(ApplicationUserRole.USER, user2.getApplicationUserRole());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void setRoleOfApplicationUser_should_return_statusCode_badRequest_if_role_not_exist() throws Exception {
        mockMvc.perform(MockMvcRequestBuilders
                        .patch("/manage/users/role")
                        .param("username", user1.getUsername())
                        .param("role", "qwerty")
                        .contentType(MediaType.APPLICATION_JSON)
                        .accept(MediaType.APPLICATION_JSON)
                        .characterEncoding("UTF-8"))
                .andExpect(status().isBadRequest())
                .andExpect(result -> assertEquals("Provided role does not exist.", result.getResolvedException().getMessage()));

        verify(userService, never()).saveApplicationUser(any(ApplicationUser.class));
        assertEquals(ApplicationUserRole.USER, user1.getApplicationUserRole());
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteApplicationUser_should_delete_user_by_id_for_admin_users() throws Exception {

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/manage/users/{username}", user2.getUsername())
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(content().string(String.format("Application User with username %s successfully deleted", user2.getUsername())));
    }

    @Test
    @WithMockUser(roles = "ADMIN")
    void deleteApplicationUser_should_should_return_statusCode_BadRequest_if_user_not_exist() throws Exception {
        String username = "not-existing-user";
        doThrow(ApplicationUserNotFoundException.class).when(userService).deleteApplicationUserByUsername(username);

        mockMvc.perform(MockMvcRequestBuilders
                        .delete("/manage/users/{username}", username)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }
}