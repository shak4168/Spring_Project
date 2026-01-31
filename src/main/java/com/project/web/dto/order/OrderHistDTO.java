package com.project.web.dto.order;

import com.project.web.domain.order.OrderStatus;
import com.project.web.domain.order.Orders;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

@Getter
@NoArgsConstructor
public class OrderHistDTO {
    private Long orderId;       // 주문 아이디
    private String orderDate;   // 주문 날짜 (문자열로 변환)
    private OrderStatus orderStatus; // 주문 상태
    private List<OrderItemDTO> orderItemDTOList = new ArrayList<>(); // 주문 상품 리스트

    public OrderHistDTO(Orders order) {
        this.orderId = order.getId();
        // 날짜를 보기 좋게 포맷팅 (예: 2026-01-30 14:30)
        this.orderDate = order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.orderStatus = order.getStatus();
    }

    // 주문 상품 추가 메서드
    public void addOrderItemDTO(OrderItemDTO orderItemDTO) {
    	orderItemDTOList.add(orderItemDTO);
    }
}