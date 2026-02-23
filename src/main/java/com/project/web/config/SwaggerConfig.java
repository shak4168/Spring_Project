package com.project.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;

@Configuration // 스프링 부트가 이 클래스를 설정 파일로 인식
public class SwaggerConfig {

    @Bean
    public OpenAPI openAPI() {
        String jwtSchemeName = "jwtAuth";
        
        // 1. API 요청 헤더에 JWT 토큰을 포함시키도록 요구사항 설정
        SecurityRequirement securityRequirement = new SecurityRequirement().addList(jwtSchemeName);
        
        // 2. Security 스키마 설정 (Bearer 토큰 방식 명시)
        Components components = new Components()
                .addSecuritySchemes(jwtSchemeName, new SecurityScheme()
                        .name(jwtSchemeName)
                        .type(SecurityScheme.Type.HTTP) // HTTP 방식
                        .scheme("bearer")
                        .bearerFormat("JWT")); // 토큰 형식을 JWT로 지정

        // 3. 기존 API 정보(Info)와 Security 설정을 하나로 합쳐서 반환
        return new OpenAPI()
                .info(apiInfo()) // 제목과 설명 유지
                .addSecurityItem(securityRequirement) // 자물쇠 버튼 추가
                .components(components); 
    }

    private Info apiInfo() {
        return new Info()
                .title("Cloud Native Shopping Mall API") // 문서 제목
                .description("쇼핑몰 프로젝트 API 명세서입니다.") // 문서 설명
                .version("1.0.0"); // 버전
    }
}