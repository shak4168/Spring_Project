package com.project.web.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.project.web.domain.item.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {
	// 1. 전체 상품 조회 (페이징)
    Page<Item> findAll(Pageable pageable);

    // 2. 카테고리별 상품 조회 (페이징)
    // "categoryId" 조건을 걸어 조회
    Page<Item> findByCategoryId(Long categoryId, Pageable pageable);
    
 // 3.  상품명 검색 기능
    // 설명: "Containing"을 붙이면 SQL의 "LIKE %키워드%" 처럼 동작
    // 특징: 아까 만든 인덱스(idx_item_name) 덕분에 검색 속도가 매우 빠름
    Page<Item> findByNameContaining(String name, Pageable pageable);
}