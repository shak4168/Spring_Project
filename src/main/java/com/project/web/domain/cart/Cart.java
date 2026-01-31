package com.project.web.domain.cart;

import com.project.web.domain.BaseEntity;
import com.project.web.domain.member.Member;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor
public class Cart extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "cart_id")
    private Long id;

    // 회원 1명당 장바구니 1개
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "member_id")
    private Member member;

    // 장바구니 생성 메서드
    public static Cart createCart(Member member) {
        Cart cart = new Cart();
        cart.member = member;
        return cart;
    }
}