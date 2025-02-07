package com.hiccproject.moaram.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;
import com.amazonaws.services.s3.model.S3Object;
import com.amazonaws.services.s3.model.S3ObjectInputStream;
import com.amazonaws.util.IOUtils;
import com.hiccproject.moaram.dto.*;
import com.hiccproject.moaram.entity.User;
import com.hiccproject.moaram.entity.exhibition.Exhibition;
import com.hiccproject.moaram.entity.exhibition.Field;
import com.hiccproject.moaram.entity.university.University;
import com.hiccproject.moaram.repository.ExhibitionRepository;
import com.hiccproject.moaram.repository.FieldRepository;
import com.hiccproject.moaram.repository.UniversityRepository;
import com.hiccproject.moaram.repository.UserRepository;
import com.hiccproject.moaram.repository.specification.ExhibitionSpecifications;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDate;
import java.util.Base64;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExhibitionService {

    private final ExhibitionRepository exhibitionRepository;
    private final UniversityRepository universityRepository;

    private final FieldRepository fieldRepository;

    private final UserRepository userRepository;
    private final AmazonS3 amazonS3;  // S3 클라이언트

    @Value("${aws.s3.bucket-name}")
    private String BUCKET_NAME; // S3 버킷 이름

    @Autowired
    public ExhibitionService(ExhibitionRepository exhibitionRepository,
                             UniversityRepository universityRepository, FieldRepository fieldRepository,
                             UserRepository userRepository,
                             AmazonS3 amazonS3) {
        this.exhibitionRepository = exhibitionRepository;
        this.universityRepository = universityRepository;
        this.fieldRepository = fieldRepository;
        this.userRepository = userRepository;
        this.amazonS3 = amazonS3;
    }

    @Transactional
    public Exhibition createExhibition(CreateExhibitionDto dto, MultipartFile image, KakaoUserInfoDto userInfo) throws IOException {
        University university = universityRepository.findById(dto.getUniversityId())
                .orElseThrow(() -> new IllegalArgumentException("University not found"));

        User createdBy = userRepository.findById(userInfo.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Field field = fieldRepository.findById(dto.getFieldId())
                .orElseThrow(() -> new IllegalArgumentException("Field not found"));

        Exhibition exhibition = new Exhibition();
        exhibition.setUniversity(university);
        exhibition.setLocation(dto.getLocation());
        exhibition.setMajor(dto.getMajor());
        exhibition.setField(field);
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

    // 전시 정보를 가져오는 메서드
    public Exhibition getExhibition(Long exhibitionId) {
        return exhibitionRepository.findByIdAndIsAllowedAndDeletedTimeIsNull(exhibitionId, true)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exhibition not found with id: " + exhibitionId));
    }

    public Map<String, Object> searchExhibitionsWithPagination(
            LocalDate startDate, LocalDate endDate, String keyword, Long fieldId, Pageable pageable) throws IOException {

        Specification<Exhibition> spec = Specification.where(ExhibitionSpecifications.hasIsAllowedTrue())
                .and(ExhibitionSpecifications.hasDeletedTimeNull());

        if (startDate != null) {
            spec = spec.and(ExhibitionSpecifications.hasStartDate(startDate));
        }
        if (endDate != null) {
            spec = spec.and(ExhibitionSpecifications.hasEndDate(endDate));
        }
        if (keyword != null) {
            spec = spec.and(ExhibitionSpecifications.hasKeyword(keyword));
        }
        if (fieldId != null) {
            spec = spec.and(ExhibitionSpecifications.hasField(fieldId));
        }

        // 페이지네이션을 포함한 결과 조회
        Page<Exhibition> exhibitionPage = exhibitionRepository.findAll(spec, pageable);

        // 전시 정보와 이미지만 포함한 DTO 리스트
        List<ExhibitionResponseDto> exhibitions = exhibitionPage.stream()
                .map(exhibition -> {
                    try {
                        String base64Image = getImageFromS3(exhibition.getId());
                        return new ExhibitionResponseDto(
                                ExhibitionDto.fromEntity(exhibition),
                                base64Image
                        );
                    } catch (IOException e) {
                        return null;
                    }
                })
                .collect(Collectors.toList());

        // 페이지네이션 메타데이터 포함
        Map<String, Object> response = new HashMap<>();
        response.put("exhibitions", exhibitions);
        response.put("pageInfo", new PageMetadataDto(
                exhibitionPage.getNumber(),
                exhibitionPage.getSize(),
                exhibitionPage.getTotalElements(),
                exhibitionPage.getTotalPages(),
                exhibitionPage.hasNext(),
                exhibitionPage.hasPrevious(),
                exhibitionPage.isFirst(),
                exhibitionPage.isLast()
        ));

        return response;
    }


    private void saveImageToS3(Long exhibitionId, MultipartFile image) throws IOException {
        String uniqueFileName = exhibitionId + ".jpeg";
        String filePath = "exhibitions/" + uniqueFileName;

        // 파일 크기 설정
        ObjectMetadata metadata = new ObjectMetadata();
        metadata.setContentLength(image.getSize());  // 파일 크기 설정
        metadata.setContentType(image.getContentType());  // MIME 타입 설정

        // S3에 업로드
        try (InputStream inputStream = image.getInputStream()) {
            amazonS3.putObject(new PutObjectRequest(BUCKET_NAME, filePath, inputStream, metadata));
        }
    }

    // 이미지 파일을 S3에서 가져오는 메서드
    public String getImageFromS3(Long exhibitionId) throws IOException {
        String filePath = "exhibitions/" + exhibitionId + ".jpeg";
        S3Object s3Object = amazonS3.getObject(BUCKET_NAME, filePath);
        S3ObjectInputStream inputStream = s3Object.getObjectContent();
        byte[] imageBytes = IOUtils.toByteArray(inputStream);

        // Base64로 변환
        return Base64.getEncoder().encodeToString(imageBytes);
    }
}

