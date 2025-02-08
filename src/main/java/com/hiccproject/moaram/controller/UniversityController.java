package com.hiccproject.moaram.controller;

import com.hiccproject.moaram.dto.UniversityDto;
import com.hiccproject.moaram.service.UniversityService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/universities")
@RequiredArgsConstructor
public class UniversityController {

    private final UniversityService universityService;

    @GetMapping
    public List<UniversityDto> getUniversities(@RequestParam(required = false) String keyword) {
        return universityService.getUniversities(keyword);
    }
}