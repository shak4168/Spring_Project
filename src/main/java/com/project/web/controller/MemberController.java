package com.project.web.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.web.dto.member.LoginRequestDTO;
import com.project.web.dto.member.LoginResponseDTO;
import com.project.web.dto.member.MemberJoinRequestDTO;
import com.project.web.service.MemberService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j // 로그 남기기
@RestController
@RequestMapping("/api/members")
@RequiredArgsConstructor
public class MemberController {

    private final MemberService memberService;

    // 회원가입 (기존 코드)
    @PostMapping("/join")
    public ResponseEntity<Long> join(@RequestBody MemberJoinRequestDTO dto) {
        Long memberId = memberService.join(dto);
        return ResponseEntity.ok(memberId);
    }

    // 로그인
    @PostMapping("/login")
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
}