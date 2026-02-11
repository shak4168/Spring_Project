package com.project.web.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

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
    
 // 관리자 페이지용: 판매자(Member) 정보를 한 방에 같이 가져오는 메서드
    //'JOIN FETCH'를 쓰면 DB에서 아이템을 가져올 때 판매자 정보도 미리 채워옴
    @Query(value = "SELECT i FROM Item i LEFT JOIN FETCH i.seller",
           countQuery = "SELECT count(i) FROM Item i")
    Page<Item> findAllWithSeller(Pageable pageable);
    
    @Query(value = "SELECT i FROM Item i LEFT JOIN FETCH i.seller WHERE i.name LIKE %:keyword% AND i.delYn = 'N'",
            countQuery = "SELECT count(i) FROM Item i WHERE i.name LIKE %:keyword% AND i.delYn = 'N'")
     Page<Item> searchByNameWithSeller(@Param("keyword") String keyword, Pageable pageable);
}