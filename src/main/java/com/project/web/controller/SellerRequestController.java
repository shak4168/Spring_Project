package com.project.web.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.web.service.SellerRequestService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/seller-requests")
@RequiredArgsConstructor
@Tag(name = "8. 판매자 신청", description = "일반 유저가 판매자(SELLER) 권한을 신청하는 프로세스를 담당합니다.")
public class SellerRequestController {

    private final SellerRequestService sellerRequestService;

    // 판매자 권한 신청 (POST /api/seller-requests)
    @Operation(summary = "판매자 권한 신청", description = "로그인한 유저(USER)가 판매자 권한을 신청합니다. 관리자 승인 후 권한이 변경됩니다.")
    @PostMapping
    @PreAuthorize("hasRole('USER')") // 일반 유저만 신청 가능
    public ResponseEntity<String> applySeller() {
        sellerRequestService.applySeller();
        return ResponseEntity.ok("판매자 신청이 완료되었습니다.");
    }
}