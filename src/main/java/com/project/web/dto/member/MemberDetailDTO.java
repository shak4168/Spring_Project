package com.project.web.dto.member;

import java.util.List;
import com.project.web.dto.admin.AdminOrderResponseDTO;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MemberDetailDTO {
    private Long id;
    private String email;
    private String name;
    private String role; 
    private String phone;
    private String address;
    
    private String delYn;  
    
    private List<AdminOrderResponseDTO> orders;
}