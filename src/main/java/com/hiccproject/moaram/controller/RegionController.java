package com.hiccproject.moaram.controller;

import com.hiccproject.moaram.dto.RegionDto;
import com.hiccproject.moaram.entity.university.Region;
import com.hiccproject.moaram.service.RegionService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/regions")
@RequiredArgsConstructor
public class RegionController {

    private final RegionService regionService;

    @GetMapping
    public List<RegionDto> getAllRegions() {
        return regionService.getAllRegions();
    }
}