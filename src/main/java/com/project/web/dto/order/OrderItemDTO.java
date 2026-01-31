package com.project.web.dto.order;

import com.project.web.domain.order.OrderItem;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class OrderItemDTO {
    private String itemName; // 상품명
    private int count;       // 주문 수량
    private int orderPrice;  // 주문 가격
    private String imageUrl; // 상품 이미지

    public OrderItemDTO(OrderItem orderItem) {
        this.itemName = orderItem.getItem().getName();
        this.count = orderItem.getCount();
        this.orderPrice = orderItem.getOrderPrice();
        this.imageUrl = orderItem.getItem().getImageUrl();
    }
}