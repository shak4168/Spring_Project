package com.project.web.domain.member;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter // getKey() 메서드를 자동으로 만들어줍니다.
@RequiredArgsConstructor // final 필드를 초기화하는 생성자를 만들어줍니다.
public enum Role {

    // 스프링 시큐리티 권한 코드("ROLE_")와 설명(title)을 함께 관리합니다.
    USER("ROLE_USER", "일반 사용자"),
    SELLER("ROLE_SELLER", "판매자"),
    ADMIN("ROLE_ADMIN", "관리자"),
	BAN("ROLE_BAN", "정지된 계정");
    private final String key;   // 예: "ROLE_USER"
    private final String title; // 예: "일반 사용자"
}