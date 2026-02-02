package com.project.web.dto.order;

import com.project.web.domain.order.OrderItem;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderItemDTO {
	private Long itemId;
    private String itemName; // 상품명
    private int count;       // 주문 수량
    private int orderPrice;  // 주문 가격
    private String imageUrl; // 상품 이미지
    private boolean hasReview; // 리뷰가 있는지 확인
    
    public OrderItemDTO(OrderItem orderItem, boolean hasReview) {
    	this.itemId = orderItem.getItem().getId();
        this.itemName = orderItem.getItem().getName();
        this.count = orderItem.getCount();
        this.orderPrice = orderItem.getOrderPrice();
        this.imageUrl = orderItem.getItem().getImageUrl();
        this.hasReview = hasReview;
    }
}