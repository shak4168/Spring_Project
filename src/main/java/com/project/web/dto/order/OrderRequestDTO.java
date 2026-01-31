package com.project.web.dto.order;

import lombok.Getter;
import lombok.Setter;

@Getter @Setter
public class OrderRequestDTO {
    private Long itemId;
    private int count;
}