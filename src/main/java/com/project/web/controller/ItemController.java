package com.project.web.controller;

import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.project.web.domain.item.Item;
import com.project.web.dto.item.ItemFormRequestDTO;
import com.project.web.dto.item.ItemResponseDTO;
import com.project.web.service.ItemService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/*
 * ItemController 클래스
 * 
 * 
 * */

@Slf4j
@RestController // 데이터를 JSON 등으로 반환하는 API 컨트롤러
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;

    /**
     * 상품 등록 API
     * 권한: 관리자(ADMIN)만 가능해야 함 (지금은 누구나 가능하게 열어둠)
     */
 // 상품 등록 API
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
    
 // 추가: 모든 상품 목록 조회 API
    @GetMapping
    public ResponseEntity<List<ItemResponseDTO>> getItems() {
        List<Item> items = itemService.findAllItems();
        
        // 실무 스타일: 엔티티 리스트를 DTO 리스트로 변환 (Stream API 활용)
        List<ItemResponseDTO> responseDTOs = items.stream()
                .map(ItemResponseDTO::new)
                .collect(Collectors.toList());

        return ResponseEntity.ok(responseDTOs);
    }
}