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

    @Lob
    private String content;

    @Builder
    public Reply(Review review, Member member, String content) {
        this.review = review;
        this.member = member;
        this.content = content;
    }
}