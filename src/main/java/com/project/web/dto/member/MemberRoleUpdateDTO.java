package com.project.web.dto.member;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor
public class MemberRoleUpdateDTO {
    private String role;  // "ADMIN", "SELLER", "USER"
    private String delYn; // "Y"(탈퇴/정지), "N"(정상)
}