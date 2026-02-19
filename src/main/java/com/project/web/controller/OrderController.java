package com.project.web.controller;

import java.security.Principal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.web.domain.member.Member;
import com.project.web.dto.order.OrderHistDTO;
import com.project.web.dto.order.OrderRequestDTO;
import com.project.web.repository.MemberRepository;
import com.project.web.service.OrderService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@Tag(name = "4. 주문(Order)", description = "상품 단건 주문 및 사용자별 구매 이력(주문 내역)을 조회합니다.")
public class OrderController {

    private final OrderService orderService;
    private final MemberRepository memberRepository;

    /*
     * 상품 주문 API
     * [POST] /api/orders
     */
    @Operation(summary = "상품 주문", description = "상세 페이지에서 바로 상품을 주문합니다.")
    @PostMapping("/api/orders")
    public ResponseEntity<String> order(@RequestBody OrderRequestDTO request, Principal principal) {
        
        // 1. 로그인 체크 (Security가 1차로 막아주지만, 안전장치로 한 번 더)
        if (principal == null) {
            return ResponseEntity.status(401).body("로그인이 필요합니다.");
        }

        // 2. 로그인한 회원의 ID 찾기 (이메일로 조회)
        String email = principal.getName();
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("회원 정보가 없습니다."));

        // 3. 주문 서비스 호출 (회원ID, 상품ID, 수량)
        orderService.order(member.getId(), request.getItemId(), request.getCount());

        return ResponseEntity.ok("주문이 완료되었습니다!");
    }
    
    /*
     * 구매 이력 조회 API
     * [GET] /api/orders  <-- 경로 명시 필요
     */
    @Operation(summary = "구매 이력 조회", description = "로그인한 사용자의 주문 내역을 페이징하여 조회합니다.")
    @GetMapping("/api/orders")
    public ResponseEntity<Page<OrderHistDTO>> orderHist(
            @RequestParam(value = "page", defaultValue = "0") int page, // 1. URL 파라미터로 페이지 번호를 받음 (기본값 0)
            Principal principal) {
        
        if (principal == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        // 2. 페이징 요청 객체 생성 (page: 현재 페이지, 5: 한 페이지에 보여줄 개수)
        Pageable pageable = PageRequest.of(page, 5);

        // 3. 서비스 호출 (이제 Page<OrderHistDTO>가 반환됨)
        Page<OrderHistDTO> orderHistDtoPage = orderService.getOrderList(principal.getName(), pageable);

        // 4. 결과 반환
        return ResponseEntity.ok(orderHistDtoPage);
    }
    
    /* 주문 취소 API
    * [POST] /api/orders/{orderId}/cancel
    */
   @Operation(summary = "주문 취소", description = "사용자가 자신의 주문을 취소합니다.")
   @PostMapping("/api/orders/{orderId}/cancel")
   public ResponseEntity<String> cancelOrder(@PathVariable Long orderId, Principal principal) {
       
       // 1. 로그인 체크
       if (principal == null) {
           return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
       }

       // 2. 서비스 로직 호출 (재고 원상복구 포함)
       orderService.cancelOrder(orderId);

       return ResponseEntity.ok("주문이 성공적으로 취소되었습니다.");
   }
    
}