package com.hiccproject.moaram.service;

import com.hiccproject.moaram.dto.*;
import com.hiccproject.moaram.entity.Item.*;
import com.hiccproject.moaram.entity.User;
import com.hiccproject.moaram.entity.composite.ItemImageId;
import com.hiccproject.moaram.entity.exhibition.Exhibition;
import com.hiccproject.moaram.entity.relation.ItemExhibition;
import com.hiccproject.moaram.entity.relation.ItemWishlist;
import com.hiccproject.moaram.entity.university.University;
import com.hiccproject.moaram.repository.*;
import com.hiccproject.moaram.repository.specification.ItemSpecifications;
import com.hiccproject.moaram.util.ItemStatus;
import lombok.RequiredArgsConstructor;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ItemService {

    private final ItemRepository itemRepository;
    private final ItemImageRepository itemImageRepository;
    private final ItemWishlistRepository itemWishlistRepository;
    private final ItemExhibitionRepository itemExhibitionRepository;
    private final UniversityRepository universityRepository;
    private final ArtworkTypeRepository artworkTypeRepository;
    private final MaterialRepository materialRepository;
    private final ToolRepository toolRepository;
    private final UserService userService;
    private final S3Service s3Service;

    @Value("${aws.s3.bucket-name}")
    private String BUCKET_NAME; // S3 버킷 이름

    @Value("${aws.s3.item.save-path}")
    private String savePath;

    public Item createItem(CreateItemDto dto, List<MultipartFile> images, KakaoUserInfoDto kakaouserInfo) throws IOException {
        University university = null;
        if (dto.getUniversityId() != null) {
            university = universityRepository.findById(dto.getUniversityId())
                    .orElseThrow(() -> new IllegalArgumentException("University not found"));
        }

        User createdBy = userService.getUserById(kakaouserInfo.getId());

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

    public ItemResponseDto getItem(Long itemId, KakaoUserInfoDto kakaoUserInfoDto) {
        // 아이템과 위시리스트 상태를 함께 조회 (kakaoUserInfoDto가 null일 경우에도 아이템은 조회되어야 함)
        Object[] result = itemWishlistRepository
                .findItemWithWishlistStatus(itemId, (kakaoUserInfoDto != null) ? kakaoUserInfoDto.getId() : null)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Item not found with id: " + itemId));

        Item item = (Item) ((Object[]) result[0])[0];  // 첫 번째 요소는 Item 객체
        ItemWishlist itemWishlist = (ItemWishlist) ((Object[]) result[0])[1];  // 두 번째 요소는 ItemWishlist 객체 (없으면 null)

        // 아이템과 관련된 전시회 조회 (ItemExhibition을 통해 연관된 전시회를 찾음)
        ItemExhibition itemExhibition = itemExhibitionRepository
                .findByItemIdAndItemDeletedTimeIsNullAndExhibitionIsAllowedAndExhibitionDeletedTimeIsNull(itemId, true)
                .orElse(null);  // 전시회가 없으면 null 반환

        Exhibition exhibition = null;
        if (itemExhibition != null) {
            exhibition = itemExhibition.getExhibition();
        }

        // 아이템 DTO 생성
        ItemDto itemDto = ItemDto.fromEntity(item, exhibition);

        // 아이템 이미지 목록 조회 (인덱스 순서대로)
        List<ItemImage> itemImages = itemImageRepository.findByItemIdOrderByIdAsc(itemId);

        // 이미지들을 Base64로 변환하여 리스트에 추가
        List<String> imageNames = new ArrayList<>();
        for (ItemImage itemImage : itemImages) {
            imageNames.add(itemImage.getId().getItem_id() + "_" + itemImage.getId().getIdx()); // ID를 리스트에 추가
        }

        // 여러 이미지를 한 번에 Base64로 변환
        List<String> base64Images = s3Service.getImagesBase64(imageNames, savePath, BUCKET_NAME);

        // 위시리스트 상태 처리 (kakaoUserInfoDto가 null일 경우 false로 설정)
        boolean isInWishlist = (kakaoUserInfoDto != null && itemWishlist != null);

        // 응답 DTO 생성
        return new ItemResponseDto(itemDto, base64Images, isInWishlist);
    }


    public Map<String, Object> searchItemsWithPagination(
            String keyword, Integer artworkTypeId, Integer materialId, Integer toolId,
            ItemStatus status, Integer minPrice, Integer maxPrice, KakaoUserInfoDto kakaoUserInfoDto,
            Pageable pageable) {

        // Item에 대한 동적 Specification 생성
        Specification<Item> spec = Specification.where(ItemSpecifications.hasDeletedTimeNull());

        if (keyword != null) {
            spec = spec.and(ItemSpecifications.hasKeyword(keyword));
        }
        if (artworkTypeId != null) {
            spec = spec.and(ItemSpecifications.hasType(artworkTypeId));
        }
        if (materialId != null) {
            spec = spec.and(ItemSpecifications.hasMaterial(materialId));
        }
        if (toolId != null) {
            spec = spec.and(ItemSpecifications.hasTool(toolId));
        }
        if (status != null) {
            spec = spec.and(ItemSpecifications.hasStatus(status));
        }
        if (minPrice != null) {
            spec = spec.and(ItemSpecifications.hasMinPrice(minPrice));
        }
        if (maxPrice != null) {
            spec = spec.and(ItemSpecifications.hasMaxPrice(maxPrice));
        }

        // Item 조회
        Page<Item> itemPage = itemRepository.findAll(spec, pageable);

        List<ListItemResponseDto> items = new ArrayList<>();

        // 카카오 유저 정보가 있을 경우, ItemWishlist 정보를 한 번에 조회
        if (kakaoUserInfoDto != null) {
            // 카카오 유저 정보가 있을 경우, ItemWishlist 목록을 한 번에 조회
            List<ItemWishlist> itemWishlistList = itemWishlistRepository.findByUserId(kakaoUserInfoDto.getId());

            // ItemWishlist를 Map으로 변환 (아이템 ID를 키로 사용)
            Map<Long, Boolean> wishlistMap = itemWishlistList.stream()
                    .collect(Collectors.toMap(
                            itemWishlist -> itemWishlist.getId().getItemId(),
                            itemWishlist -> true
                    ));

            // Item 리스트를 돌며 위시리스트 상태를 설정
            items = itemPage.stream()
                    .map(item -> {
                        try {
                            // 위시리스트에 해당 아이템이 있는지 확인
                            boolean isInWishlist = wishlistMap.containsKey(item.getId());

                            // ListItemDto 생성
                            ListItemDto listItemDto = ListItemDto.fromEntity(item);

                            // base64Image 처리 (예시 코드)
                            String base64Image = s3Service.getImageBase64(item.getId() + "_0", savePath, BUCKET_NAME);

                            // ItemResponseDto 생성
                            return new ListItemResponseDto(listItemDto, base64Image, isInWishlist);
                        } catch (Exception e) {
                            return null;
                        }
                    })
                    .collect(Collectors.toList());
        } else {
            // 카카오 유저 정보가 없을 경우, 모든 아이템에 대해 wishlist 여부는 false로 처리
            items = itemPage.stream()
                    .map(item -> {
                        try {
                            // ListItemDto 생성
                            ListItemDto listItemDto = ListItemDto.fromEntity(item);

                            // base64Image 처리 (예시 코드)
                            String base64Image = s3Service.getImageBase64(item.getId() + "_0", savePath, BUCKET_NAME);

                            // ItemResponseDto 생성
                            return new ListItemResponseDto(listItemDto, base64Image, false);
                        } catch (Exception e) {
                            return null;
                        }
                    })
                    .collect(Collectors.toList());
        }

        // 페이지 정보와 함께 응답
        Map<String, Object> response = new HashMap<>();
        response.put("items", items);
        response.put("pageInfo", new PageMetadataDto(
                itemPage.getNumber(),
                itemPage.getSize(),
                itemPage.getTotalElements(),
                itemPage.getTotalPages(),
                itemPage.hasNext(),
                itemPage.hasPrevious(),
                itemPage.isFirst(),
                itemPage.isLast()
        ));

        return response;
    }
}
