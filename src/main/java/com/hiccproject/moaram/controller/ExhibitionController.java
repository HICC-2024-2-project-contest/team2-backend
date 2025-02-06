package com.hiccproject.moaram.controller;

import com.hiccproject.moaram.dto.CreateExhibitionDto;
import com.hiccproject.moaram.dto.ExhibitionDto;
import com.hiccproject.moaram.dto.ExhibitionResponseDto;
import com.hiccproject.moaram.dto.KakaoUserInfoDto;
import com.hiccproject.moaram.entity.Exhibition;
import com.hiccproject.moaram.exception.AlreadyExistsException;
import com.hiccproject.moaram.service.ExhibitionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.time.LocalDate;
import java.util.Map;

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

    @GetMapping("/public/{exhibitionId}")
    public ResponseEntity<ExhibitionResponseDto> getExhibitionWithImage(@PathVariable Long exhibitionId) {
        try {
            // 전시 정보 조회
            Exhibition exhibition = exhibitionService.getExhibition(exhibitionId);
            ExhibitionDto exhibitionDto = ExhibitionDto.fromEntity(exhibition);

            // 이미지 파일 가져오기
            String base64Image = exhibitionService.getImageFromS3(exhibitionId);

            // DTO와 이미지 파일을 함께 반환
            ExhibitionResponseDto response = new ExhibitionResponseDto(exhibitionDto, base64Image);

            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // 전시 목록을 검색하는 라우터
    @GetMapping("/public/search")
    public ResponseEntity<Map<String, Object>> searchExhibitions(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) String keyword,  // name과 universityName을 합쳐 keyword로 받음
            @RequestParam(required = false) String field,
            Pageable pageable) {  // Pageable을 추가하여 페이지네이션 처리
        try {
            // 검색된 전시 목록 반환 (페이지네이션 포함)
            Map<String, Object> response = exhibitionService.searchExhibitionsWithPagination(startDate, endDate, keyword, field, pageable);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }


    @ExceptionHandler(AlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleAlreadyExists(AlreadyExistsException e) {
        return e.getMessage();
    }

    @ExceptionHandler(ResponseStatusException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(ResponseStatusException e) {
        return e.getMessage();
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleInternalError(RuntimeException e) {
        return "Internal Server Error: " + e.getMessage();
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<String> handleMissingParams(MissingServletRequestParameterException ex) {
        return ResponseEntity.badRequest().body("Required request parameter is missing : " + ex.getParameterName());
    }
}


