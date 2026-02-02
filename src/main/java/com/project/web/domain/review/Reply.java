package com.project.web.domain.review;

import com.project.web.domain.BaseEntity;
import com.project.web.domain.member.Member;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Reply extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "reply_id")
    private Long id;

    // 어떤 리뷰에 대한 답글인가?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "review_id")
    private Review review;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member; // 작성자 (관리자)

    //  @Lob은 데이터베이스에 따라 CLOB/BLOB으로 매핑되어 성능 이슈가 있을 수 있음
    // MySQL 기준으로는 TEXT 타입으로 명시하는 것이 조회 성능상 유리
    // 답변이 소설처럼 길지 않다면 varchar(1000) 정도도 충분
    @Column(nullable = false, columnDefinition = "TEXT")
    private String content;

    @Builder
    public Reply(Review review, Member member, String content) {
        this.review = review;
        this.member = member;
        this.content = content;
    }
}