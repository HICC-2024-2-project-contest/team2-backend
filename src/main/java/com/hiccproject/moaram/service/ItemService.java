package com.hiccproject.moaram.service;

import com.hiccproject.moaram.dto.CreateItemDto;
import com.hiccproject.moaram.dto.KakaoUserInfoDto;
import com.hiccproject.moaram.entity.Item.*;
import com.hiccproject.moaram.entity.User;
import com.hiccproject.moaram.entity.composite.ItemImageId;
import com.hiccproject.moaram.entity.university.University;
import com.hiccproject.moaram.repository.*;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemImageRepository itemImageRepository;
    private final UniversityRepository universityRepository;
    private final UserRepository userRepository;
    private final ArtworkTypeRepository artworkTypeRepository;
    private final MaterialRepository materialRepository;
    private final ToolRepository toolRepository;
    private final S3Service s3Service;

    @Value("${aws.s3.bucket-name}")
    private String BUCKET_NAME; // S3 버킷 이름

    @Value("${aws.s3.item.save-path}")
    private String savePath;

    public Item createItem(CreateItemDto dto, List<MultipartFile> images, KakaoUserInfoDto userInfo) throws IOException {
        University university = null;
        if (dto.getUniversityId() != null) {
            university = universityRepository.findById(dto.getUniversityId())
                    .orElseThrow(() -> new IllegalArgumentException("University not found"));
        }

        User createdBy = userRepository.findById(userInfo.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        ArtworkType artworkType = null;
        if (dto.getArtworkTypeId() != null) {
            artworkType = artworkTypeRepository.findById(dto.getArtworkTypeId())
                    .orElseThrow(() -> new IllegalArgumentException("ArtworkType not found"));
        }

        Material material = null;
        if (dto.getMaterialId() != null) {
            material = materialRepository.findById(dto.getMaterialId())
                    .orElseThrow(() -> new IllegalArgumentException("Material not found"));
        }

        Tool tool = null;
        if (dto.getToolId() != null) {
            tool = toolRepository.findById(dto.getToolId())
                    .orElseThrow(() -> new IllegalArgumentException("Tool not found"));
        }

        // Item 객체 생성 및 값 설정
        Item item = new Item();
        item.setUniversity(university);  // universityId가 null인 경우 null로 설정됨
        item.setLocation(dto.getLocation());
        item.setName(dto.getName());
        item.setPrice(dto.getPrice());
        item.setDescription(dto.getDescription());
        item.setArtworkType(artworkType);  // artworkTypeId가 null인 경우 null로 설정됨
        item.setMaterial(material);  // materialId가 null인 경우 null로 설정됨
        item.setTool(tool);  // toolId가 null인 경우 null로 설정됨
        item.setStatus(dto.getStatus());
        item.setCreatedBy(createdBy);

        // 아이템 DB에 저장
        Item savedItem = itemRepository.save(item);

        // 이미지가 있으면 S3에 업로드 후 DB에 이미지 URL 저장
        if (images != null && !images.isEmpty()) {
            s3Service.uploadImages(savedItem.getId(), savePath, BUCKET_NAME, images);

            // 순서대로 ItemImage 객체를 만들어 저장
            List<ItemImage> itemImages = new ArrayList<>();
            for (int i = 0; i < images.size(); i++) {
                ItemImage itemImage = new ItemImage();

                // 복합 키로 ItemImageId 설정
                ItemImageId itemImageId = new ItemImageId();
                itemImageId.setItem_id(savedItem.getId());
                itemImageId.setIdx(i);

                itemImage.setId(itemImageId);

                itemImages.add(itemImage);
            }

            // ItemImage 객체들을 DB에 저장
            itemImageRepository.saveAll(itemImages);
        }

        return savedItem;
    }

}
