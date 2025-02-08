package com.hiccproject.moaram.service;

import com.hiccproject.moaram.dto.CategoryDto;
import com.hiccproject.moaram.entity.exhibition.Category;
import com.hiccproject.moaram.repository.CategoryRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public List<CategoryDto> getCategoriesWithFields() {
        // 카테고리와 각 카테고리에 속한 필드 목록을 가져오기
        List<Category> categories = categoryRepository.findAll();
        return categories.stream()
                .map(CategoryDto::fromEntity)  // CategoryDto에서 카테고리와 필드 목록을 처리
                .collect(Collectors.toList());
    }
}
