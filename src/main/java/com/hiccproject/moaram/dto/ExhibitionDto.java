package com.hiccproject.moaram.dto;

import com.hiccproject.moaram.entity.exhibition.Exhibition;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExhibitionDto {

    private Long id;
    private UniversityDto university;
    private String location;
    private String major;
    private FieldDto field;
    private String name;
    private String description;
    private String startDate;
    private String endDate;
    private UserDto createdBy;

    public static ExhibitionDto fromEntity(Exhibition exhibition) {
        return new ExhibitionDto(
                exhibition.getId(),
                new UniversityDto(
                        exhibition.getUniversity().getId(),
                        exhibition.getUniversity().getName(),
                        exhibition.getUniversity().getCampus(),
                        exhibition.getUniversity().getAddress(),
                        new RegionDto(
                                exhibition.getUniversity().getRegion().getId(),
                                exhibition.getUniversity().getRegion().getName()
                        )
                ),
                exhibition.getLocation(),
                exhibition.getMajor(),
                FieldDto.fromEntity(exhibition.getField()), // 수정된 부분
                exhibition.getName(),
                exhibition.getDescription(),
                exhibition.getStartDate().toString(),
                exhibition.getEndDate().toString(),
                new UserDto(
                        exhibition.getCreatedBy().getId(),
                        exhibition.getCreatedBy().getName(),
                        exhibition.getCreatedBy().getEmail()
                )
        );
    }
}
