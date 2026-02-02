package com.project.web.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.project.web.domain.item.Item;
import com.project.web.domain.member.Member;
import com.project.web.domain.review.Review;

public interface ReviewRepository extends JpaRepository<Review, Long> {

    // 중복 검사 용
    boolean existsByMemberAndItem(Member member, Item item);

    // [목록 조회 용] 페이징 + N+1 해결
    // Review를 가져올 때 Member를 같이(Fetch) 가져옴.
    // Item은 상세 페이지니까 이미 알고 있다고 가정(필요시 join fetch item 추가)
    @Query(value = "select r from Review r join fetch r.member where r.item.id = :itemId",
           countQuery = "select count(r) from Review r where r.item.id = :itemId")
    Page<Review> findByItemId(@Param("itemId") Long itemId, Pageable pageable);
}