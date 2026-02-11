package com.project.web.controller;

import java.io.IOException;
import java.security.Principal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault; // [New] 페이징 처리를 위해 추가
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.web.domain.item.Item; // [New] 리포지토리 반환 타입
import com.project.web.dto.item.ItemDetailResponseDTO;
import com.project.web.dto.item.ItemFormRequestDTO;
import com.project.web.dto.item.ItemResponseDTO;
import com.project.web.repository.ItemRepository; // [New] 검색을 위해 리포지토리 추가
import com.project.web.service.ItemService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
@Tag(name = "5. 상품(Item)", description = "상품 등록, 전체 조회(페이징), 상세 조회 및 검색 기능을 제공합니다.")
public class ItemController {

    private final ItemService itemService;
    private final ItemRepository itemRepository; // [New] 검색 기능을 위해 주입받음

    /**
     * 상품 등록 API
     * 권한: 관리자(ADMIN)만 가능해야 함 (지금은 누구나 가능하게 열어둠)
     */
    @PostMapping
    @Operation(summary = "상품 등록", description = "상품 정보를 받아 새로운 상품을 등록합니다.")
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
     */
    @Operation(summary = "상품 목록 조회", description = "카테고리별 상품 목록을 페이징하여 조회합니다.")
    @GetMapping
    public ResponseEntity<Page<ItemResponseDTO>> getItems(
            @RequestParam(value = "page", defaultValue = "0") int page,
            @RequestParam(value = "categoryId", required = false) Long categoryId
    ) {
        // 1. 페이지 설정: 한 페이지에 8개씩, ID 역순(최신순)으로 정렬
        Pageable pageable = PageRequest.of(page, 8, Sort.by("id").descending());
        
        // 2. 서비스 호출
        Page<ItemResponseDTO> result = itemService.getMainItemPage(categoryId, pageable);
        
        // 3. 반환
        return ResponseEntity.ok(result);
    }

    /**
     * [New] 상품 검색 API
     * 설명: 검색어(keyword)를 받아서 상품명에 포함된 아이템을 찾습니다.
     * URL: /api/items/search?keyword=나이키&page=0
     */
    @Operation(summary = "상품 검색", description = "키워드가 포함된 상품을 검색합니다.")
    @GetMapping("/search")
    public ResponseEntity<Page<ItemResponseDTO>> searchItems(
            @RequestParam("keyword") String keyword,
            @PageableDefault(size = 8, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        
        // 1. 리포지토리에서 바로 검색 (아까 만든 인덱스 태움)
        Page<Item> itemPage = itemRepository.findByNameContaining(keyword, pageable);
        
        // 2. Entity(Item) -> DTO(ItemResponseDTO) 변환
        // (ItemResponseDTO 생성자가 Item을 받도록 되어 있다고 가정)
        Page<ItemResponseDTO> response = itemPage.map(ItemResponseDTO::new);
        
        return ResponseEntity.ok(response);
    }
    
    /*
     * 상품 상세 조회 API
     * [GET] /api/items/{itemId}
     */
    @Operation(summary = "상품 상세 조회", description = "상품 ID로 상세 정보를 조회합니다.")
    @GetMapping("/{itemId}")
    public ResponseEntity<ItemDetailResponseDTO> getItemDetail(@PathVariable("itemId") Long itemId) {
        ItemDetailResponseDTO itemDetail = itemService.getItemDetail(itemId);
        return ResponseEntity.ok(itemDetail);
    }
}