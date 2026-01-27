package com.project.web.domain;

import java.time.LocalDateTime;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import jakarta.persistence.Column;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.MappedSuperclass;
import lombok.Getter;

/* 
 * BaseEntity 클래스
 * 공통 엔터티를 생성해주는 클래스
 * */

@Getter
@MappedSuperclass // 자식들에게 컬럼만 물려주는 부모
@EntityListeners(AuditingEntityListener.class) // 시간을 자동으로 감시
public abstract class BaseEntity {
	@CreatedDate
	@Column(updatable = false) // 생성일은 수정 불가능하게 만듦
	private LocalDateTime createAt;
	
	@LastModifiedDate
	private LocalDateTime updatedAt;
	
	// 삭제된 시간
	private LocalDateTime deletedAt;
	
}
