package com.project.web.domain.review;

import com.project.web.domain.BaseEntity;
import com.project.web.domain.item.Item;
import com.project.web.domain.member.Member;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Review extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "review_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member; // 작성자

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item; // 어떤 상품인지

    @Lob // 리뷰도 길게 쓸 수 있으니까
    private String content;
    
    private int rating; // 별점

    @Builder
    public Review(Member member, Item item, String content, int rating) {
        this.member = member;
        this.item = item;
        this.content = content;
        this.rating = rating;
    }
}