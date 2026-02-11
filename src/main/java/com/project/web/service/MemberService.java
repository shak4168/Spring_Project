package com.project.web.service;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.web.config.jwt.JwtTokenProvider;
import com.project.web.domain.member.Member;
import com.project.web.domain.member.Role;
import com.project.web.domain.order.Orders;
import com.project.web.dto.admin.AdminOrderResponseDTO;
import com.project.web.dto.member.LoginRequestDTO;
import com.project.web.dto.member.LoginResponseDTO;
import com.project.web.dto.member.MemberDetailDTO;
import com.project.web.dto.member.MemberJoinRequestDTO;
import com.project.web.dto.member.MemberResponseDTO;
import com.project.web.dto.member.MemberRoleUpdateDTO;
import com.project.web.dto.member.SocialJoinRequestDTO;
import com.project.web.repository.MemberRepository;
import com.project.web.repository.OrderRepository;
import com.project.web.util.AESUtil;

import lombok.RequiredArgsConstructor;

@Service // 서비스계층
@Transactional(readOnly = true) // 하나의 작업단위(트랜잭션)으로 묶음, 성공시 커밋, 실패시 롤백 / (readOnly = true) DB 부하 분산
@RequiredArgsConstructor	//  final 이 붙은 필드를 모아서 생성자를 자동으로 만들어줌
// @Autowired (수동표식) : 필요한 부품(Bean)을 자동으로 연결
public class MemberService {
	private final MemberRepository memberRepository;
	private final OrderRepository orderRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AESUtil aesUtil; // 암호화 도구
    
    // 회원가입
    @Transactional
    public Long join(MemberJoinRequestDTO dto) {
        // 1. 아이디 중복 검증
        validateDuplicateMember(dto.getEmail());

        Role role = Role.USER; // 기본값
        if (dto.getRole() == Role.SELLER) {
            role = Role.SELLER;
        } else if (dto.getRole() == Role.ADMIN) {
             role = Role.ADMIN;
        }
        // 2. 비밀번호 암호화 및 엔티티 변환
        Member member = Member.builder()
        		.email(dto.getEmail())
                .password(passwordEncoder.encode(dto.getPassword())) // BCrypt 암호화
                .name(dto.getName())
                .phone(dto.getPhone())
                .role(role)
            	.birthDate(dto.getBirthDate())
                .zipcode(dto.getZipcode())
                .address(dto.getAddress())
                .detailAddress(dto.getDetailAddress())
                .build();

        // 3. 저장
        memberRepository.save(member);
        return member.getId();
    }
    // 아이디가 존재하는지 찾아줌
    private void validateDuplicateMember(String email) {
        memberRepository.findByEmail(email)
                .ifPresent(m -> {
                    throw new IllegalStateException("이미 존재하는 이메일입니다.");
                });
    }
    
    public LoginResponseDTO login(LoginRequestDTO dto) {
        // 1. ID로 회원이 존재하는지 확인 (없으면 에러)
        Member member = memberRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("가입되지 않은 이메일 입니다."));

        // 2. 비밀번호 검증 (DB의 암호화된 비번 vs 입력받은 비번 비교)
        // matches(입력비번, 암호화비번) 순서 중요!
        if (!passwordEncoder.matches(dto.getPassword(), member.getPassword())) {
            throw new IllegalArgumentException("잘못된 비밀번호입니다.");
        }
        
        
     //  정지된 회원 체크
        if (member.getRole() == Role.BAN) {
            throw new IllegalStateException("관리자에 의해 이용이 정지된 계정입니다."); // 403 에러 유발
        }
        
        
        String token = jwtTokenProvider.createToken(member.getEmail(), member.getRole().toString());
        // 3. 인증 성공 시 JWT 토큰 생성 및 반환
        return new LoginResponseDTO(token, member.getName(), member.getRole().toString());
    }
    
    @Transactional
    public void updateSocialMember(String email, SocialJoinRequestDTO dto) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보가 없습니다."));

        // 엔티티의 메서드를 호출하여 정보 변경 (Dirty Checking으로 자동 저장됨)
        member.updateSocialInfo(
                dto.getPhone(),
                dto.getZipcode(),
                dto.getAddress(),
                dto.getDetailAddress(),
                Role.valueOf(dto.getRole()) // 문자열 -> Enum 변환
        );
    }
    
    // 내 정보 조회
    @Transactional(readOnly = true)
    public MemberResponseDTO getMyInfo(String email) {
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("사용자 정보를 찾을 수 없습니다."));
        
        // Entity -> DTO 변환
        return new MemberResponseDTO(member);
    }
    
 // ========================================================
    // [관리자 기능]
    // ========================================================

 // [관리자용] 회원 목록 검색 (관리자 계정 제외)
    @Transactional(readOnly = true)
    public Page<MemberResponseDTO> getMembers(String keyword, Pageable pageable) {
        
        Page<Member> members;

        // 1. 검색어가 없을 때 -> Role이 ADMIN이 아닌 사람만 조회
        if (keyword == null || keyword.isBlank()) {
            members = memberRepository.findByRoleNot(Role.ADMIN, pageable);
        } 
        // 2. 검색어가 있을 때 -> 검색어 조건 + ADMIN 제외
        else {
            members = memberRepository.searchMembersExcludeAdmin(keyword, pageable);
        }
        
        return members.map(MemberResponseDTO::new);
    }

    // 2. 회원 상세 조회 (+주문 내역 포함)
    public MemberDetailDTO getMemberDetail(Long memberId) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 회원의 주문 내역 조회 (OrderRepository 필요)
        List<Orders> orders = orderRepository.findByMemberId(memberId);

        // 관리자용 주문 DTO로 변환
        List<AdminOrderResponseDTO> adminOrderDTOs = orders.stream()
                .map(AdminOrderResponseDTO::new)
                .collect(Collectors.toList());

        return MemberDetailDTO.builder()
                .id(member.getId())
                .email(member.getEmail())
                .name(member.getName())
                .role(member.getRole().name())
                .phone(member.getPhone())
                .address(member.getAddress())
                .delYn(member.getDelYn())
                .orders(adminOrderDTOs) // 변환된 리스트 주입
                .build();
    }
    
 // [관리자용] 회원 권한 및 상태 수정
    @Transactional // 트랜잭션 필수 (그래야 변경 감지 작동)
    public void updateMemberRole(Long memberId, MemberRoleUpdateDTO dto) {
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new IllegalArgumentException("존재하지 않는 회원입니다."));

        // 1. 권한 변경 (Enum 변환)
        if (dto.getRole() != null) {
            member.updateRole(Role.valueOf(dto.getRole()));
        }
        
        // 2. 상태 변경 (Y/N)
        if (dto.getDelYn() != null) {
            member.changeDelYn(dto.getDelYn()); 
        }
    }
}
