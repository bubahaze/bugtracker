package com.poludnikiewicz.bugtracker.controller;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

class TemplateControllerTest {

    TemplateController templateController;
    MockMvc mockMvc;

    @Test
    void displayLoginPage_should_display_modelAndView_of_loginpage() throws Exception {
        templateController = new TemplateController();
        mockMvc = MockMvcBuilders.standaloneSetup(new TemplateController()).build();
        mockMvc.perform(get("/login")).andExpect(view().name("login-page"))
                .andExpect(header().string("Content-type", "text/html"));
    }
}