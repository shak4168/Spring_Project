package com.project.web.repository;

import com.project.web.domain.cart.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository<Cart, Long> {
    // 회원의 장바구니 찾기
    Cart findByMemberId(Long memberId);
}