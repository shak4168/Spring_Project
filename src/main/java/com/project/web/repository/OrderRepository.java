package com.project.web.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.web.domain.order.Orders;

public interface OrderRepository extends JpaRepository<Orders, Long> {
	// Page<Orders>를 반환하면 카운트 쿼리까지 JPA가 최적화해서 실행합니다.
    @Query("select o from Orders o " + // 엔티티명이 Orders라면 Orders로 기입
            "where o.member.email = :email " +
            "order by o.orderDate desc")
    Page<Orders> findOrders(@Param("email") String email, Pageable pageable);
}