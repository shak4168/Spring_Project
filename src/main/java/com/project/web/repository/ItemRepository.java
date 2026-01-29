package com.project.web.repository;

import com.project.web.domain.item.Item;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ItemRepository extends JpaRepository<Item, Long> {
    // 기본 CRUD(저장, 조회)는 JpaRepository가 다 해줍니다.
}