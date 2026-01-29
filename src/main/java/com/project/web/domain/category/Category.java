package com.project.web.domain.category;

import java.util.ArrayList;
import java.util.List;

import com.project.web.domain.BaseEntity;

import jakarta.persistence.CascadeType;
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
 * Category 테이블을 만드는 엔터티
 * 
 * */

@Entity
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseEntity {

    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    // 내 부모 카테고리 (예: '의류'가 부모, '청바지'가 자식일 때 '청바지'의 parent는 '의류')
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_id")
    private Category parent;

    // 내 자식 카테고리들
    @OneToMany(mappedBy = "parent", cascade = CascadeType.ALL)
    private List<Category> children = new ArrayList<>();

    @Builder
    public Category(String name, Category parent) {
        this.name = name;
        if (parent != null) {
            this.parent = parent;
            // 부모 카테고리의 자식 리스트에 나(this)를 추가 (연관관계 편의 메서드)
            this.parent.getChildren().add(this);
        }
    }
}