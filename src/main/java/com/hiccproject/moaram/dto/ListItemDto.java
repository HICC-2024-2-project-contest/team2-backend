package com.hiccproject.moaram.dto;

import com.hiccproject.moaram.entity.Item.Item;
import com.hiccproject.moaram.entity.User;
import lombok.AllArgsConstructor;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
@AllArgsConstructor
public class ListItemDto {
    private Long id;
    private String name;
    private Integer price;
    private LocalDateTime createdTime;
    private UserDto user;

    public static ListItemDto fromEntity(Item item) {
        User user = item.getCreatedBy();
        UserDto userDto = new UserDto(user.getId(), user.getName(), user.getEmail());

        return new ListItemDto(
                item.getId(),
                item.getName(),
                item.getPrice(),
                item.getCreatedTime(),
                userDto
        );
    }
}
