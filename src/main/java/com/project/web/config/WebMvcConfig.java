package com.project.web.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebMvcConfig implements WebMvcConfigurer {

    // 기본값은 윈도우용으로 두되, 주입받을 수 있게 설정
    @Value("${file.dir:C:/web-project/images/}")
    private String fileDir;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // file:/// 대신 file: 을 사용하세요 (리눅스 호환성 UP)
        registry.addResourceHandler("/images/**")
                .addResourceLocations("file:" + fileDir);
    }
}