package com.project.web.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.web.domain.member.Member;
import com.project.web.domain.seller.SellerRequest;
import com.project.web.domain.seller.SellerRequestStatus;

public interface SellerRequestRepository extends JpaRepository<SellerRequest, Long> {

    // N+1 방지: Member를 한 번에 조회 (Fetch Join)
    @Query("SELECT sr FROM SellerRequest sr JOIN FETCH sr.member WHERE sr.status = :status")
    List<SellerRequest> findAllByStatus(@Param("status") SellerRequestStatus status);
    
 // 특정 회원이, 특정 상태(WAITING)로 신청한게 있는지 확인 (중복 신청 방지용)
    boolean existsByMemberAndStatus(Member member, SellerRequestStatus status);
}