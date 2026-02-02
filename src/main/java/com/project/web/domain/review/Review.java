package com.project.web.domain.review;

import java.util.ArrayList;
import java.util.List;

import com.project.web.domain.BaseEntity;
import com.project.web.domain.item.Item;
import com.project.web.domain.member.Member;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // 기본 생성자 (파라미터가 없는 생성자)를 생성(JPA나 Jackson(JSON 변환기)이 객체를 생성할 때 필요)
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
    
    // 구매 확인 여부를 필드로 두어 조회 성능 최적화
    private boolean isPurchased;
    
 // 리뷰에 달린 답글들 (일대다 관계)
    @OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Reply> replies = new ArrayList<>();

    @Builder
    public Review(Member member, Item item, String content, int rating, boolean isPurchased) {
        this.member = member;
        this.item = item;
        this.content = content;
        this.rating = rating;
        this.isPurchased = isPurchased;
    }
    
    /**
     * 리뷰 수정 비즈니스 로직
     */
    public void updateReview(String content, int rating) {
        this.content = content;
        this.rating = rating;
    }
}