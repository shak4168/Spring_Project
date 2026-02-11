package com.project.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.web.config.jwt.JwtAuthenticationFilter; 
import com.project.web.service.SellerRequestService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/seller-requests")
@RequiredArgsConstructor
public class SellerRequestController {

    private final SellerRequestService sellerRequestService;

    // 판매자 권한 신청 (POST /api/seller-requests)
    @PostMapping
    @PreAuthorize("hasRole('USER')") // 일반 유저만 신청 가능
    public ResponseEntity<String> applySeller() {
        // 현재 로그인한 사용자의 ID를 Service로 넘겨서 처리
        // (SecurityContext에서 ID를 꺼내는 방식은 Service에서 구현하거나, 
        //  여기서 @AuthenticationPrincipal로 꺼내도 됩니다. 
        //  편의상 Service에서 SecurityContextHolder를 쓰도록 하겠습니다.)
        
        sellerRequestService.applySeller();
        return ResponseEntity.ok("판매자 신청이 완료되었습니다.");
    }
}