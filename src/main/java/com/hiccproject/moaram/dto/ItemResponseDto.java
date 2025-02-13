package com.hiccproject.moaram.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ItemResponseDto {
    private ItemDto itemDto;
    private List<String> base64Images; // 여러 이미지를 담을 List로 변경
    private boolean isInWishlist;
}
