package com.hiccproject.moaram.repository;

import com.hiccproject.moaram.entity.exhibition.Category;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {

    @Query("SELECT c FROM Category c LEFT JOIN FETCH c.fields")
    List<Category> findAllWithFields();  // 카테고리와 관련된 필드들을 함께 로드
}
