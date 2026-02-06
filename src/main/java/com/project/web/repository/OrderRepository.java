package com.project.web.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.web.domain.order.OrderStatus;
import com.project.web.domain.order.Orders;

public interface OrderRepository extends JpaRepository<Orders, Long> {
	// Page<Orders>를 반환하면 카운트 쿼리까지 JPA가 최적화해서 실행
	// join fetch 추가 + countQuery 최적화
    @Query(value = "select distinct o from Orders o " +
            "left join fetch o.orderItems " + 
            "where o.member.email = :email " +
            "order by o.orderDate desc",
            
        // 페이징을 위한 카운트 쿼리는 조인을 뺌 (성능 최적화)
    countQuery = "select count(o) from Orders o where o.member.email = :email")
    Page<Orders> findOrders(@Param("email") String email, Pageable pageable);
    
 //구매 이력 검증 쿼리
    // "이 이메일(email)을 가진 회원의 주문(o) 중에서,
    // 해당 아이템(itemId)을 포함하고 있는(oi) 주문이 있는지(count > 0) 확인
    // 단, 주문 상태가 'ORDER(구매완료)' 인 것만."
    @Query("select count(o) > 0 " +
           "from Orders o " +
           "join o.orderItems oi " +             // Orders와 OrderItems를 조인
           "where o.member.email = :email " +    // 회원 아이디 검증
           "and oi.item.id = :itemId " +         // 상품 아이디 검증
           "and o.status = :status")        // 주문 상태 검증 (취소된 주문은 리뷰 불가)
    boolean existsByEmailAndItemId(@Param("email") String email, 
                                   @Param("itemId") Long itemId,
                                   @Param("status") OrderStatus status);
}