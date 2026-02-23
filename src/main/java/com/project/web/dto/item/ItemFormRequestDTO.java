package com.project.web.dto.item;
import org.springframework.web.multipart.MultipartFile;
import io.swagger.v3.oas.annotations.media.Schema;

import lombok.Data;
@Schema(description = "신규 상품 등록 요청 데이터") // 전체 DTO 설명
@Data
public class ItemFormRequestDTO {
	@Schema(description = "상품명", example = "클라우드 네이티브 맨투맨")
    private String name;

    @Schema(description = "상품 가격 (원)", example = "35000")
    private int price;

    @Schema(description = "초기 재고 수량", example = "100")
    private int stockQuantity;

    @Schema(description = "상품 상세 설명", example = "최고급 면 100%로 제작된 편안한 데일리 맨투맨입니다.")
    private String description;
    
    @Schema(description = "카테고리 ID (외래키)", example = "1")
    private Long categoryId;
    
    @Schema(description = "상품 대표 이미지 파일 (Multipart)")
    private MultipartFile imageFile;
}