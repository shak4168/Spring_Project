package com.project.web.repository;

import com.project.web.domain.cart.CartItem;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {
    // 장바구니 아이디와 상품 아이디로 장바구니 상품 찾기
    CartItem findByCartIdAndItemId(Long cartId, Long itemId);
    
    // 내 장바구니의 모든 상품 목록 조회
    List<CartItem> findByCartId(Long cartId);
}