package com.project.web.dto.member;

import java.time.format.DateTimeFormatter;
import com.project.web.domain.member.Member;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class MemberResponseDTO {

    private Long id;
    private String email;
    private String name;
    private String role;      // 화면에서 "ADMIN", "USER"로 확인하기 위해 String 사용
    private String phone;
    
    // 주소 정보
    private String zipcode;
    private String address;
    private String detailAddress;
    
    // 관리자 페이지용 추가 필드
    private String provider;  // 소셜 로그인
    private String delYn;     // 탈퇴 여부
    private String createdAt; // 가입일 (문자열)

    public MemberResponseDTO(Member member) {
        this.id = member.getId();
        this.email = member.getEmail();
        this.name = member.getName();
        this.phone = member.getPhone();
        this.zipcode = member.getZipcode();
        this.address = member.getAddress();
        this.detailAddress = member.getDetailAddress();
        this.provider = member.getProvider();
        this.delYn = member.getDelYn(); // Entity의 delYn 가져오기

        // 1. Role 처리 (Null 방지 + "ROLE_" 접두사 제거)
        if (member.getRole() != null) {
            // getKey()가 "ROLE_USER"라면 -> name()은 "USER"를 반환함.
            // 관리자 페이지 JS가 'ADMIN'인지 체크하므로 name()을 쓰는 게 더 안전합니다.
            this.role = member.getRole().name(); 
        } else {
            this.role = "USER"; // 기본값
        }
        
        // 2. [중요] 가입일 날짜 포맷팅 (Null 방지 코드)
        if (member.getCreatedAt() != null) {
            this.createdAt = member.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        } else {
            this.createdAt = ""; // 날짜가 없으면 빈칸으로 둠 (에러 방지!)
        }
    }
}