package com.project.web.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.web.domain.member.Member;

public interface MemberRepository extends JpaRepository<Member, Long> {
    // loginId로 회원을 찾는 기능 추가
    Optional<Member> findByEmail(String email);
}