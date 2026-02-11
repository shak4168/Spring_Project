package com.project.web.config.oauth;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import com.project.web.config.jwt.JwtTokenProvider;
import com.project.web.domain.member.Member;
import com.project.web.repository.MemberRepository;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2SuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final JwtTokenProvider jwtTokenProvider;
    private final MemberRepository memberRepository;
    
    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        
        // 1. 로그인 된 사용자 정보 가져오기
        OAuth2User oAuth2User = (OAuth2User) authentication.getPrincipal();
        String email = oAuth2User.getAttribute("email");
        
        log.info("구글 로그인 성공! 이메일: {}", email);

        // 2. DB 조회
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("이메일에 해당하는 유저가 없습니다."));

        // 3. 토큰 생성 (Null 방지 로직 추가)
        // 혹시 Role이 없으면 에러가 나므로 기본값 설정
        String role = (member.getRole() != null) ? member.getRole().getKey() : "ROLE_USER";
        String token = jwtTokenProvider.createToken(email, role);

        // 4. 리다이렉트 URL 결정
        String targetUrl;

        // 전화번호가 비어있거나 공백이면 => 신규 회원 (social-join으로 이동)
        if (member.getPhone() == null || member.getPhone().trim().isEmpty()) {
            log.info("신규 회원(전화번호 없음) -> 정보 입력 페이지로 이동");
            targetUrl = UriComponentsBuilder.fromUriString("/social-join.html")
                    .queryParam("token", token)
                    .build().toUriString();
        } 
        // 전화번호가 있으면 => 기존 회원 (메인으로 이동)
        else {
            log.info("기존 회원 -> index.html로 직행");
            
            // "/"가 아니라 "/index.html"로 명확하게 지정합니다.
            // UriComponentsBuilder 대신 문자열 더하기로 확실하게 토큰을 붙입니다.
            targetUrl = "/index.html?token=" + token;
        }

        log.info("[최종 리다이렉트 URL] : " + targetUrl);

        // 5. 이동 실행
        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}