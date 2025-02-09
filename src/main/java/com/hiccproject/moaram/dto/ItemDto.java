package com.hiccproject.moaram.dto;

import com.hiccproject.moaram.entity.Item.Item;
import com.hiccproject.moaram.entity.exhibition.Exhibition;
import com.hiccproject.moaram.util.ItemStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ItemDto {
    private Long id;
    private String name;
    private String location;
    private Integer price;
    private String description;
    private ItemStatus status;
    private ExhibitionDto exhibition;  // ExhibitionDto 추가

    public static ItemDto fromEntity(Item item, Exhibition exhibition) {
        ExhibitionDto exhibitionDto = null;
        if (exhibition != null) {
            exhibitionDto = ExhibitionDto.fromEntity(exhibition);  // exhibition이 null이 아니면 fromEntity 실행
        }

        return new ItemDto(
                item.getId(),
                item.getName(),
                item.getLocation(),
                item.getPrice(),
                item.getDescription(),
                item.getStatus(),
                exhibitionDto  // exhibition이 null이면 null로 설정
        );
    }

}
