package com.hiccproject.moaram.dto;

import com.hiccproject.moaram.entity.exhibition.Category;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.util.List;
import java.util.stream.Collectors;

@Getter
@AllArgsConstructor
public class CategoryDto {
    private Long id;
    private String name;
    private List<FieldDto> fields;  // List<FieldDto>로 수정

    // CategoryDto에서 fromEntity 메서드를 작성하여 카테고리와 필드 목록을 생성하도록 합니다.
    public static CategoryDto fromEntity(Category category) {
        return new CategoryDto(
                category.getId(),
                category.getName(),
                category.getFields().stream()
                        .map(field -> new FieldDto(field.getId(), field.getName()))  // 필드를 FieldDto로 변환
                        .collect(Collectors.toList())  // List<FieldDto>로 수집
        );
    }
}
