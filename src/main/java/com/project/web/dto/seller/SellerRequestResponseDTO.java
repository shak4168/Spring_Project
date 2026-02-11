package com.project.web.dto.seller;

import java.time.LocalDateTime;

import com.project.web.domain.seller.SellerRequest;
import com.project.web.domain.seller.SellerRequestStatus;

import lombok.Getter;

@Getter
public class SellerRequestResponseDTO {
    private Long id;
    private String email;
    private String username;
    private SellerRequestStatus status;
    private LocalDateTime requestedAt;

    public SellerRequestResponseDTO(SellerRequest entity) {
        this.id = entity.getId();
        this.email = entity.getMember().getEmail(); // Fetch Join 덕분에 쿼리 안 나감
        this.username = entity.getMember().getName();
        this.status = entity.getStatus();
        this.requestedAt = entity.getRequestedAt();
    }
}