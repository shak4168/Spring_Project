package com.project.web.domain.item;

import java.util.ArrayList;
import java.util.List;

import com.project.web.domain.BaseEntity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

/*
 * Category 클래스
 * 카테고리 테이블을 생성
 * */


@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseEntity {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "category_id")
	private Long id;
	
	private String name;
	
	// 부모 카테고리 ( 상위 분류 )
	@ManyToOne(fetch = FetchType.LAZY)	// 필요할 때만 가져온다. (지연 로딩, 기본값 : EAGER)
	@JoinColumn(name = "parent_id") // DB테이블에 ~라는 이름으로 외래키(FK) 컬럼을 생성
	private Category parent;
	
	// 자식 카테고리들 (하위 분류)
    @OneToMany(mappedBy = "parent") // DB테이블 구조 상 일대다 관계에서 다 쪽이 외래키를 가지는데 읽기 전용이라고 알려주는 것
    private List<Category> children = new ArrayList<>();

    @Builder
    public Category(String name, Category parent) {
        this.name = name;
        this.parent = parent;
    }
}
