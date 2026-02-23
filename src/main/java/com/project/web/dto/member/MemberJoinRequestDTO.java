package com.project.web.dto.member;
import com.project.web.domain.member.Role;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Schema(description = "회원가입 요청 데이터") // 전체 DTO 설명
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberJoinRequestDTO {
	
	@Schema(description = "사용자 이메일 (로그인 ID로 사용)", example = "user@shak-lab.site")
    private String email;

    @Schema(description = "비밀번호 (8자 이상, 영문/숫자/특수문자 포함)", example = "password123!")
    private String password;

    @Schema(description = "사용자 실명", example = "홍길동")
    private String name;

    @Schema(description = "전화번호 (- 제외)", example = "01012345678")
    private String phone;

    @Schema(description = "생년월일 (YYYYMMDD)", example = "19940101")
    private String birthDate;

    @Schema(description = "우편번호", example = "12345")
    private String zipcode;

    @Schema(description = "기본 주소", example = "경기도 안산시")
    private String address;

    @Schema(description = "상세 주소", example = "101동 101호")
    private String detailAddress;

    @Schema(description = "권한 등급 (USER, SELLER, ADMIN)", example = "USER")
    private Role role;
		
}
