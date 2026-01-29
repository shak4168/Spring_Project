package com.project.web.service;

import com.project.web.domain.member.Member;
import com.project.web.repository.MemberRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Service
public class CustomUserDetailsService implements UserDetailsService {

    private final MemberRepository memberRepository;

    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        // 1. DB에서 이메일로 회원 조회
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new UsernameNotFoundException("해당 이메일을 가진 사용자를 찾을 수 없습니다: " + email));

        // [디버깅용 로그] 콘솔에 권한이 제대로 찍히는지 확인해보세요!
        System.out.println("==========================================");
        System.out.println("로그인 감지: " + member.getEmail());
        System.out.println("DB 권한 정보: " + member.getRole()); // 여기서 SELLER가 나와야 함
        System.out.println("==========================================");

        // 2. Spring Security가 이해할 수 있는 User 객체로 변환하여 반환
        // roles() 안에는 "USER", "SELLER" 문자열이 들어가야 함 (ROLE_ 접두사는 자동 처리됨)
        return User.builder()
                .username(member.getEmail())
                .password(member.getPassword())
                .roles(member.getRole().toString()) 
                .build();
    }
}