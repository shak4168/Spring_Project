package com.project.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    @Value("${file.dir:C:/web-project/images/}")
    private String fileDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // /images/** 로 시작하는 요청은 로컬 폴더에서 찾는다.
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:///" + fileDir);
    }
}