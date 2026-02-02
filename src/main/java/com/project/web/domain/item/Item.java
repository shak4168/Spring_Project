package com.project.web.domain.item;

import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.ColumnDefault;

import com.project.web.domain.BaseEntity;
import com.project.web.domain.category.Category;
import com.project.web.domain.member.Member;
import com.project.web.domain.review.Review;

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


@Entity //JPA가 이 클래스를 테이블과 매핑하도록 지정 (기본적으로 클래스 이름인 Item 테이블 생성)
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA는 엔티티 생성 시 기본 생성자(Default Constructor)가 필수, 하지만 무분별한 객체 생성을 막기 위해 접근 제어자를 protected로 설정하여 안전성을 높임
public class Item extends BaseEntity {

    // @Id: 해당 필드가 테이블의 Primary Key(PK)임을 명시
    // @GeneratedValue(strategy = GenerationType.IDENTITY): PK 생성을 DB에 위임 (MySQL의 AUTO_INCREMENT)
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false) // DDL 생성 시 Not Null 제약조건을 추가
    private String name;

    private int price;
    private int stockQuantity; // 재고 수량

    @Lob // Large Object. 대용량 텍스트 데이터를 저장할 때 사용 (MySQL의 TEXT/LONGTEXT 타입 매핑)
    private String description;

    @jakarta.persistence.Column(name = "imgUrl") // [추가] DB 컬럼명을 'img_url'로 고정
    private String imageUrl; // 저장된 이미지 파일명 (UUID 포함)

    @Column(columnDefinition = "CHAR(1) default 'N'") // 삭제 여부 (Y/N). 실수로 삭제해도 복구할 수 있게 데이터를 남겨둠 (Soft Delete)
    private String delYn;

    
    // 리뷰 개수와 평균 평점 (조회 성능 최적화를 위한 반정규화 필드)
    // null 방지를 위해 primitive type 사용 및 DB Default 값 설정
    @Column(nullable = false)
    @ColumnDefault("0") 
    private int reviewCount; 

    @Column(nullable = false)
    @ColumnDefault("0.0")
    private double averageRating;
    
    //@JsonIgnore
    // 상품은 반드시 하나의 카테고리에 속해야함
    @ManyToOne(fetch = FetchType.LAZY) // 다대일 관계 설정 (Item N : 1 Category),  fetch = FetchType.LAZY (지연 로딩) 데이터를 바로 가져오지 않고, 실제 필드에 접근할 때 쿼리를 날림, 성능 최적화(N+1 문제 방지)를 위함
    @JoinColumn(name = "category_id") // 외래키(FK) 컬럼명을 category_id로 지정
    private Category category;
    
    //@JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id") // 판매자별 상품 조회 기능을 위함
    private Member seller;
    
    // 리뷰 목록
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();
    
    @Builder // 빌더 패턴을 적용하여 객체 생성 시 가독성을 높이고, 생성자 파라미터 순서 실수를 방지
    public Item(String name, int price, int stockQuantity, String description, String imageUrl, Category category, Member seller) {
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.description = description;
        this.imageUrl = imageUrl;
        this.category = category;
        this.seller = seller;
        this.delYn = "N"; // 생성 시 삭제 여부 기본값 N
        this.reviewCount = 0;
        this.averageRating = 0.0;
    }
    
    // 비즈니스 로직: 재고 감소 (주문 시 호출)
    public void removeStock(int count) {
        int restStock = this.stockQuantity - count;
        if (restStock < 0) {
            throw new IllegalStateException("재고가 부족합니다."); // 나중에 예외처리 클래스로 변경 예정
        }
        this.stockQuantity = restStock;
    }
    
    // 비즈니스 로직: 재고 증가 (주문 취소 시 호출)
    public void addStock(int count) {
        this.stockQuantity += count;
    }
    
 // 상품 등록/수정 시 카테고리를 넣기 위한 메서드
    public void setCategory(Category category) {
        this.category = category;
    }
    
    /**
     * [추가] 리뷰 등록 시 호출: 평점 평균 재계산 및 카운트 증가
     * 공식: ((기존평균 * 기존개수) + 새점수) / (기존개수 + 1)
     */
    public void addReviewRating(int rating) {
        double totalRating = this.averageRating * this.reviewCount;
        this.reviewCount++;
        this.averageRating = (totalRating + rating) / this.reviewCount;
    }

    /**
     *  리뷰 삭제 시 호출: 평점 재계산 
     */
    public void removeReviewRating(int rating) {
        if (this.reviewCount <= 1) {
            this.reviewCount = 0;
            this.averageRating = 0.0;
        } else {
            double totalRating = this.averageRating * this.reviewCount;
            this.reviewCount--;
            this.averageRating = (totalRating - rating) / this.reviewCount;
        }
    }
}