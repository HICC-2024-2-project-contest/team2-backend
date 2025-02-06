package com.hiccproject.moaram.controller;

import com.hiccproject.moaram.dto.CreateExhibitionDto;
import com.hiccproject.moaram.dto.ExhibitionDto;
import com.hiccproject.moaram.dto.KakaoUserInfoDto;
import com.hiccproject.moaram.entity.Exhibition;
import com.hiccproject.moaram.service.ExhibitionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDate;

@RestController
@RequestMapping("/api/exhibitions")
@RequiredArgsConstructor
public class ExhibitionController {

    private final ExhibitionService exhibitionService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ExhibitionDto> createExhibition(
            @Valid @RequestParam("universityId") Long universityId,
            @RequestParam("location") String location,
            @Valid @RequestParam("major") String major,
            @RequestParam("field") String field,
            @Valid @RequestParam("name") String name,
            @RequestParam("description") String description,
            @Valid @RequestParam("startDate") String startDate,
            @Valid @RequestParam("endDate") String endDate,
            @RequestParam("image") MultipartFile image,
            @RequestAttribute KakaoUserInfoDto kakaoUserInfoDto) {

        try {
            CreateExhibitionDto dto = new CreateExhibitionDto();
            dto.setUniversityId(universityId);
            dto.setLocation(location);
            dto.setMajor(major);
            dto.setField(field);
            dto.setName(name);
            dto.setDescription(description);
            dto.setStartDate(LocalDate.parse(startDate));
            dto.setEndDate(LocalDate.parse(endDate));

            Exhibition exhibition = exhibitionService.createExhibition(dto, image, kakaoUserInfoDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(ExhibitionDto.fromEntity(exhibition));  // DTO 변환
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}


