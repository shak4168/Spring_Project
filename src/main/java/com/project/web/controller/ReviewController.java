package com.project.web.controller;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.web.dto.review.ReviewRequestDTO;
import com.project.web.dto.review.ReviewResponseDTO;
import com.project.web.service.ReviewService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController // JSON 응답을 위해 사용
@RequiredArgsConstructor
@RequestMapping("/api/reviews")
@Tag(name = "3. 리뷰(Review)", description = "상품별 리뷰 목록 조회 및 구매자 리뷰 작성 기능을 담당합니다.")
public class ReviewController {

    private final ReviewService reviewService;

    /**
     * 리뷰 목록 조회 (페이징)
     * GET /api/reviews/{itemId}?page=0
     */
    @Operation(summary = "리뷰 목록 조회", description = "해당 상품의 리뷰를 최신순으로 조회합니다.")
    @GetMapping("/{itemId}")
    public ResponseEntity<Page<ReviewResponseDTO>> getReviewList(
            @PathVariable("itemId") Long itemId,
            @RequestParam("page") Optional<Integer> page) {
            
        // 5개씩 조회, 최신순 정렬
        Pageable pageable = PageRequest.of(page.orElse(0), 5, Sort.by(Sort.Direction.DESC, "createdAt"));
        Page<ReviewResponseDTO> reviews = reviewService.getReviewList(itemId, pageable);
        return ResponseEntity.ok(reviews);
    }

    /**
     * [API] 리뷰 등록
     * 요청 예시: POST /api/reviews
     * Body: { "itemId": 105, "content": "좋아요", "rating": 5 }
     */
    @Operation(summary = "리뷰 등록", description = "구매한 상품에 대해 별점과 내용을 작성합니다.")
    @PostMapping
    public ResponseEntity<?> createReview(
            @RequestBody @Valid ReviewRequestDTO reviewRequestDto,
            BindingResult bindingResult,
            @AuthenticationPrincipal User user) { // Spring Security 로그인 유저 정보

        // 1. 유효성 검사 실패 시 에러 반환
        if (bindingResult.hasErrors()) {
            StringBuilder sb = new StringBuilder();
            bindingResult.getAllErrors().forEach(error -> {
                sb.append(error.getDefaultMessage()).append("\n");
            });
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(sb.toString());
        }

        // 2. 로그인 여부 체크 (Security 설정에 따라 생략 가능하지만 방어적으로)
        if (user == null) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("로그인이 필요합니다.");
        }

        // 3. 서비스 호출 (예외 처리는 GlobalExceptionHandler에서 잡는 것을 권장)
        try {
            Long reviewId = reviewService.createReview(reviewRequestDto, user.getUsername()); // email 전달
            return ResponseEntity.ok(reviewId);
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(e.getMessage()); // "구매한 사람만 가능합니다" 등
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("리뷰 등록 중 에러가 발생했습니다.");
        }
    }
}