package com.project.web.domain.member;
import java.time.LocalDateTime;

import com.project.web.domain.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
/*
 * Member 클래스
 * 회원 테이블을 생성
 * */

@Entity // 이 클래스는 테이블이라고 선언
@Getter
@Builder // 빌더 패턴 사용 가능하게 함
@AllArgsConstructor // 빌더를 위해 모든 필드 생성자 자동 생성
@NoArgsConstructor(access = AccessLevel.PROTECTED)
//1. 삭제(delete) 명령이 오면 -> 이 UPDATE 문을 대신 실행해라!
//- del_yn = 'Y' 로 변경
//- deleted_at = NOW() (현재시간) 로 변경
@SQLDelete(sql = "UPDATE member SET del_yn = 'Y', deleted_at = NOW() WHERE member_id = ?")
//2. 조회(select) 할 때는 -> del_yn이 'N' 인 애들만 가져와라!
@Where(clause = "del_yn = 'N'")
@Table(name = "member") // 실제 DB에 생길 테이블 이름 강제 설정
public class Member extends BaseEntity{

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY) // 이 필드가 테이블의 PK
															// 번호 설정 방식을 DB에 맡김 ( mysql은 IDENTITY )
    @Column(name = "member_id")
    private Long id; // PK (기본키)

    @Column(unique = true, nullable = false)
    private String email; // 이메일

    @Column(nullable = false)
    private String password; // 비밀번호

    @Column(nullable = false)
    private String name; // 이름

    private String phone; // 연락처
    
    @Column(length = 8)
    private String birthDate;
    
    // 주소 정보 (나중에 확장성을 위해 분리할 수도 있지만 일단 포함)
    private String zipcode;
    private String address;
    private String detailAddress;


    @Enumerated(EnumType.STRING)
    private Role role; // 권한 (USER, SELLER, ADMIN)

    // BaseEntity(공통컬럼)는 다음 단계에 적용할 예정이라 일단 필드 직접 작성
    private LocalDateTime createdAt;
    
    @Builder
    public Member(String email, String password, String name, String phone, Role role,
                  String birthDate, String zipcode, String address, String detailAddress) { // 생성자에도 추가
    	this.email = email;
        this.password = password;
        this.name = name;
        this.phone = phone;
        this.role = role;
        this.birthDate = birthDate;
        this.zipcode = zipcode;
        this.address = address;
        this.detailAddress = detailAddress;
    }

}
