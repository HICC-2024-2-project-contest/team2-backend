package com.hiccproject.moaram.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ExhibitionResponseDto {
    private ExhibitionDto exhibitionDto;  // 기존 전시 정보
    private String base64Image;  // 전시 이미지 (Base64)
}
