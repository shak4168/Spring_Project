package com.project.web.domain.seller;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum SellerRequestStatus {

    WAITING("대기 중"),  // 사용자가 신청 버튼을 누름, 관리자 확인 전
    APPROVED("승인됨"),  // 관리자가 수락함 -> 이때 Member의 Role이 SELLER로 변함
    REJECTED("반려됨");  // 관리자가 거절함 -> Role은 그대로 USER 유지

    private final String description;
}