package com.project.web.controller;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.project.web.domain.item.Item;
import com.project.web.dto.admin.AdminItemResponseDTO;
import com.project.web.dto.member.MemberDetailDTO;
import com.project.web.dto.member.MemberResponseDTO;
import com.project.web.dto.member.MemberRoleUpdateDTO;
import com.project.web.dto.seller.SellerRequestResponseDTO;
import com.project.web.repository.ItemRepository;
import com.project.web.service.AdminService;
import com.project.web.service.ItemService;
import com.project.web.service.MemberService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/admin")
@RequiredArgsConstructor
@Tag(name = "7. 관리자(Admin)", description = "판매자 승인, 전체 상품 관리, 회원 관리 등 시스템 관리자 전용 기능입니다.")
public class AdminController {

    private final AdminService adminService;
    private final ItemService itemService;
    private final MemberService memberService;
    private final ItemRepository itemRepository;
    
    // ==========================================
    // 1. 판매자 신청 관리
    // ==========================================
    
    // 신청 목록 조회 (WAITING)
    @Operation(summary = "판매자 신청 목록 조회", description = "승인 대기 중인(WAITING) 판매자 신청 건들을 조회합니다. (ADMIN 권한 필요)")
    @GetMapping("/seller-requests")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<SellerRequestResponseDTO>> getSellerRequests() {
        return ResponseEntity.ok(adminService.getPendingRequests());
    }

    // 승인
    @Operation(summary = "판매자 승인", description = "특정 사용자의 판매자 신청을 승인하여 ROLE_SELLER 권한을 부여합니다.")
    @PostMapping("/seller-requests/{id}/approve")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> approveSeller(@PathVariable("id") Long id) {
        adminService.approveSeller(id);
        return ResponseEntity.ok("승인되었습니다.");
    }

    // 거부
    @Operation(summary = "판매자 승인 거부", description = "판매자 신청을 거부합니다.")
    @PostMapping("/seller-requests/{id}/reject")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> rejectSeller(@PathVariable("id") Long id) {
        adminService.rejectSeller(id);
        return ResponseEntity.ok("거부되었습니다.");
    }
    
    // ==========================================
    // 2. 상품 관리
    // ==========================================
    
 // 상품 관리 - 전체 상품 조회 및 검색
    @Operation(summary = "전체 상품 관리 조회", description = "키워드 검색 및 페이징을 통해 시스템의 모든 상품을 조회합니다.")
    @GetMapping("/items")
    public ResponseEntity<Page<AdminItemResponseDTO>> getAllItems(
    		@RequestParam(name = "keyword", required = false) String keyword, // 검색어 받기
            @PageableDefault(page = 0, size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        
        Page<Item> items;
        
        // 검색어가 있으면 검색 쿼리 실행
        if (keyword != null && !keyword.isBlank()) {
            items = itemRepository.searchByNameWithSeller(keyword, pageable);
        } else {
            // 없으면 전체 조회 실행
            items = itemRepository.findAllWithSeller(pageable);
        }
        
        Page<AdminItemResponseDTO> dtos = items.map(AdminItemResponseDTO::new);
        return ResponseEntity.ok(dtos);
    }
    //  상품 관리 - 상품 강제 삭제
    @Operation(summary = "상품 강제 삭제", description = "해당하는 상품을 강제로 삭제합니다.")
    @DeleteMapping("/items/{itemId}")
    public ResponseEntity<String> deleteItem(@PathVariable("itemId") Long itemId) {
        itemRepository.deleteById(itemId); 
        return ResponseEntity.ok("상품이 삭제되었습니다.");
    }
    
    // ==========================================
    // 3. 회원 관리 (New!)
    // ==========================================

    // 회원 목록 조회 (검색 + 페이징)
    @Operation(summary = "회원 목록 조회", description = "전체 회원 목록을 조회합니다.")
    @GetMapping("/members")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Page<MemberResponseDTO>> getMembers(
    		@RequestParam(value = "keyword", required = false) String keyword,
    		@PageableDefault(size = 10, sort = "id", direction = Sort.Direction.DESC) Pageable pageable) {
        
        Page<MemberResponseDTO> members = memberService.getMembers(keyword, pageable);
        return ResponseEntity.ok(members);
    }

    // 회원 상세 조회 (주문 내역 포함)
    @Operation(summary = "회원 상세 조회", description = "특정 회원의 기본 정보와 주문 내역을 상세 조회합니다.")
    @GetMapping("/members/{memberId}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MemberDetailDTO> getMemberDetail(@PathVariable("memberId") Long memberId) {
        MemberDetailDTO detail = memberService.getMemberDetail(memberId);
        return ResponseEntity.ok(detail);
    }
    
 // 회원 권한/상태 수정
    @Operation(summary = "회원 권한/상태 수정", description = "특정 회원의 권한 또는 상태를 수정합니다.")
    @PutMapping("/members/{memberId}") // PUT: 수정
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<String> updateMemberRole(
            @PathVariable("memberId") Long memberId,
            @RequestBody MemberRoleUpdateDTO dto) {
        
        memberService.updateMemberRole(memberId, dto);
        return ResponseEntity.ok("회원 정보가 수정되었습니다.");
    }
}