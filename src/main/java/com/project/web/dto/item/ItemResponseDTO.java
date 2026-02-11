package com.project.web.dto.item;

import com.project.web.domain.item.Item;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor  // 1. Jackson을 위한 기본 생성자 자동 생성 (필수!)
@AllArgsConstructor // 2. 모든 필드를 인자로 받는 생성자 (선택사항이나 권장)
public class ItemResponseDTO {
    private Long id;
    private String name;
    private int price;
    private String description;
    private String imageUrl;

    // 엔티티를 DTO로 변환하는 생성자
    public ItemResponseDTO(Item item) {
        this.id = item.getId();
        this.name = item.getName();
        this.price = item.getPrice();
        this.description = item.getDescription();
        this.imageUrl = item.getImageUrl();
    }
}