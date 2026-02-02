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
     // CSRF 해제
        .csrf(csrf -> csrf.disable())
        // JWT는 세션을 안 쓰므로 STATELESS 설정
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        
        .authorizeHttpRequests(auth -> auth
            // 내부 페이지 이동 허용
            .dispatcherTypeMatchers(jakarta.servlet.DispatcherType.FORWARD, jakarta.servlet.DispatcherType.ERROR).permitAll()
            
            // 1. 화면(HTML)과 정적 리소스는 다 열어둠
            .requestMatchers("/", "/*.html", "/css/**", "/js/**", "/images/**", "/favicon.ico", "/error").permitAll()
            
            // 2. 회원가입, 로그인 API 허용
            .requestMatchers("/api/members/**").permitAll()

            // 3. 상품 목록/상세 조회 API 허용 (GET)
            .requestMatchers(HttpMethod.GET, "/api/items/**").permitAll()

            // 4. 리뷰 목록 조회 API 허용 (GET)
            // 로그인 안 한 사람도 상세 페이지에서 리뷰를 볼 수 있음
            // 단, 리뷰 작성(POST)은 여전히 로그인해야 가능 (아래 anyRequest에 걸림)
            .requestMatchers(HttpMethod.GET, "/api/reviews/**").permitAll()

            // 5. 나머지는 다 잠금 (장바구니, 주문, 리뷰 작성 등)
            .anyRequest().authenticated()
        )
        
        // 예외 처리 (401, 403)
        .exceptionHandling(exception -> exception
                .authenticationEntryPoint(unauthorizedHandler()) 
                .accessDeniedHandler(accessDeniedHandler())     
        )
        // JWT 필터 추가
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