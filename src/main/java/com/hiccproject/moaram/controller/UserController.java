package com.hiccproject.moaram.controller;

import com.hiccproject.moaram.dto.KakaoUserInfoDto;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class UserController {

    @GetMapping("/public/demo")
    public String demo() {
        return "demo";
    }

    @GetMapping("/name")
    public String name(@RequestAttribute KakaoUserInfoDto userInfo) {
        return userInfo.getProperties().getNickname();
    }
}