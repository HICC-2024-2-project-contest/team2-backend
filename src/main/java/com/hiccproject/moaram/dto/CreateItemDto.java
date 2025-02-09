package com.hiccproject.moaram.dto;

import com.hiccproject.moaram.util.ItemStatus;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CreateItemDto {

    private Long universityId;

    @NotNull
    private String location;

    @NotNull
    private String name;

    private Integer price;

    private String description;

    private Long artworkTypeId;

    private Long materialId;

    private Long toolId;

    private ItemStatus status;
}
