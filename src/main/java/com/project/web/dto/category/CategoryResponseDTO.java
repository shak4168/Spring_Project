package com.project.web.dto.category;

import com.project.web.domain.category.Category;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CategoryResponseDTO {
    private Long id;
    private String name;

    public CategoryResponseDTO(Category category) {
        this.id = category.getId();
        this.name = category.getName();
    }
}