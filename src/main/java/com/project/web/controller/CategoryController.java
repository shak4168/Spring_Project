package com.project.web.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.web.dto.category.CategoryResponseDTO;
import com.project.web.repository.CategoryRepository;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
        // 모든 카테고리 조회 -> DTO 변환 -> 반환
        List<CategoryResponseDTO> categories = categoryRepository.findAll().stream()
                .map(CategoryResponseDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(categories);
    }
}