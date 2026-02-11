package com.project.web.dto.admin;

import com.project.web.domain.order.OrderItem;
import com.project.web.domain.order.OrderStatus;
import com.project.web.domain.order.Orders; // Entity 이름 주의
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@NoArgsConstructor
public class AdminOrderResponseDTO {

    private Long orderId;
    private String orderDate;
    private OrderStatus orderStatus;
    private int totalPrice; // 관리자에게 필수적인 정보 (총 매출 파악용)
    
    // 관리자용 주문 상품 리스트 (Inner Class 사용)
    private List<AdminOrderItemDTO> orderItems = new ArrayList<>();

    // Entity -> Admin DTO 변환
    public AdminOrderResponseDTO(Orders order) {
        this.orderId = order.getId();
        this.orderDate = order.getOrderDate().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
        this.orderStatus = order.getStatus();

        // 1. 주문 상품 리스트 변환
        this.orderItems = order.getOrderItems().stream()
                .map(AdminOrderItemDTO::new)
                .collect(Collectors.toList());
        
        // 2. 총 주문 금액 계산 (주문 상품 가격 * 수량의 합)
        this.totalPrice = this.orderItems.stream()
                .mapToInt(item -> item.getOrderPrice() * item.getCount())
                .sum();
    }

    /**
     * [내부 클래스] 관리자용 주문 상품 상세
     * - 관리자는 이미지보다 '상품 ID', '재고' 같은 게 더 중요할 수 있음
     */
    @Getter
    @NoArgsConstructor
    public static class AdminOrderItemDTO {
        private Long itemId;
        private String itemName;
        private int count;
        private int orderPrice;
        // private int currentStock; // 나중에 이런 필드 추가하기 좋음

        public AdminOrderItemDTO(OrderItem orderItem) {
            this.itemId = orderItem.getItem().getId();
            this.itemName = orderItem.getItem().getName();
            this.count = orderItem.getCount();
            this.orderPrice = orderItem.getOrderPrice();
            // 관리자는 이미지가 크게 중요하지 않을 수 있어서 뺄 수도 있지만, 확인용으로 둠
        }
    }
}