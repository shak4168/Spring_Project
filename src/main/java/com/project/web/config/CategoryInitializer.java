package com.project.web.config;

import com.project.web.domain.category.Category;
import com.project.web.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

@Component
@RequiredArgsConstructor
public class CategoryInitializer implements CommandLineRunner {

    private final CategoryRepository categoryRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        // 1. 이미 카테고리가 있으면 실행하지 않음 (중복 방지)
        if (categoryRepository.count() > 0) {
            return;
        }

        // === [1] 패션/의류 카테고리 만들기 ===
        // 1-1. 부모 생성 (parent = null)
        Category fashion = createCategory("패션/의류", null);
        
        // 1-2. 자식 생성 (parent = fashion)
        createCategory("상의", fashion);
        createCategory("하의", fashion);
        createCategory("아우터", fashion);


        // === [2] 가전/디지털 카테고리 만들기 ===
        Category electronics = createCategory("가전/디지털", null);

        createCategory("노트북", electronics);
        createCategory("스마트폰", electronics);
        createCategory("주변기기", electronics);


        // === [3] 식품 카테고리 만들기 ===
        Category food = createCategory("식품", null);
        
        createCategory("신선식품", food);
        createCategory("가공식품", food);

        // ... 필요한 만큼 더 추가 ...
    }

    /**
     * 카테고리 생성 헬퍼 메서드
     */
    private Category createCategory(String name, Category parent) {
        Category category = Category.builder()
                .name(name)
                .parent(parent) // 부모가 없으면 null, 있으면 객체 연결
                .build();
        
        return categoryRepository.save(category);
    }
}