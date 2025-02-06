package com.hiccproject.moaram.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class UniversityDto {

    private Long id;
    private String name;
    private String campus;
    private String address;
    private RegionDto region;

}
