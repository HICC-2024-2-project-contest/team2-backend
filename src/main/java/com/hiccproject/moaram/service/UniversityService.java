package com.hiccproject.moaram.service;

import com.hiccproject.moaram.dto.RegionDto;
import com.hiccproject.moaram.dto.UniversityDto;
import com.hiccproject.moaram.entity.university.University;
import com.hiccproject.moaram.repository.UniversityRepository;
import com.hiccproject.moaram.repository.specification.UniversitySpecifications;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UniversityService {

    private final UniversityRepository universityRepository;

    public List<UniversityDto> getUniversities(String keyword) {
        List<University> universities;

        universities = universityRepository.findAll(UniversitySpecifications.hasName(keyword));

        return universities.stream()
                .map(university -> new UniversityDto(
                        university.getId(),
                        university.getName(),
                        university.getCampus(),
                        university.getAddress(),
                        new RegionDto(university.getRegion().getId(), university.getRegion().getName())
                ))
                .collect(Collectors.toList());
    }
}
