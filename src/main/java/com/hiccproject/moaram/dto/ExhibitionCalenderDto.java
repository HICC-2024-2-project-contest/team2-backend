package com.hiccproject.moaram.dto;

import com.hiccproject.moaram.entity.exhibition.Exhibition;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class ExhibitionCalenderDto {
    private Long id;
    private UniversityDto university;
    private String location;
    private String major;
    private String name;
    private String startDate;
    private String endDate;

    public static ExhibitionCalenderDto fromEntity(Exhibition exhibition) {
        return new ExhibitionCalenderDto(
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
                exhibition.getName(),
                exhibition.getStartDate().toString(),
                exhibition.getEndDate().toString()
        );
    }
}
