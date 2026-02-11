package com.project.web.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.web.dto.category.CategoryResponseDTO;
import com.project.web.repository.CategoryRepository;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@Tag(name = "2. 카테고리(Category)", description = "전체 카테고리 목록을 조회합니다.")
public class CategoryController {

    private final CategoryRepository categoryRepository;
    @Operation(summary = "전체 카테고리 조회", description = "모든 카테고리 목록을 계층 구조 없이 조회합니다.")
    @GetMapping
    public ResponseEntity<List<CategoryResponseDTO>> getAllCategories() {
        // 모든 카테고리 조회 -> DTO 변환 -> 반환
        List<CategoryResponseDTO> categories = categoryRepository.findAll().stream()
                .map(CategoryResponseDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(categories);
    }
}