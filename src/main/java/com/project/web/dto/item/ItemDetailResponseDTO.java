package com.project.web.dto.item;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor // 모든 필드를 파라미터로 받는 생성자를 생성 (@Builder 어노테이션을 사용할 때 내부적으로 모든 필드를 채우기 위해 필요)
@NoArgsConstructor
public class ItemDetailResponseDTO {

	private Long itemId;
    private String name;
    private String description; // 엔티티의 description과 매핑
    private Integer price;
    private Integer stockNumber; // 엔티티의 stockQuantity와 매핑
    private String imageUrl;      //  상품 이미지 경로
    private List<ReviewDTO> reviews; // 상품에 달린 리뷰들

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReviewDTO {
        private Long reviewId;
        private String content;
        private String writerName;
        private int rating;
        private LocalDateTime regDate;
        private List<ReplyDTO> replies; // 리뷰에 달린 답글들
    }

    @Getter
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ReplyDTO {
        private Long replyId;
        private String content;
        private String writerName;
        private LocalDateTime regDate;
    }
}