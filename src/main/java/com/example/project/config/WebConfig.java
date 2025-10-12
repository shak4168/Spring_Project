package com.example.project.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.web.servlet.config.annotation.*;

@Configuration
@EnableWebMvc
@ComponentScan(basePackages = "com.example.project")
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addCorsMappings(CorsRegistry reg) {
        reg.addMapping("/api/**")
           .allowedOrigins("http://localhost:5173") // React 연결 대비
           .allowedMethods("GET", "POST", "PUT", "DELETE");
    }
}