package com.hiccproject.moaram.controller;

import com.hiccproject.moaram.dto.CreateExhibitionDto;
import com.hiccproject.moaram.dto.KakaoUserInfoDto;
import com.hiccproject.moaram.entity.Exhibition;
import com.hiccproject.moaram.service.ExhibitionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/exhibitions")
@RequiredArgsConstructor
public class ExhibitionController {

    private final ExhibitionService exhibitionService;

    @PostMapping("/create")
    public ResponseEntity<Exhibition> createExhibition(
            @Valid @ModelAttribute CreateExhibitionDto dto,
            @RequestParam("image") MultipartFile image,
            @RequestAttribute KakaoUserInfoDto kakaoUserInfoDto) {
        try {
            Exhibition exhibition = exhibitionService.createExhibition(dto, image, kakaoUserInfoDto);
            return ResponseEntity.ok(exhibition);
        } catch (IOException e) {
            return ResponseEntity.internalServerError().body(null);
        }
    }
}
