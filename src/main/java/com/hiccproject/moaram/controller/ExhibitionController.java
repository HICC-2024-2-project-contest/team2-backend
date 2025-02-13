package com.hiccproject.moaram.controller;

import com.hiccproject.moaram.dto.CreateExhibitionDto;
import com.hiccproject.moaram.dto.ExhibitionDto;
import com.hiccproject.moaram.dto.ExhibitionResponseDto;
import com.hiccproject.moaram.dto.KakaoUserInfoDto;
import com.hiccproject.moaram.entity.exhibition.Exhibition;
import com.hiccproject.moaram.exception.AlreadyExistsException;
import com.hiccproject.moaram.service.ExhibitionService;
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
            @RequestParam("universityId") Long universityId,
            @RequestParam(value = "location", required = false) String location,
            @RequestParam("major") String major,
            @RequestParam("fieldId") Long fieldId,
            @RequestParam("name") String name,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam("startDate") String startDate,
            @RequestParam("endDate") String endDate,
            @RequestParam("image") MultipartFile image,
            @RequestAttribute KakaoUserInfoDto kakaoUserInfoDto) {

        try {
            CreateExhibitionDto dto = new CreateExhibitionDto();
            dto.setUniversityId(universityId);
            dto.setLocation(location);
            dto.setMajor(major);
            dto.setFieldId(fieldId);  // 필드 ID 설정
            dto.setName(name);
            dto.setDescription(description);
            dto.setStartDate(LocalDate.parse(startDate));
            dto.setEndDate(LocalDate.parse(endDate));

            Exhibition exhibition = exhibitionService.createExhibition(dto, image, kakaoUserInfoDto);
            return ResponseEntity.status(HttpStatus.CREATED).body(ExhibitionDto.fromEntity(exhibition));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/scrap/{exhibitionId}")
    public ResponseEntity<Void> scrapExhibition(
            @PathVariable Long exhibitionId,
            @RequestAttribute KakaoUserInfoDto kakaoUserInfoDto) {

        exhibitionService.scrapExhibition(kakaoUserInfoDto, exhibitionId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @GetMapping("/{exhibitionId}")
    public ResponseEntity<ExhibitionResponseDto> getExhibitionWithImage(@PathVariable Long exhibitionId) {
        try {
            Exhibition exhibition = exhibitionService.getExhibition(exhibitionId);
            ExhibitionDto exhibitionDto = ExhibitionDto.fromEntity(exhibition);
            String base64Image = exhibitionService.getImageBase64(exhibitionId);

            ExhibitionResponseDto response = new ExhibitionResponseDto(exhibitionDto, base64Image);
            return ResponseEntity.ok(response);
        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/search")
    public ResponseEntity<Map<String, Object>> searchExhibitions(
            @RequestParam(required = false) LocalDate startDate,
            @RequestParam(required = false) LocalDate endDate,
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) Long fieldId,  // String → Long 변경
            @RequestAttribute(required = false) KakaoUserInfoDto kakaoUserInfoDto,
            Pageable pageable) {
        try {
            Map<String, Object> response = exhibitionService.searchExhibitionsWithPagination(startDate, endDate, keyword, fieldId, kakaoUserInfoDto, pageable);
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
