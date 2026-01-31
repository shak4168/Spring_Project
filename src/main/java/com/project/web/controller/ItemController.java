package com.project.web.controller;

import java.io.IOException;
import java.security.Principal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.web.dto.item.ItemDetailResponseDTO;
import com.project.web.dto.item.ItemFormRequestDTO;
import com.project.web.dto.item.ItemResponseDTO;
import com.project.web.service.ItemService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    /**
     * 상품 등록 API
     * 권한: 관리자(ADMIN)만 가능해야 함 (지금은 누구나 가능하게 열어둠)
     */
    @PostMapping
    public ResponseEntity<String> createItem(
            @ModelAttribute ItemFormRequestDTO requestDTO,
            Principal principal // 시큐리티가 현재 로그인한 유저 정보를 넣어줌
    ) throws IOException {
        
        // principal.getName()을 호출하면 로그인할 때 사용한 '이메일'이 나옴
        String email = principal.getName(); 
        log.info("상품 등록 요청자: {}, 상품명: {}", email, requestDTO.getName());

        // 서비스에 DTO와 이메일을 함께 던져줌
        itemService.saveItem(requestDTO, email); 
        
        return ResponseEntity.ok("상품이 성공적으로 등록되었습니다!");
    }
    
    /**
     * 상품 목록 조회 API (페이징 + 카테고리 적용)
     * [GET] /api/items?page=0&categoryId=1
     * ★ 기존의 List<ItemResponseDTO>를 반환하던 메서드는 삭제했습니다.
     */
    @GetMapping
    public ResponseEntity<Page<ItemResponseDTO>> getItems(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "categoryId", required = false) Long categoryId
    ) {
        // 1. 페이지 설정: 한 페이지에 8개씩, ID 역순(최신순)으로 정렬
        Pageable pageable = PageRequest.of(page, 8, Sort.by("id").descending());
        
        // 2. 서비스 호출 (Service도 수정되어 있어야 함)
        Page<ItemResponseDTO> result = itemService.getMainItemPage(categoryId, pageable);
        
        // 3. 반환 (프론트엔드 페이징 지원)
        return ResponseEntity.ok(result);
    }
    
    /*
     * 상품 상세 조회 API
     * [GET] /api/items/{itemId}
     */
    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDetailResponseDTO> getItemDetail(@PathVariable("itemId") Long itemId) {
        ItemDetailResponseDTO itemDetail = itemService.getItemDetail(itemId);
        return ResponseEntity.ok(itemDetail);
    }
}