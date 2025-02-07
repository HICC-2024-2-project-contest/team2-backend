package com.hiccproject.moaram.dto;

import com.hiccproject.moaram.entity.exhibition.Field;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class FieldDto {
    private Long id;
    private String name;

    public static FieldDto fromEntity(Field field) {
        return new FieldDto(field.getId(), field.getName());
    }
}
