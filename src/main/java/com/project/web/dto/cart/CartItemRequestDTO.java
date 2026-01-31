package com.project.web.dto.cart;

import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor // JSON 파싱을 위해 필수
public class CartItemRequestDTO {
    private Long itemId;
    private int count;
    
    // 테스트 코드 작성을 위해 생성자 추가
    public CartItemRequestDTO(Long itemId, int count) {
        this.itemId = itemId;
        this.count = count;
    }
}