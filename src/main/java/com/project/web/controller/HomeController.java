package com.project.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {
    @GetMapping("/")
    public String home() {
        // 무조건 index.html로 강제 이동 (리다이렉트)
        return "redirect:/index.html";
    }
}