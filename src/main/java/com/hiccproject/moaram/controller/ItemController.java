package com.hiccproject.moaram.controller;

import com.hiccproject.moaram.dto.CreateItemDto;
import com.hiccproject.moaram.dto.ItemDto;
import com.hiccproject.moaram.dto.KakaoUserInfoDto;
import com.hiccproject.moaram.entity.Item.Item;
import com.hiccproject.moaram.entity.exhibition.Exhibition;
import com.hiccproject.moaram.exception.AlreadyExistsException;
import com.hiccproject.moaram.service.ItemService;
import com.hiccproject.moaram.service.relation.ItemExhibitionService;
import com.hiccproject.moaram.util.ItemStatus;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/api/items")
@RequiredArgsConstructor
public class ItemController {

    private final ItemService itemService;
    private final ItemExhibitionService itemExhibitionService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ItemDto> createItem(
            @RequestParam(value = "universityId", required = false) Long universityId,
            @RequestParam("location") String location,
            @RequestParam("name") String name,
            @RequestParam(value = "price", required = false) Integer price,
            @RequestParam(value = "description", required = false) String description,
            @RequestParam(value = "artworkTypeId", required = false) Long artworkTypeId,
            @RequestParam(value = "materialId", required = false) Long materialId,
            @RequestParam(value = "toolId", required = false) Long toolId,
            @RequestParam(value = "status", required = false) ItemStatus status,
            @RequestParam(value = "exhibitionId", required = false) Long exhibitionId,
            @RequestParam(value = "images") List<MultipartFile> images,
            @RequestAttribute KakaoUserInfoDto kakaoUserInfoDto) {

        try {
            // CreateItemDto에 받은 파라미터들을 사용하여 생성
            CreateItemDto dto = new CreateItemDto(
                    universityId, location, name, price, description,
                    artworkTypeId, materialId, toolId, status
            );

            // Item 생성
            Item item = itemService.createItem(dto, images, kakaoUserInfoDto);
            Exhibition exhibition = null;
            if (exhibitionId != null) {
                exhibition = itemExhibitionService.createAndSaveItemExhibition(item, exhibitionId);  // Exhibition 반환 받기
            }

            // ItemDto 반환, Exhibition 정보 포함
            return ResponseEntity.status(HttpStatus.CREATED).body(ItemDto.fromEntity(item, exhibition));
        } catch (IOException e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @ExceptionHandler(AlreadyExistsException.class)
    @ResponseStatus(HttpStatus.CONFLICT)
    public String handleAlreadyExists(AlreadyExistsException e) {
        return e.getMessage();
    }

    @ExceptionHandler(ResponseStatusException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public String handleNotFound(ResponseStatusException e) {
        return e.getMessage();
    }

    @ExceptionHandler(RuntimeException.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    public String handleInternalError(RuntimeException e) {
        return "Internal Server Error: " + e.getMessage();
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<String> handleMissingParams(MissingServletRequestParameterException ex) {
        return ResponseEntity.badRequest().body("Required request parameter is missing : " + ex.getParameterName());
    }
}
