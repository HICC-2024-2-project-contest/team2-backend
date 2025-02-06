package com.hiccproject.moaram.controller;

import com.hiccproject.moaram.dto.KakaoUserInfoDto;
import com.hiccproject.moaram.dto.UserDto;
import com.hiccproject.moaram.entity.User;
import com.hiccproject.moaram.exception.AlreadyExistsException;
import com.hiccproject.moaram.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequestMapping("/api/users")
@RequiredArgsConstructor
public class UserController {

    private final UserService userService;

    @PostMapping
    public UserDto createOrUpdateUser(@RequestAttribute KakaoUserInfoDto kakaoUserInfoDto) {
        return userService.processUser(kakaoUserInfoDto);
    }

    @GetMapping("/public/{id}")
    public UserDto getUser(@PathVariable Long id) {
        // 유저를 찾고, DTO로 변환하여 반환
        User user = userService.getUserById(id);
        return new UserDto(user.getId(), user.getName(), user.getEmail());
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
}
