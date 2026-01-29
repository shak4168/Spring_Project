package com.project.web.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Lazy;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import com.project.web.config.jwt.JwtAuthenticationFilter;
import com.project.web.config.jwt.JwtTokenProvider;

import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
//@RequiredArgsConstructor // final 필드 주입
public class SecurityConfig {

    // JWT 검증을 위해 필요
    private final JwtTokenProvider jwtTokenProvider;
 // 의미: JwtTokenProvider는 급하게 만들지 말고, 필요할 때 천천히 주입
    public SecurityConfig(@Lazy JwtTokenProvider jwtTokenProvider) {
        this.jwtTokenProvider = jwtTokenProvider;
    }
    @Bean
    public BCryptPasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
            // 1. CSRF 해제
            .csrf(csrf -> csrf.disable())
            
            // 2. 세션 사용 안 함 (Stateless)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

            // 3. URL별 권한 관리
            .authorizeHttpRequests(auth -> auth
                //  누구나 접근 가능 (정적 리소스 포함)
                .requestMatchers("/", "/index.html", "/login.html", "/join.html", "/api/members/**").permitAll()
                .requestMatchers("/css/**", "/js/**", "/images/**", "/favicon.ico").permitAll() // css, js, 이미지 허용
                .requestMatchers(HttpMethod.GET, "/api/items").permitAll()
                .requestMatchers("/item-add.html").permitAll() // 상품 등록 페이지
                .requestMatchers(HttpMethod.POST, "/api/items").hasAnyRole("SELLER", "ADMIN") // 상품 등록 API
                
                // 그 외 모든 요청은 인증 필요
                .anyRequest().authenticated()
            )
            

            // 4. JWT 인증 필터를 UsernamePasswordAuthenticationFilter 앞에 추가
            // 이 줄이 없으면 로그인을 해도 토큰 검사를 안 해서 403
            
            .exceptionHandling(exception -> exception
                    .authenticationEntryPoint(unauthorizedHandler()) // 401: 인증 안됨
                    .accessDeniedHandler(accessDeniedHandler())     // 403: 권한 없음
                )
            .addFilterBefore(new JwtAuthenticationFilter(jwtTokenProvider), UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }
 // 401 Unauthorized 처리 (인증 실패)
    private AuthenticationEntryPoint unauthorizedHandler() {
        return (request, response, authException) -> {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
            response.getWriter().write("{\"error\": \"로그인이 필요합니다.\"}");
        };
    }

    // 403 Forbidden 처리 (권한 부족)
    private AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setContentType("application/json;charset=UTF-8");
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.getWriter().write("{\"error\": \"해당 메뉴에 대한 접근 권한이 없습니다.\"}");
        };
    }
}