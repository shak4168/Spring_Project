package com.project.web.dto.item;

import com.project.web.domain.item.Item;
import lombok.Getter;

@Getter
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
        this.imageUrl = item.getImgUrl();
    }
}