package com.project.web.dto.member;

import com.project.web.domain.member.Role;
import lombok.Data;

@Data
public class SocialJoinRequestDTO {
    private String role;       
    private String phone;
    private String zipcode;      
    private String address;    
    private String detailAddress;
}