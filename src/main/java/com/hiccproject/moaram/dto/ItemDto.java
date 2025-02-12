package com.hiccproject.moaram.dto;

import com.hiccproject.moaram.entity.Item.Item;
import com.hiccproject.moaram.entity.exhibition.Exhibition;
import com.hiccproject.moaram.util.ItemStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ItemDto {
    private Long id;
    private String name;
    private UniversityDto universityDto;
    private String location;
    private Integer price;
    private String description;
    private ItemStatus status;
    private LocalDateTime createdTime;
    private ExhibitionDto exhibitionDto;  // ExhibitionDto 추가

    public static ItemDto fromEntity(Item item, Exhibition exhibition) {
        // exhibition 처리
        ExhibitionDto exhibitionDto = null;
        if (exhibition != null) {
            exhibitionDto = ExhibitionDto.fromEntity(exhibition);  // exhibition이 null이 아니면 fromEntity 실행
        }

        // university 처리
        UniversityDto universityDto = null;
        if (item.getUniversity() != null) {
            universityDto = new UniversityDto(
                    item.getUniversity().getId(),
                    item.getUniversity().getName(),
                    item.getUniversity().getCampus(),
                    item.getUniversity().getAddress(),
                    new RegionDto(
                            item.getUniversity().getRegion().getId(),
                            item.getUniversity().getRegion().getName()
                    )
            );
        }

        return new ItemDto(
                item.getId(),
                item.getName(),
                universityDto,
                item.getLocation(),
                item.getPrice(),
                item.getDescription(),
                item.getStatus(),
                item.getCreatedTime(),
                exhibitionDto  // exhibition이 null이면 null로 설정
        );
    }

}
