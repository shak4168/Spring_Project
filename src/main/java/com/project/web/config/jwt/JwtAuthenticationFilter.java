package com.project.web.config.jwt;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

import java.io.IOException;

@RequiredArgsConstructor
public class JwtAuthenticationFilter extends GenericFilterBean {

    private final JwtTokenProvider jwtTokenProvider;

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpRequest = (HttpServletRequest) request;
        
        // 1. 요청 URL 확인
        String requestURI = httpRequest.getRequestURI();
        System.out.println("=== 필터 실행됨: " + requestURI + " ===");

        // 2. 토큰 추출 시도
        String token = resolveToken(httpRequest);
        System.out.println("1. 추출된 토큰: " + (token != null ? "존재함 (앞자리: " + token.substring(0, Math.min(10, token.length())) + "...)" : "NULL"));

        // 3. 유효성 검사 시도
        if (token != null) {
            boolean isValid = jwtTokenProvider.validateToken(token);
            System.out.println("2. 토큰 유효성 검사 결과: " + isValid);

            if (isValid) {
                // 4. 인증 정보 생성
                Authentication authentication = jwtTokenProvider.getAuthentication(token);
                System.out.println("3. 인증 객체 생성 완료: " + authentication.getName());
                
                SecurityContextHolder.getContext().setAuthentication(authentication);
                System.out.println("4. SecurityContext에 저장 완료!");
            }
        } else {
            System.out.println("2. 토큰이 없어서 검사 건너뜀");
        }

        chain.doFilter(request, response);
    }

    // Request Header에서 토큰 정보 추출 ("Bearer " 제거하고 토큰만 꺼내옴)
    private String resolveToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
}