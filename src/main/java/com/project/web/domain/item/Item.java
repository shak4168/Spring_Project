package com.project.web.domain.item;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.hibernate.annotations.ColumnDefault;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import com.project.web.domain.BaseEntity;
import com.project.web.domain.category.Category;
import com.project.web.domain.member.Member;
import com.project.web.domain.review.Review;
import com.project.web.exception.NotEnoughStockException;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Version;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * [도메인 엔티티] 상품(Item)
 * * - 특징: 
 * 1. 물리적 삭제(Hard Delete) 대신 논리적 삭제(Soft Delete)를 적용하여 데이터 무결성 보장.
 * 2. 비즈니스 로직(재고 감소, 평점 계산 등)을 엔티티 내부에 응집시켜 객체지향적 설계 구현.
 * 3. 조회 성능 최적화를 위해 다대일(N:1) 관계는 모두 지연 로딩(LAZY) 적용.
 */
@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED) // JPA 스펙상 기본 생성자 필수, 무분별한 객체 생성 방지(protected)
// ▼ [핵심] Repository.delete() 호출 시, 실제로는 이 SQL이 실행됨 (Soft Delete)
@SQLDelete(sql = "UPDATE item SET del_yn = 'Y', deleted_at = NOW() WHERE item_id = ?")
// ▼ [핵심] 조회(findAll, findById) 시 자동으로 '삭제되지 않은 데이터'만 필터링
@Where(clause = "del_yn = 'N'") 
public class Item extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id") // DB 컬럼명을 명시적으로 지정 (@SQLDelete의 WHERE절과 일치해야 함)
    private Long id;

    @Column(nullable = false)
    private String name;

    private int price;
    
    private int stockQuantity; // 재고 수량

    @Lob // Large Object (MySQL TEXT/LONGTEXT 타입 매핑)
    private String description;

    @Column(name = "img_url") // DB 컬럼명은 스네이크 케이스(img_url) 권장
    private String imageUrl; 

    /* =============================================
     * Soft Delete (논리적 삭제) 필드
     * ============================================= */
    @Column(name = "del_yn", length = 1)
    @ColumnDefault("'N'") // DDL 생성 시 기본값 설정
    private String delYn = "N"; // Java 객체 초기값

    private LocalDateTime deletedAt; // 삭제된 시간 (복구 및 이력 관리용)

    /* =============================================
     * 반정규화 필드 (성능 최적화)
     * - 매번 Review 테이블을 count/avg 하는 비용을 줄이기 위해 Item에 캐싱
     * ============================================= */
    @Column(nullable = false)
    @ColumnDefault("0") 
    private int reviewCount; 

    @Column(nullable = false)
    @ColumnDefault("0.0")
    private double averageRating;
    
    // 판매 상태 필드 (SELL, SOLD_OUT)
    @Enumerated(EnumType.STRING)
    private ItemSellStatus itemSellStatus;
    
    /* =============================================
     * 연관관계 매핑
     * ============================================= */
    // [성능] 즉시 로딩(EAGER)은 N+1 문제의 주범이므로 지연 로딩(LAZY) 사용
    @ManyToOne(fetch = FetchType.LAZY) 
    @JoinColumn(name = "category_id")
    private Category category;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "seller_id")
    private Member seller;
    
    // 상품 삭제 시 리뷰도 같이 관리 (Cascade)
    @OneToMany(mappedBy = "item", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Review> reviews = new ArrayList<>();
    
    /* =============================================
     * 동시성 제어를 위한 낙관적 락 필드 추가
     * - 이 필드가 있으면 JPA가 수정 시 버전을 체크합니다.
     * ============================================= */
    @Version
    private Long version = 0L;    
    
    /* =============================================
     * 생성자 (Builder Pattern)
     * ============================================= */
    @Builder
    public Item(String name, int price, int stockQuantity, String description, String imageUrl, Category category, Member seller) {
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.description = description;
        this.imageUrl = imageUrl;
        this.category = category;
        this.seller = seller;
        this.itemSellStatus = itemSellStatus != null ? itemSellStatus : ItemSellStatus.SELL;
        // 초기값 명시적 설정
        this.delYn = "N"; 
        this.reviewCount = 0;
        this.averageRating = 0.0;
    }
    
    /* =============================================
     * 비즈니스 로직 (도메인 주도 설계)
     * - 엔티티가 스스로 상태를 변경하도록 설계
     * ============================================= */
    
    // 재고 감소 (주문 시 호출)
    public void removeStock(int count) {
        int restStock = this.stockQuantity - count;
        if (restStock < 0) {
            throw new NotEnoughStockException("재고가 부족합니다."); 
        }
        this.stockQuantity = restStock;
        
     // 재고가 0이 되면 자동으로 품절 처리
        if (this.stockQuantity == 0) {
            this.itemSellStatus = ItemSellStatus.SOLD_OUT;
        }
    }
    
    // 재고 증가 (주문 취소/반품 시 호출)
    public void addStock(int count) {
        this.stockQuantity += count;
        
     // 재고가 들어왔으니 다시 판매중으로 변경
        if (this.stockQuantity > 0) {
            this.itemSellStatus = ItemSellStatus.SELL;
        }
    }
    
    // 카테고리 변경 (수정 메서드)
    public void setCategory(Category category) {
        this.category = category;
    }
    
    /**
     * 리뷰 등록 시 평점 재계산 (Incremental Update)
     * 공식: ((기존평균 * 기존개수) + 새점수) / (기존개수 + 1)
     */
    public void addReviewRating(int rating) {
        double totalRating = this.averageRating * this.reviewCount;
        this.reviewCount++;
        this.averageRating = (totalRating + rating) / this.reviewCount;
    }

    /**
     * 리뷰 삭제 시 평점 재계산
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