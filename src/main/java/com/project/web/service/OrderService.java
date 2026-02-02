package com.project.web.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.web.domain.cart.Cart;
import com.project.web.domain.cart.CartItem;
import com.project.web.domain.item.Item;
import com.project.web.domain.member.Member;
import com.project.web.domain.order.Delivery;
import com.project.web.domain.order.DeliveryStatus;
import com.project.web.domain.order.OrderItem;
import com.project.web.domain.order.Orders; // 사용자가 만든 클래스명 Orders
import com.project.web.dto.order.OrderHistDTO;
import com.project.web.dto.order.OrderItemDTO;
import com.project.web.repository.CartItemRepository;
import com.project.web.repository.CartRepository;
import com.project.web.repository.ItemRepository;
import com.project.web.repository.MemberRepository;
import com.project.web.repository.OrderRepository;
import com.project.web.repository.ReviewRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class OrderService {

    private final OrderRepository orderRepository;
    private final MemberRepository memberRepository;
    private final ItemRepository itemRepository;
    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final ReviewRepository reviewRepository;
    
    /*
     * [주문]
     */
    @Transactional
    public Long order(Long memberId, Long itemId, int count) {

        // 1. 엔티티 조회
        Member member = memberRepository.findById(memberId)
                .orElseThrow(() -> new EntityNotFoundException("회원을 찾을 수 없습니다."));

        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("상품을 찾을 수 없습니다."));

        // 2. 배송 정보 생성 (작성하신 Delivery 엔티티 필드에 맞춤)
        // 실무에서는 Member에 저장된 주소를 가져오거나, 화면에서 입력받은 값을 써야 함
        // 여기서는 임시 값으로 대체
        Delivery delivery = Delivery.builder()
                .address("서울시 강남구 테헤란로") 
                .detailAddress("101호")
                .zipcode("12345")
                .status(DeliveryStatus.READY)
                .build();

        // 3. 주문 상품 생성 (이때 item.removeStock()이 실행되어 재고가 차감됨)
        OrderItem orderItem = OrderItem.createOrderItem(item, item.getPrice(), count);

        // 4. 주문 생성
        Orders order = Orders.createOrder(member, delivery, List.of(orderItem));

        // 5. 주문 저장 (Cascade로 인해 Delivery와 OrderItem도 자동 저장)
        orderRepository.save(order);

        return order.getId();
    }

    /*
     * [주문 취소]
     */
    @Transactional
    public void cancelOrder(Long orderId) {
        Orders order = orderRepository.findById(orderId)
                .orElseThrow(() -> new EntityNotFoundException("주문이 존재하지 않습니다."));
        
        // 주문 취소 (재고 원상복구 로직 포함)
        order.cancel();
    }
    
    /**
     * [장바구니 주문]
     * 장바구니에 담긴 모든 상품을 주문하고, 장바구니를 비웁니다.
     */
    @Transactional
    public Long orderFromCart(String email) {
        // 1. 회원 조회
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("회원 정보가 없습니다."));

        // 2. 장바구니 조회
        Cart cart = cartRepository.findByMemberId(member.getId());
        if (cart == null) {
            throw new IllegalStateException("장바구니가 비어있습니다.");
        }

        // 3. 장바구니 상품 목록 조회
        List<CartItem> cartItems = cartItemRepository.findByCartId(cart.getId());
        if (cartItems.isEmpty()) {
            throw new IllegalStateException("주문할 상품이 없습니다.");
        }

        // 4. 주문 상품 리스트 생성 (CartItem -> OrderItem 변환)
        List<OrderItem> orderItems = new ArrayList<>();
        for (CartItem cartItem : cartItems) {
            // CartItem의 정보를 바탕으로 OrderItem 생성 (이 시점에 재고 차감 발생!)
            OrderItem orderItem = OrderItem.createOrderItem(
                    cartItem.getItem(), 
                    cartItem.getItem().getPrice(), 
                    cartItem.getCount()
            );
            orderItems.add(orderItem);
        }

        // 5. 배송 정보 생성 (임시)
        Delivery delivery = Delivery.builder()
                .address("서울시 강남구")
                .status(DeliveryStatus.READY)
                .build();

        // 6. 주문 생성 (여러 개의 주문 상품을 한 번에 담음)
        // Orders.createOrder 메서드가 가변인자(...)를 받도록 되어 있다면 List를 배열로 변환하거나, 
        // createOrder 메서드를 List를 받도록 수정 
        // 여기서는 List를 받는 오버로딩 메서드를 Orders 엔티티에 추가하는 것을 권장
        
        // Orders 엔티티에 List를 받는 createOrder가 없다면 아래와 같이 호출
        Orders order = Orders.createOrder(member, delivery, orderItems); // <-- 이 부분 확인 필요

        // 7. 주문 저장
        orderRepository.save(order);

        // 8. 장바구니 비우기
        // CartService를 주입받아 쓰면 순환 참조가 날 수 있으므로, 
        // 여기서 직접 리포지토리를 쓰거나 별도 로직으로 처리.
        cartItemRepository.deleteAllInBatch(cartItems);

        return order.getId();
    }
    
 //  주문 목록 조회
    @Transactional(readOnly = true)
    public Page<OrderHistDTO> getOrderList(String email, Pageable pageable) {

        // 리뷰 작성 여부를 확인하기 위해, 현재 로그인한 회원 정보를 가져옵니다.
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("회원 정보가 없습니다."));

        // 1. 리포지토리에서 페이징된 주문 목록 가져오기
        Page<Orders> ordersPage = orderRepository.findOrders(email, pageable);
        
        // 2. 반환할 DTO 리스트 생성
        List<OrderHistDTO> orderHistDtos = new ArrayList<>();

        for (Orders order : ordersPage) {
            // 주문 DTO 생성
            OrderHistDTO orderHistDTO = new OrderHistDTO(order);
            
            // 주문에 포함된 상품들(OrderItem) 반복
            for (OrderItem orderItem : order.getOrderItems()) {
                
            	// 1. 리뷰 작성 여부를 먼저 확인
                boolean hasReview = reviewRepository.existsByMemberAndItem(member, orderItem.getItem());
                
                // 2. DTO 생성 시점에 hasReview 전달
                OrderItemDTO orderItemDTO = new OrderItemDTO(orderItem, hasReview);
                
                // 3. 리스트에 추가
                orderHistDTO.addOrderItemDTO(orderItemDTO);
            }

            orderHistDtos.add(orderHistDTO);
        }

        // 3. PageImpl로 감싸서 반환
        return new PageImpl<>(orderHistDtos, pageable, ordersPage.getTotalElements());
    }
}