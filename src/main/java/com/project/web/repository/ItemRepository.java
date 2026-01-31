package com.project.web.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import com.project.web.domain.item.Item;

public interface ItemRepository extends JpaRepository<Item, Long> {
	// 1. [기존] 전체 상품 조회 (페이징)
    Page<Item> findAll(Pageable pageable);

    // 2. [추가] 카테고리별 상품 조회 (페이징)
    // "categoryId" 조건을 걸어 조회합니다.
    Page<Item> findByCategoryId(Long categoryId, Pageable pageable);
}