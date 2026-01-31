package com.project.web.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.web.domain.cart.Cart;
import com.project.web.domain.cart.CartItem;
import com.project.web.domain.item.Item;
import com.project.web.domain.member.Member;
import com.project.web.dto.cart.CartDetailDTO;
import com.project.web.dto.cart.CartItemRequestDTO;
import com.project.web.repository.CartItemRepository;
import com.project.web.repository.CartRepository;
import com.project.web.repository.ItemRepository;
import com.project.web.repository.MemberRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional
public class CartService {

    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;

    @Transactional
    public Long addCart(CartItemRequestDTO cartItemDTO, String email) {

        // 1. 회원 조회
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("회원 정보가 없습니다."));

        // 2. 장바구니 조회 (없으면 생성)
        Cart cart = cartRepository.findByMemberId(member.getId());
        if (cart == null) {
            cart = Cart.createCart(member);
            cartRepository.save(cart);
        }

        // 3. 상품 조회
        Item item = itemRepository.findById(cartItemDTO.getItemId())
                .orElseThrow(() -> new EntityNotFoundException("상품 정보가 없습니다."));

        // 4. 장바구니에 이미 담겨있는지 확인
        CartItem savedCartItem = cartItemRepository.findByCartIdAndItemId(cart.getId(), item.getId());

        if (savedCartItem != null) {
            // 이미 있으면 수량만 증가 (Dirty Checking으로 자동 저장)
            savedCartItem.addCount(cartItemDTO.getCount());
            return savedCartItem.getId();
        } else {
            // 없으면 새로 생성해서 저장
            CartItem cartItem = CartItem.createCartItem(cart, item, cartItemDTO.getCount());
            cartItemRepository.save(cartItem);
            return cartItem.getId();
        }
    }
    // 장바구니 목록 조회
    @Transactional(readOnly = true)
    public List<CartDetailDTO> getCartList(String email) {
        List<CartDetailDTO> cartDetailDTOList = new ArrayList<>();

        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("회원 정보가 없습니다."));

        Cart cart = cartRepository.findByMemberId(member.getId());
        
        // 장바구니가 비어있으면 빈 리스트 반환
        if (cart == null) {
            return cartDetailDTOList;
        }

        // 장바구니에 담긴 상품 조회
        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());

        for (CartItem cartItem : cartItems) {
            CartDetailDTO dto = new CartDetailDTO(
                    cartItem.getId(),
                    cartItem.getItem().getId(),
                    cartItem.getItem().getName(),
                    cartItem.getItem().getPrice(),
                    cartItem.getCount(),
                    cartItem.getItem().getImageUrl()
            );
            cartDetailDTOList.add(dto);
        }

        return cartDetailDTOList;
    }
    
    // 장바구니 비우기 (주문 완료 후 호출)
    @Transactional
    public void clearCart(Long cartId) {
        // 장바구니 안의 모든 상품 삭제 (Bulk Delete 쿼리)
        cartItemRepository.deleteAllInBatch(cartItemRepository.findByCartId(cartId));
    }
    
    //  장바구니 상품 개별 삭제
    @Transactional
    public void deleteCartItem(Long cartItemId, String email) {
        // 1. 타겟 상품 조회
        CartItem cartItem = cartItemRepository.findById(cartItemId)
                .orElseThrow(() -> new EntityNotFoundException("장바구니 상품이 존재하지 않습니다."));

        // 2. 현재 로그인한 회원의 장바구니인지 확인 (보안)
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("회원 정보가 없습니다."));

        if (!cartItem.getCart().getMember().getId().equals(member.getId())) {
            throw new IllegalArgumentException("해당 장바구니 상품에 대한 접근 권한이 없습니다.");
        }

        // 3. 삭제
        cartItemRepository.delete(cartItem);
    }
}