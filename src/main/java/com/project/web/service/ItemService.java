package com.project.web.service;

import java.io.IOException;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.web.domain.category.Category;
import com.project.web.domain.item.Item;
import com.project.web.domain.member.Member;
import com.project.web.dto.item.ItemFormRequestDTO;
import com.project.web.repository.CategoryRepository;
import com.project.web.repository.ItemRepository;
import com.project.web.repository.MemberRepository;
import com.project.web.util.FileStore;

import lombok.RequiredArgsConstructor;
/*
 * ItemService 클래스
 * 카테고리 조회(Repository 사용)
 * 이미지 저장(FileStore 사용)
 * 상품 생성(Entity의 Builder 사용)
 * DB 저장(Repository 사용)
 * 
 * */
@Service
@Transactional(readOnly = true) // 기본적으로 읽기 전용 (성능 최적화)
@RequiredArgsConstructor
public class ItemService {

    private final ItemRepository itemRepository;
    private final CategoryRepository categoryRepository;
    private final MemberRepository memberRepository;
    private final FileStore fileStore;

    /**
     * 상품 등록 (쓰기 작업이므로 readOnly = false 적용)
     */
    @Transactional
    public Long saveItem(ItemFormRequestDTO dto, String email) throws IOException {
    	
    	// 1. 판매자(로그인한 사람) 찾기
        Member seller = memberRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("판매자를 찾을 수 없습니다."));
    	
        // 2. 카테고리 조회
        Category category = categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new IllegalArgumentException("해당 카테고리가 존재하지 않습니다. id=" + dto.getCategoryId()));

        // 3. 이미지 파일 저장
        String storeFileName = fileStore.storeFile(dto.getImageFile());

        // 4. 상품 엔티티 생성 (imgUrl -> imageUrl 등 필드명 오타 주의!)
        Item item = Item.builder()
                .name(dto.getName())
                .price(dto.getPrice())
                .stockQuantity(dto.getStockQuantity())
                .description(dto.getDescription())
                .imgUrl(storeFileName) // 엔티티 필드명과 맞춰주세요
                .category(category)    
                .seller(seller)          // [추가] 판매자 주입
                .build();

        // 5. 상품 저장
        Item savedItem = itemRepository.save(item);
        
        return savedItem.getId();
    }
    
    public List<Item> findAllItems() {
    	return itemRepository.findAll();
    	}
}