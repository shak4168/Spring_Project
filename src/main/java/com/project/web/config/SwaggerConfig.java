package com.project.web.config;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;

public class SwaggerConfig {
	@Bean
    public OpenAPI openAPI() {
        return new OpenAPI()
                .components(new Components())
                .info(apiInfo());
    }

    private Info apiInfo() {
        return new Info()
                .title("Cloud Native Shopping Mall API") // 문서 제목
                .description("쇼핑몰 프로젝트 API 명세서입니다.") // 문서 설명
                .version("1.0.0"); // 버전
    }
}
