package com.project.web.dto.member;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class LoginResponseDTO {
    private String token; // JWT 토큰
    private String name;  // 사용자 이름
    private String role;
}