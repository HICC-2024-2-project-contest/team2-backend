package com.hiccproject.moaram.service;

import com.hiccproject.moaram.dto.KakaoUserInfoDto;
import com.hiccproject.moaram.dto.UserDto;
import com.hiccproject.moaram.entity.User;
import com.hiccproject.moaram.exception.AlreadyExistsException;
import com.hiccproject.moaram.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public UserDto processUser(KakaoUserInfoDto kakaoUserInfoDto) {
        // 카카오에서 받은 id로 유저가 존재하고, is_deleted가 false인 유저가 있는지 확인
        return (UserDto) userRepository.findByIdAndIsDeletedFalse(kakaoUserInfoDto.getId())
                .map(user -> {
                    throw new AlreadyExistsException("User already exists with id: " + kakaoUserInfoDto.getId());
                })
                .orElseGet(() -> createNewUser(kakaoUserInfoDto));
    }

    private UserDto createNewUser(KakaoUserInfoDto kakaoUserInfoDto) {
        try {
            // 새로운 사용자 생성
            User newUser = new User();
            newUser.setId(kakaoUserInfoDto.getId());  // 카카오에서 받은 id를 유저의 id로 설정
            newUser.setName(kakaoUserInfoDto.getProperties().getNickname());
            newUser.setEmail(kakaoUserInfoDto.getKakaoAccount().getEmail());
            User savedUser = userRepository.save(newUser);

            // User -> UserDto 변환 후 반환
            return new UserDto(savedUser.getId(), savedUser.getName(), savedUser.getEmail());
        } catch (Exception e) {
            throw new RuntimeException("Error while creating new user", e); // 비즈니스 로직 오류 처리
        }
    }

    public User getUserById(Long id) {
        return userRepository.findByIdAndIsDeletedFalse(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with id: " + id));
    }
}
