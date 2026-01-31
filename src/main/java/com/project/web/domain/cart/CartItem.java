package com.project.web.domain.cart;

import com.project.web.domain.BaseEntity;
import com.project.web.domain.item.Item;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Getter
@NoArgsConstructor
public class CartItem extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_item_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cart_id")
    private Cart cart;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    private Item item;

    private int count; // 담은 수량

    // 장바구니 상품 생성
    public static CartItem createCartItem(Cart cart, Item item, int count) {
    	CartItem cartItem = new CartItem();
        cartItem.cart = cart; // Setter 없이 필드에 직접 접근 (같은 클래스라 가능)
        cartItem.item = item;
        cartItem.count = count;
        return cartItem;
    }

    // 이미 담겨있는 상품이면 수량만 증가
    public void addCount(int count) {
        this.count += count;
    }
}