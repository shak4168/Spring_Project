package com.project.web.dto.member;
import com.project.web.domain.member.Role;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberJoinRequestDTO {
	
	private String email;
    private String password; // 비밀번호
    private String name; // 이름
    private String phone; // 전화번호
    private String birthDate; // 생년월일
    private String zipcode; // 우편번호
    private String address; // 주소
    private String detailAddress; // 상세주소
    private Role role; // USER, SELLER, ADMIN
		
}
