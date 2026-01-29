package com.project.web.dto.member;
import lombok.Getter;
import lombok.NoArgsConstructor;
@Getter
@NoArgsConstructor
public class LoginRequestDTO {
    private String email;
    private String password;
}