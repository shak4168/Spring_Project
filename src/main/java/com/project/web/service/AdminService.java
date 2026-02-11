package com.project.web.service;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.web.domain.member.Member;
import com.project.web.domain.member.Role;
import com.project.web.domain.seller.SellerRequest;
import com.project.web.domain.seller.SellerRequestStatus;
import com.project.web.dto.seller.SellerRequestResponseDTO;
import com.project.web.repository.SellerRequestRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final SellerRequestRepository sellerRequestRepository;

    // 대기 중인 목록 조회
    public List<SellerRequestResponseDTO> getPendingRequests() {
        return sellerRequestRepository.findAllByStatus(SellerRequestStatus.WAITING).stream()
                .map(SellerRequestResponseDTO::new)
                .collect(Collectors.toList());
    }

    // 승인 처리 (핵심)
    @Transactional
    public void approveSeller(Long requestId) {
        SellerRequest request = sellerRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 요청입니다."));

        // 이미 처리된 요청인지 확인
        if (request.getStatus() != SellerRequestStatus.WAITING) {
            throw new IllegalStateException("이미 처리된 요청입니다.");
        }

        // 1. 요청 상태 변경 (Domain Logic 호출)
        request.approve();

        // 2. 회원 권한 변경 (Dirty Checking 감지)
        Member member = request.getMember();
        member.updateRole(Role.SELLER); // Member 엔티티에 이 메서드가 있어야 함!
    }
    
    // 반려 처리
    @Transactional
    public void rejectSeller(Long requestId) {
        SellerRequest request = sellerRequestRepository.findById(requestId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 요청입니다."));
        request.reject();
    }
}