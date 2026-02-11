package com.project.web.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import io.swagger.v3.oas.annotations.tags.Tag;
@Tag(name = "0. 공통(Home)", description = "루트 접속 시 메인 페이지로 리다이렉트합니다.")
@Controller
public class HomeController {
    @GetMapping("/")
    public String home() {
        // 무조건 index.html로 강제 이동 (리다이렉트)
        return "redirect:/index.html";
    }
}