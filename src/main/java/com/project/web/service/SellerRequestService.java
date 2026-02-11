package com.project.web.service;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.web.domain.member.Member;
import com.project.web.domain.seller.SellerRequest;
import com.project.web.domain.seller.SellerRequestStatus;
import com.project.web.repository.MemberRepository;
import com.project.web.repository.SellerRequestRepository;

import lombok.RequiredArgsConstructor;

@Service
@Transactional
@RequiredArgsConstructor
public class SellerRequestService {

    private final SellerRequestRepository sellerRequestRepository;
    private final MemberRepository memberRepository;

    public void applySeller() {
        // 1. 현재 로그인한 사용자 찾기
        String email = getCurrentUserEmail();
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보를 찾을 수 없습니다."));

        // 2. 이미 신청했는지 확인 (중복 방지)
        // 대기중(WAITING)인 요청이 있으면 에러
        if (sellerRequestRepository.existsByMemberAndStatus(member, SellerRequestStatus.WAITING)) {
            throw new IllegalStateException("이미 신청 대기 중입니다.");
        }
        
        // 3. 신청 내역 저장
        SellerRequest request = SellerRequest.builder()
                .member(member)
                .status(SellerRequestStatus.WAITING) // 초기 상태는 대기
                .build();
        
        sellerRequestRepository.save(request);
    }

    // SecurityContext에서 현재 로그인한 이메일 꺼내기
    private String getCurrentUserEmail() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("로그인이 필요합니다.");
        }
        return authentication.getName(); // UserDetails의 username (우리는 email로 설정함)
    }
}