package com.hiccproject.moaram.service;

import com.hiccproject.moaram.dto.RegionDto;
import com.hiccproject.moaram.repository.RegionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RegionService {

    private final RegionRepository regionRepository;

    public List<RegionDto> getAllRegions() {
        return regionRepository.findAll().stream()
                .map(region -> new RegionDto(region.getId(), region.getName()))
                .collect(Collectors.toList());
    }
}