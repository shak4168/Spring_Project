package com.project.web.controller;

import java.security.Principal;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.web.dto.cart.CartDetailDTO;
import com.project.web.dto.cart.CartItemRequestDTO;
import com.project.web.service.CartService;
import com.project.web.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/cart")
@Tag(name = "6. 장바구니(Cart)", description = "장바구니 상품 담기, 목록 조회, 삭제 및 전체 주문 기능을 수행합니다.")
public class CartController {

    private final CartService cartService;
    private final OrderService orderService;

    // 1. 장바구니 담기 (POST)
    @Operation(summary = "장바구니 담기", description = "상품 ID와 수량을 받아 장바구니에 추가합니다.")
    @PostMapping
    public ResponseEntity<String> addCart(
            @RequestBody CartItemRequestDTO cartItemRequestDTO,
            Principal principal) {

        if (principal == null) {
            return new ResponseEntity<>("로그인이 필요합니다.", HttpStatus.UNAUTHORIZED);
        }

        String email = principal.getName();
        cartService.addCart(cartItemRequestDTO, email); // 반환값 안 써도 됨

        return new ResponseEntity<>("장바구니에 상품을 담았습니다.", HttpStatus.OK);
    }
    
    // 2. 장바구니 목록 조회 (GET) 
    @Operation(summary = "장바구니 조회", description = "로그인한 사용자의 장바구니 목록을 조회합니다.")
    @GetMapping
    public ResponseEntity<List<CartDetailDTO>> getCartList(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        
        List<CartDetailDTO> cartList = cartService.getCartList(principal.getName());
        return ResponseEntity.ok(cartList);
    }
    
    // 3. 장바구니 전체 주문 (POST)
    @Operation(summary = "장바구니 일괄 주문", description = "장바구니에 담긴 모든 상품을 한 번에 주문합니다.")
    @PostMapping("/order")
    public ResponseEntity<String> orderFromCart(Principal principal) {
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        try {
            Long orderId = orderService.orderFromCart(principal.getName());
            return ResponseEntity.ok("주문이 완료되었습니다! (주문번호: " + orderId + ")");
        } catch (IllegalStateException e) {
            return ResponseEntity.badRequest().body(e.getMessage()); 
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("주문 중 오류가 발생했습니다.");
        }
    }
    
    @Operation(summary = "장바구니 상품 삭제", description = "장바구니에서 특정 상품(cartItemId)을 삭제합니다.")
    @DeleteMapping("/{cartItemId}") // URL: /api/cart/5
    public ResponseEntity<String> deleteCartItem(
            @PathVariable("cartItemId") Long cartItemId,
            Principal principal) {

        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        cartService.deleteCartItem(cartItemId, principal.getName());

        return ResponseEntity.ok("상품이 장바구니에서 삭제되었습니다.");
    }
}