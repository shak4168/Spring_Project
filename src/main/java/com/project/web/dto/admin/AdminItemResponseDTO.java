package com.project.web.dto.admin;

import com.project.web.domain.item.Item;
import com.project.web.domain.item.ItemSellStatus;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
public class AdminItemResponseDTO {

    private Long id;
    private String itemName;
    private int price;
    private int stockNumber;
    private String sellStatus; // SELL, SOLD_OUT
    private String sellerEmail; // 누가 팔고 있는지
    private String regTime;

    public AdminItemResponseDTO(Item item) {
        this.id = item.getId();
        this.itemName = item.getName(); 
        this.price = item.getPrice();
        this.stockNumber = item.getStockQuantity(); 
        if (item.getItemSellStatus() != null) {
            this.sellStatus = item.getItemSellStatus().name();
        } else {
            // DB에 상태값이 NULL인 경우, 재고가 있으면 'SELL', 없으면 'SOLD_OUT'
            this.sellStatus = (item.getStockQuantity() > 0) ? "SELL" : "SOLD_OUT";
        }
        
        // 판매자 정보도 혹시 없을 수 있으니 방어 코드 넣으면 좋음
        this.sellerEmail = item.getSeller() != null ? item.getSeller().getEmail() : "알 수 없음";
        
        this.regTime = item.getCreatedAt() != null ? item.getCreatedAt().toString().substring(0, 10) : "";
    }
}