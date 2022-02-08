package com.poludnikiewicz.bugtracker.controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class DashboardControllerTest {

    DashboardController dashboardController;
    MockMvc mockMvc;

    @BeforeEach
    void setup() {
        dashboardController = new DashboardController();
        this.mockMvc = MockMvcBuilders.standaloneSetup(new DashboardController()).build();
    }

    @Test
    void admin_should_return_modelAndView_of_admin() throws Exception {
        mockMvc.perform(get("/admin")).andExpect(view().name("admin-dashboard"));
    }

    @Test
    void user_should_return_modelAndView_of_user() throws Exception {
        mockMvc.perform(get("/user")).andExpect(view().name("user-dashboard"));
    }

    @Test
    void staff_should_return_modelAndView_of_staff() throws Exception {
        mockMvc.perform(get("/staff")).andExpect(view().name("staff-dashboard"));
    }
}