package com.project.web.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist; // 추가
import jakarta.persistence.PreUpdate;  // 추가
import lombok.Getter;

@Getter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @CreatedDate
    @Column(updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    private LocalDateTime updatedAt;

    // 삭제 시간
    private LocalDateTime deletedAt;

    // 삭제 여부
    @Column(nullable = false)
    private String delYn = "N"; // ← 여기서 초기화해도 @Builder 쓰면 무시될 수 있음

    // DB에 저장되기 직전(PrePersist)에 실행되어 값을 강제로 채워줌
    @PrePersist
    public void onPrePersist() {
        // 1. 생성시간이 비어있으면 현재 시간 주입 (Auditing이 실패해도 이게 막아줌)
        if (this.createdAt == null) {
            this.createdAt = LocalDateTime.now();
        }
        // 2. 수정시간도 같이 맞춤
        this.updatedAt = LocalDateTime.now();

        // 3. [중요] 빌더 패턴 사용 시 delYn이 null로 들어오는 것을 방지
        if (this.delYn == null) {
            this.delYn = "N";
        }
    }

    // 수정되기 직전에 실행
    @PreUpdate
    public void onPreUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    

 // 자식 클래스들이 상태를 변경할 수 있도록 메서드 제공
    public void changeDelYn(String delYn) {
        this.delYn = delYn;
        
        // 상태 변경 시 수정 시간도 같이 갱신해주는 센스!
        this.updatedAt = LocalDateTime.now();
        
        // 만약 다시 'N'으로 살리는 경우, 삭제 시간은 null로 초기화해주면 더 완벽함
        if ("N".equals(delYn)) {
            this.deletedAt = null;
        } else if ("Y".equals(delYn)) {
            this.deletedAt = LocalDateTime.now();
        }
    }
}