package com.project.web.domain.order;
import com.project.web.domain.BaseEntity;
import com.project.web.domain.item.Item;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrderItem extends BaseEntity{
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "order_id")
    private Orders orders;

    private int orderPrice; // 주문 당시 가격 (할인 등이 있을 수 있으므로 박제)
    private int count; // 수량

    // 생성 메서드
    public static OrderItem createOrderItem(Item item, int orderPrice, int count) {
        OrderItem orderItem = new OrderItem();
        orderItem.item = item;
        orderItem.orderPrice = orderPrice;
        orderItem.count = count;
        
        item.removeStock(count); // 주문하자마자 재고 까기
        return orderItem;
    }

    // 비즈니스 로직: 취소 시 재고 복구
    public void cancel() {
        this.item.addStock(count);
    }
    
    // Setter (Orders에서 호출용)
    public void setOrders(Orders orders) {
        this.orders = orders;
    }
    
}
