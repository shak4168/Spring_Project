package com.project.web.domain.order;

import com.project.web.domain.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToOne;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Delivery extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_id")
    private Long id;

    private String zipcode;
    private String address;
    private String detailAddress;
    
    private String trackingNumber; // 운송장 번호

    @Enumerated(EnumType.STRING) // enum 값을 DB에 저장할 때 글자 그대로 저장
    private DeliveryStatus status;

    @OneToOne(mappedBy = "delivery", fetch = FetchType.LAZY) // 주문 하나당 배송 정보는 딱 하나
    private Orders orders;

    @Builder
    public Delivery(String zipcode, String address, String detailAddress, DeliveryStatus status) {
        this.zipcode = zipcode;
        this.address = address;
        this.detailAddress = detailAddress;
        this.status = status;
    }
    
    // 배송 정보 입력 로직
    public void setTrackingNumber(String trackingNumber) {
        this.trackingNumber = trackingNumber;
        this.status = DeliveryStatus.COMP;
    }
    
    // 주문 엔티티 설정을 위한 편의 메서드
    public void setOrders(Orders orders) {
        this.orders = orders;
    }
    
    public DeliveryStatus getStatus() {
        return this.status;
    }
}