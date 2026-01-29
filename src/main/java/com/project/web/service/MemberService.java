package com.project.web.service;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.web.config.jwt.JwtTokenProvider;
import com.project.web.domain.member.Member;
import com.project.web.domain.member.Role;
import com.project.web.dto.member.LoginRequestDTO;
import com.project.web.dto.member.LoginResponseDTO;
import com.project.web.dto.member.MemberJoinRequestDTO;
import com.project.web.repository.MemberRepository;
import com.project.web.util.AESUtil;

import lombok.RequiredArgsConstructor;

@Service // 서비스계층
@Transactional(readOnly = true) // 하나의 작업단위(트랜잭션)으로 묶음, 성공시 커밋, 실패시 롤백 / (readOnly = true) DB 부하 분산
@RequiredArgsConstructor	//  final 이 붙은 필드를 모아서 생성자를 자동으로 만들어줌
// @Autowired (수동표식) : 필요한 부품(Bean)을 자동으로 연결
public class MemberService {
	private final MemberRepository memberRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;
    private final AESUtil aesUtil; // 암호화 도구
    
    // 회원가입
    @Transactional
    public Long join(MemberJoinRequestDTO dto) {
        // 1. 아이디 중복 검증
        validateDuplicateMember(dto.getEmail());

        Role role = Role.USER; // 기본값
        if ("SELLER".equals(dto.getRole())) {
            role = Role.SELLER;
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
        String token = jwtTokenProvider.createToken(member.getEmail(), member.getRole().toString());
        // 3. 인증 성공 시 JWT 토큰 생성 및 반환
        return new LoginResponseDTO(token, member.getName(), member.getRole().toString());
    }
    
}
