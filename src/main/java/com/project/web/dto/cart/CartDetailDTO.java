package com.project.web.dto.cart;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor // JPA나 라이브러리가 기본 생성자를 필요로 할 수 있음 (조회용이라 필수는 아니지만 안전상 추가)
public class CartDetailDTO {
    private Long cartItemId;
    private Long itemId;
    private String itemName;
    private int price;
    private int count;
    private String imageUrl;

    // 생성자를 통해서만 값을 주입받음 (Setter 삭제)
    public CartDetailDTO(Long cartItemId, Long itemId, String itemName, int price, int count, String imageUrl) {
        this.cartItemId = cartItemId;
        this.itemId = itemId;
        this.itemName = itemName;
        this.price = price;
        this.count = count;
        this.imageUrl = imageUrl;
    }
}