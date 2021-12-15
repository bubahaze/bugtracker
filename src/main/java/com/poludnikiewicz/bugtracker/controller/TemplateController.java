package com.poludnikiewicz.bugtracker.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;

@Controller
@RequestMapping("/")
public class TemplateController {

    @GetMapping("login")
    public ModelAndView displayLoginPage(HttpServletResponse response) {
        response.addHeader("Content-Type", "text/html");
        return new ModelAndView("login");
    }


}
