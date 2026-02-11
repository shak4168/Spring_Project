package com.project.web.repository;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.web.domain.member.Member;
import com.project.web.domain.member.Role;

public interface MemberRepository extends JpaRepository<Member, Long> {
    // loginId로 회원을 찾는 기능 추가
    Optional<Member> findByEmail(String email);
   // 1. 검색어가 없을 때: 관리자가 아닌(<> ADMIN) 사람만 전체 조회
    Page<Member> findByRoleNot(Role role, Pageable pageable);

    // 2. 검색어가 있을 때: (이름 OR 이메일 포함) AND (관리자 아님)
    @Query("SELECT m FROM Member m WHERE (m.name LIKE %:keyword% OR m.email LIKE %:keyword%) AND m.role <> 'ADMIN'")
    Page<Member> searchMembersExcludeAdmin(@Param("keyword") String keyword, Pageable pageable);
}