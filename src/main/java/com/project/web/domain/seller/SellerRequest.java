package com.project.web.domain.seller;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import com.project.web.domain.member.Member;

import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@EntityListeners(AuditingEntityListener.class)
public class SellerRequest {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    @Enumerated(EnumType.STRING)
    private SellerRequestStatus status; // WAITING, APPROVED, REJECTED

    @CreatedDate
    private LocalDateTime requestedAt;
    
    private LocalDateTime processedAt; // 처리 일시

    // 생성자 (정적 팩토리 메서드 권장)
    public static SellerRequest create(Member member) {
        SellerRequest request = new SellerRequest();
        request.member = member;
        request.status = SellerRequestStatus.WAITING;
        return request;
    }

    // 비즈니스 로직: 승인 처리
    public void approve() {
        this.status = SellerRequestStatus.APPROVED;
        this.processedAt = LocalDateTime.now();
    }
    
    // 비즈니스 로직: 반려 처리
    public void reject() {
        this.status = SellerRequestStatus.REJECTED;
        this.processedAt = LocalDateTime.now();
    }
}
