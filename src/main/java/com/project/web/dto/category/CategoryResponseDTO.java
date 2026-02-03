package com.project.web.dto.category;

import com.project.web.domain.category.Category;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class CategoryResponseDTO {
	private Long id;
    private String name;
    private Long parentId;    // 부모 ID (null이면 최상위 부모)
    private String parentName; // 부모 이름 (화면 표시용: "의류 > 반팔" 처럼 보여주기 위함)

    public CategoryResponseDTO(Category category) {
        this.id = category.getId();
        this.name = category.getName();
        
        // 부모가 있으면 자식 카테고리임!
        if (category.getParent() != null) {
            this.parentId = category.getParent().getId();
            this.parentName = category.getParent().getName();
        } else {
            // 부모가 없으면 null (최상위 카테고리)
            this.parentId = null;
            this.parentName = null;
        }
    }
}