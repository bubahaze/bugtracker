package com.poludnikiewicz.bugtracker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
public class DashboardController {


    @GetMapping("/admin")
    public ModelAndView admin() {
        ModelAndView model = new ModelAndView();
        model.setViewName("admin-dashboard");
        return model;
    }

    @GetMapping("/user")
    public ModelAndView user() {
        ModelAndView model = new ModelAndView();
        model.setViewName("user-dashboard");
        return model;
    }

    @GetMapping("/staff")
    public ModelAndView staff() {
        ModelAndView model = new ModelAndView();
        model.setViewName("staff-dashboard");
        return model;
    }
}
