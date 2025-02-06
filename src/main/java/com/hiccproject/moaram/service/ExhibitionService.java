package com.hiccproject.moaram.service;

import com.hiccproject.moaram.dto.CreateExhibitionDto;
import com.hiccproject.moaram.dto.KakaoUserInfoDto;
import com.hiccproject.moaram.entity.Exhibition;
import com.hiccproject.moaram.entity.User;
import com.hiccproject.moaram.entity.university.University;
import com.hiccproject.moaram.repository.ExhibitionRepository;
import com.hiccproject.moaram.repository.UniversityRepository;
import com.hiccproject.moaram.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

@Service
@RequiredArgsConstructor
public class ExhibitionService {

    private final ExhibitionRepository exhibitionRepository;
    private final UniversityRepository universityRepository;
    private final UserRepository userRepository;

    private static final String IMAGE_DIR = "uploads/exhibitions/";

    // 전시 등록
    public Exhibition createExhibition(CreateExhibitionDto dto, MultipartFile image, KakaoUserInfoDto userInfo) throws IOException {
        // University 조회
        University university = universityRepository.findById(dto.getUniversityId())
                .orElseThrow(() -> new IllegalArgumentException("University not found"));

        User createdBy = userRepository.findById(userInfo.getId())  // userInfo에서 직접 유저 ID 사용
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        // Exhibition 엔티티 설정
        Exhibition exhibition = new Exhibition();
        exhibition.setUniversity(university);
        exhibition.setLocation(dto.getLocation());
        exhibition.setMajor(dto.getMajor());
        exhibition.setField(dto.getField());
        exhibition.setName(dto.getName());
        exhibition.setDescription(dto.getDescription());
        exhibition.setStartDate(dto.getStartDate());
        exhibition.setEndDate(dto.getEndDate());
        exhibition.setCreatedBy(createdBy);

        // 전시 저장
        Exhibition savedExhibition = exhibitionRepository.save(exhibition);

        // 이미지 저장
        if (image != null && !image.isEmpty()) {
            saveImage(savedExhibition.getId(), image);
        }

        return savedExhibition;
    }

    // 이미지 저장 로직
    private void saveImage(Long exhibitionId, MultipartFile image) throws IOException {
        String folderPath = IMAGE_DIR;
        Files.createDirectories(Paths.get(folderPath));  // 디렉토리 생성

        String filePath = folderPath + exhibitionId + ".jpg";  // 파일 경로 설정
        image.transferTo(new File(filePath));  // 이미지 저장
    }
}
