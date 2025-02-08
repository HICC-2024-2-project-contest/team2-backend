package com.hiccproject.moaram.controller;

import com.hiccproject.moaram.dto.CategoryDto;
import com.hiccproject.moaram.service.CategoryService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    public List<CategoryDto> getCategories() {
        // 카테고리와 각 카테고리에 속한 필드를 포함한 데이터 반환
        return categoryService.getCategoriesWithFields();
    }
}
