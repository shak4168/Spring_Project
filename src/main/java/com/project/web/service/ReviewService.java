package com.project.web.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.web.domain.item.Item;
import com.project.web.domain.member.Member;
import com.project.web.domain.order.OrderStatus;
import com.project.web.domain.review.Review;
import com.project.web.dto.review.ReviewRequestDTO;
import com.project.web.dto.review.ReviewResponseDTO;
import com.project.web.repository.ItemRepository;
import com.project.web.repository.MemberRepository;
import com.project.web.repository.OrderRepository;
import com.project.web.repository.ReviewRepository;

import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReviewService {

    private final ReviewRepository reviewRepository;
    private final ItemRepository itemRepository;
    private final MemberRepository memberRepository;
    private final OrderRepository orderRepository; // 구매 내역 확인용

    
    /**
     * 리뷰 목록 조회 (Controller에서 호출)
     */
    public Page<ReviewResponseDTO> getReviewList(Long itemId, Pageable pageable) {
        // Repository에서 엔티티(Page<Review>)를 가져와서 -> DTO(Page<ReviewResponseDTO>)로 변환
        Page<Review> reviews = reviewRepository.findByItemId(itemId, pageable);
        return reviews.map(ReviewResponseDTO::new);
    }
    
    /**
     * 리뷰 등록
     */
    @Transactional
    public Long createReview(ReviewRequestDTO reviewDTO, String email) {
        
        // 1. 엔티티 조회 (기본 검증)
        Item item = itemRepository.findById(reviewDTO.getItemId())
                .orElseThrow(() -> new EntityNotFoundException("상품이 존재하지 않습니다."));
        
        Member member = memberRepository.findByEmail(email)
                .orElseThrow(() -> new EntityNotFoundException("회원이 존재하지 않습니다."));

        // 2.  구매 이력 검증 (구매 확정된 건만 리뷰 가능)
        boolean hasPurchased = orderRepository.existsByEmailAndItemId(
                email, 
                item.getId(), 
                OrderStatus.ORDER //
            );
        if (!hasPurchased) {
            throw new IllegalStateException("구매 확정된 상품만 리뷰를 작성할 수 있습니다.");
        }

        // 3. [중복 방지] 이미 해당 상품에 리뷰를 썼는지 확인 (1상품 1리뷰 정책 가정)
        // 만약 '재구매 시 또 작성 가능'하게 하려면 로직이 더 복잡해집니다(OrderItemId를 기준으로 체크).
        if (reviewRepository.existsByMemberAndItem(member, item)) {
            throw new IllegalStateException("이미 리뷰를 작성한 상품입니다.");
        }

        // 4. 리뷰 저장
        Review review = Review.builder()
                .item(item)
                .member(member)
                .content(reviewDTO.getContent())
                .rating(reviewDTO.getRating())
                .isPurchased(true) // 검증 통과했으므로 true
                .build();

        reviewRepository.save(review);
        //  상품 테이블에 평점/개수 업데이트
        item.addReviewRating(reviewDTO.getRating());
        return review.getId();
    }
}