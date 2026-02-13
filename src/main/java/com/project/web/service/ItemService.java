package com.project.web.service;

import java.io.IOException;
import java.util.List;

import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.project.web.config.RestPage;
import com.project.web.domain.category.Category;
import com.project.web.domain.item.Item;
import com.project.web.domain.member.Member;
import com.project.web.dto.item.ItemDetailResponseDTO;
import com.project.web.dto.item.ItemFormRequestDTO;
import com.project.web.dto.item.ItemResponseDTO;
import com.project.web.repository.CategoryRepository;
import com.project.web.repository.ItemRepository;
import com.project.web.repository.MemberRepository;
import com.project.web.util.FileStorageService;

import jakarta.persistence.EntityNotFoundException;
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
    private final FileStorageService fileStorageService;

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
        String storeFileName = fileStorageService.storeFile(dto.getImageFile());

        // 4. 상품 엔티티 생성
        Item item = Item.builder()
                .name(dto.getName())
                .price(dto.getPrice())
                .stockQuantity(dto.getStockQuantity())
                .description(dto.getDescription())
                .imageUrl(storeFileName)
                .category(category)    
                .seller(seller)          
                .build();

        // 5. 상품 저장
        Item savedItem = itemRepository.save(item);
        
        return savedItem.getId();
    }
    
    public ItemDetailResponseDTO getItemDetail(Long itemId) {
        // 1. 상품 조회 (삭제되지 않은 상품만 조회 로직은 나중에 QueryDSL이나 커스텀 메서드로 고도화 가능)
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new EntityNotFoundException("존재하지 않는 상품입니다."));

        // 2. Soft Delete 체크 (이미 삭제된 상품이면 예외 발생)
        if ("Y".equals(item.getDelYn())) {
            throw new EntityNotFoundException("이미 삭제된 상품입니다.");
        }

        // 3. Entity -> DTO 변환 
        // Batch Size 설정 덕분에 여기서 getReviews(), getReplies() 호출 시 쿼리가 최적화되어 나감
        return ItemDetailResponseDTO.builder()
                .itemId(item.getId())
                .name(item.getName())
                .description(item.getDescription())
                .price(item.getPrice())
                .stockNumber(item.getStockQuantity())
                .imageUrl(item.getImageUrl())
                .reviewCount((long) item.getReviews().size()) 
                .build();
    }
    
    public List<Item> findAllItems() {
    	return itemRepository.findAll();
    	}
    
    
    /**
     * 메인 페이지 상품 목록 조회 (캐싱 적용)
     * [Cacheable] 
     * - value: 캐시 저장소 이름
     * - key: 카테고리ID와 페이지 번호를 조합해 고유 키 생성 (예: mainItems::1-0)
     * - condition: 첫 페이지만 캐싱하거나 특정 조건에서만 작동하도록 설정 가능
     */
    @Cacheable(value = "mainItems", 
            key = "(#a0 == null ? 'all' : #a0) + '-' + #a1.pageNumber")
 // 1. 반환 타입을 RestPage로 변경
    public RestPage<ItemResponseDTO> getMainItemPage(Long categoryId, Pageable pageable) {
        Page<Item> itemPage;
        if (categoryId == null) {
            itemPage = itemRepository.findAll(pageable);
        } else {
            itemPage = itemRepository.findByCategoryId(categoryId, pageable);
        }

        // 2. 결과를 RestPage로 감싸서 반환
        return new RestPage<>(itemPage.map(ItemResponseDTO::new));
    }
    
    @Transactional
    @CacheEvict(value = "mainItems", allEntries = true)
    public void deleteItem(Long itemId) {
        Item item = itemRepository.findById(itemId)
                .orElseThrow(() -> new IllegalArgumentException("해당 상품이 없습니다."));
        
        itemRepository.delete(item);
    }
}