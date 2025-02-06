package com.hiccproject.moaram.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.hiccproject.moaram.dto.CreateExhibitionDto;
import com.hiccproject.moaram.dto.KakaoUserInfoDto;
import com.hiccproject.moaram.entity.Exhibition;
import com.hiccproject.moaram.entity.User;
import com.hiccproject.moaram.entity.university.University;
import com.hiccproject.moaram.repository.ExhibitionRepository;
import com.hiccproject.moaram.repository.UniversityRepository;
import com.hiccproject.moaram.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@Service
public class ExhibitionService {

    private final ExhibitionRepository exhibitionRepository;
    private final UniversityRepository universityRepository;
    private final UserRepository userRepository;
    private final AmazonS3 amazonS3;  // S3 클라이언트

    @Value("${aws.s3.bucket-name}")
    private String BUCKET_NAME; // S3 버킷 이름

    @Autowired
    public ExhibitionService(ExhibitionRepository exhibitionRepository,
                             UniversityRepository universityRepository,
                             UserRepository userRepository,
                             AmazonS3 amazonS3) {
        this.exhibitionRepository = exhibitionRepository;
        this.universityRepository = universityRepository;
        this.userRepository = userRepository;
        this.amazonS3 = amazonS3;
    }

    @Transactional
    public Exhibition createExhibition(CreateExhibitionDto dto, MultipartFile image, KakaoUserInfoDto userInfo) throws IOException {
        University university = universityRepository.findById(dto.getUniversityId())
                .orElseThrow(() -> new IllegalArgumentException("University not found"));

        User createdBy = userRepository.findById(userInfo.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

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

        Exhibition savedExhibition = exhibitionRepository.save(exhibition);

        if (image != null && !image.isEmpty()) {
            try {
                saveImageToS3(savedExhibition.getId(), image);
            } catch (IOException e) {
                throw new RuntimeException("Failed to save image to S3", e);
            }
        }

        return savedExhibition;
    }

    private void saveImageToS3(Long exhibitionId, MultipartFile image) throws IOException {
        String uniqueFileName = exhibitionId + getExtension(image);
        String filePath = "exhibitions/" + uniqueFileName;

        amazonS3.putObject(new PutObjectRequest(BUCKET_NAME, filePath, image.getInputStream(), null));
    }

    private String getExtension(MultipartFile image) {
        String contentType = image.getContentType();
        if (contentType != null && contentType.contains("image")) {
            String[] parts = contentType.split("/");
            return "." + parts[1];  // image/jpeg -> .jpeg
        }
        return ".jpg";  // 기본 .jpg 확장자
    }
}

