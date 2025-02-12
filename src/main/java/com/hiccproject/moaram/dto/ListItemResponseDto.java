package com.hiccproject.moaram.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ListItemResponseDto {
    private ListItemDto listItemDto;
    private String base64Image;
    private boolean isInWishlist;
}
