package com.project.web.dto.item;

import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

@Data
public class ItemFormRequestDTO {
    private String name;
    private int price;
    private int stockQuantity;
    private String description;
    
    private Long categoryId; // 카테고리 ID
    
    private MultipartFile imageFile; // 이미지 파일
}