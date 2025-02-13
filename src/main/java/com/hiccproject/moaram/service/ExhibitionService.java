package com.hiccproject.moaram.service;

import com.hiccproject.moaram.dto.*;
import com.hiccproject.moaram.entity.User;
import com.hiccproject.moaram.entity.composite.ExhibitionScrapId;
import com.hiccproject.moaram.entity.exhibition.Exhibition;
import com.hiccproject.moaram.entity.exhibition.Field;
import com.hiccproject.moaram.entity.relation.ExhibitionScrap;
import com.hiccproject.moaram.entity.university.University;
import com.hiccproject.moaram.repository.*;
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
import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class ExhibitionService {

    private final ExhibitionRepository exhibitionRepository;
    private final ExhibitionScrapRepository exhibitionScrapRepository;
    private final UniversityRepository universityRepository;
    private final FieldRepository fieldRepository;
    private final UserService userService;
    private final S3Service s3Service;

    @Value("${aws.s3.bucket-name}")
    private String BUCKET_NAME; // S3 버킷 이름

    @Value("${aws.s3.exhibition.save-path}")
    private String savePath;

    @Autowired
    public ExhibitionService(ExhibitionRepository exhibitionRepository, ExhibitionScrapRepository exhibitionScrapRepository,
                             UniversityRepository universityRepository, FieldRepository fieldRepository,
                             UserRepository userRepository, UserService userService,
                             S3Service s3Service) {
        this.exhibitionRepository = exhibitionRepository;
        this.exhibitionScrapRepository = exhibitionScrapRepository;
        this.universityRepository = universityRepository;
        this.fieldRepository = fieldRepository;
        this.userService = userService;
        this.s3Service = s3Service;
    }

    public Object getScrapStatus(Long exhibitionId, String token) {
        boolean isScrapped = exhibitionRepository.existsById(exhibitionId);

        // 토큰이 없을 경우
        if (token == null || token.isBlank()) {
            return isScrapped ? "scrap" : false;
        }

        // 토큰이 호출될 경우
        return exhibitionRepository.findById(exhibitionId)
                .<Object>map(exhibition -> exhibition)
                .orElse(true);
    }

    @Transactional
    public Exhibition createExhibition(CreateExhibitionDto dto, MultipartFile image, KakaoUserInfoDto kakaouserInfo) throws IOException {
        University university = universityRepository.findById(dto.getUniversityId())
                .orElseThrow(() -> new IllegalArgumentException("University not found"));

        User createdBy = userService.getUserById(kakaouserInfo.getId());

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
                s3Service.uploadImage(savedExhibition.getId(), savePath, BUCKET_NAME, image);
            } catch (IOException e) {
                throw new RuntimeException("Failed to save image to S3", e);
            }
        }

        return savedExhibition;
    }

    @Transactional
    public void scrapExhibition(KakaoUserInfoDto kakaoUserInfoDto, Long exhibitionId) {
        User user = userService.getUserById(kakaoUserInfoDto.getId());
        Exhibition exhibition = getExhibition(exhibitionId);

        ExhibitionScrapId scrapId = new ExhibitionScrapId();
        scrapId.setUserId(kakaoUserInfoDto.getId());
        scrapId.setExhibitionId(exhibitionId);

        if (exhibitionScrapRepository.existsById(scrapId)) {
            throw new IllegalStateException("Exhibition already scrapped");
        }

        ExhibitionScrap exhibitionScrap = new ExhibitionScrap();
        exhibitionScrap.setId(scrapId);
        exhibitionScrap.setUser(user);
        exhibitionScrap.setExhibition(exhibition);
        exhibitionScrapRepository.save(exhibitionScrap);
    }

    // 전시 정보를 가져오는 메서드
    public Exhibition getExhibition(Long exhibitionId) {
        return exhibitionRepository.findByIdAndIsAllowedAndDeletedTimeIsNull(exhibitionId, true)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Exhibition not found with id: " + exhibitionId));
    }

    public String getImageBase64(Long exhibitionId) throws IOException {
        return s3Service.getImageBase64(exhibitionId.toString(), savePath, BUCKET_NAME);
    }

    public Map<String, Object> searchExhibitionsWithPagination(
            LocalDate startDate, LocalDate endDate, String keyword, Long fieldId, KakaoUserInfoDto kakaoUserInfoDto, Pageable pageable) throws IOException {

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
                        String base64Image = s3Service.getImageBase64(exhibition.getId().toString(), savePath, BUCKET_NAME);
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
}
