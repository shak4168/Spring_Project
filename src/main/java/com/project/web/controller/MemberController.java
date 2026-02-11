package com.project.web.controller;

import java.security.Principal;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.web.dto.member.LoginRequestDTO;
import com.project.web.dto.member.LoginResponseDTO;
import com.project.web.dto.member.MemberJoinRequestDTO;
import com.project.web.dto.member.MemberResponseDTO;
import com.project.web.dto.member.SocialJoinRequestDTO;
import com.project.web.service.MemberService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j; 

@Slf4j // 로그 남기기
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
@Tag(name = "1. 회원(Member)", description = "회원 가입 및 로그인 기능을 담당합니다.")
public class MemberController {

    private final MemberService memberService;

    // 회원가입 (기존 코드)
    @PostMapping("/join")
    @Operation(summary = "회원가입", description = "아이디(username), 비밀번호, 이름 등을 받아 회원을 생성합니다.")
    public ResponseEntity<Long> join(@RequestBody MemberJoinRequestDTO dto) {
        Long memberId = memberService.join(dto);
        return ResponseEntity.ok(memberId);
    }

    // 로그인
    @PostMapping("/login")
    @Operation(summary = "로그인", description = "아이디와 비밀번호로 JWT 토큰을 발급받습니다.")
    public ResponseEntity<?> login(@RequestBody LoginRequestDTO dto) {
        try {
        	LoginResponseDTO response = memberService.login(dto);
            return ResponseEntity.ok(response); // 성공: JSON 객체 반환
        } catch (IllegalArgumentException e) {
            // 실패: 서비스에서 던진 예외 메시지("가입되지 않은 ID..." 등)를 그대로 문자열로 반환
            // 상태 코드는 401(Unauthorized) 또는 400(Bad Request) 추천
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(e.getMessage());
        }
    }
    
    @PostMapping("/social-update")
    public ResponseEntity<String> updateSocialInfo(
            @RequestBody SocialJoinRequestDTO requestDTO, 
            Principal principal) {
        
        // 토큰에서 추출한 이메일
        String email = principal.getName();
        
        memberService.updateSocialMember(email, requestDTO);
        
        return ResponseEntity.ok("가입 정보가 업데이트되었습니다.");
    }
    
    @GetMapping("/me")
    @Operation(summary = "내 정보 조회", description = "현재 로그인한 사용자의 정보를 반환합니다.")
    public ResponseEntity<MemberResponseDTO> getMyInfo(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        // 서비스 호출
        MemberResponseDTO myInfo = memberService.getMyInfo(principal.getName());
        
        return ResponseEntity.ok(myInfo);
    }
}