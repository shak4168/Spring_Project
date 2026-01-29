package com.project.web.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.project.web.domain.category.Category;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    // 추가 쿼리 메서드가 필요하면 나중에 여기에 적습니다.
}