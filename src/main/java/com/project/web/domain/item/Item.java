package com.project.web.domain.item;
import com.project.web.domain.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Item extends BaseEntity {
	
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    // 어떤 카테고리인가?
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "category_id")
    private Category category;

    private String name;
    private int price;
    private int stockQuantity;

    @Lob // 긴 텍스트 저장
    private String description;

    @Builder
    public Item(Category category, String name, int price, int stockQuantity, String description) {
        this.category = category;
        this.name = name;
        this.price = price;
        this.stockQuantity = stockQuantity;
        this.description = description;
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
}
