package com.hiccproject.moaram.service;

import com.hiccproject.moaram.dto.KakaoUserInfoDto;
import com.hiccproject.moaram.exception.InvalidTokenException;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

@Service
public class KakaoAuthService {

    private static final String KAKAO_API_URL = "https://kapi.kakao.com/v2/user/me"; // Kakao API 사용자 정보 조회 URL

    private final WebClient webClient;

    // WebClient.Builder를 사용하여 WebClient 인스턴스 생성
    public KakaoAuthService(WebClient.Builder webClientBuilder) {
        this.webClient = webClientBuilder.baseUrl(KAKAO_API_URL).build();
    }

    public KakaoUserInfoDto validateTokenAndGetUser(String token) {
        try {
            // Kakao API 호출 (Bearer Token 인증 방식)
            String authorizationHeader = "Bearer " + token;

            // GET 요청을 보내기 위한 헤더 설정
            return webClient.get()
                    .uri("")
                    .header(HttpHeaders.AUTHORIZATION, authorizationHeader) // Authorization 헤더 추가
                    .retrieve()
                    .bodyToMono(KakaoUserInfoDto.class) // 응답 본문을 KakaoUserInfoDto로 매핑
                    .block(); // 동기 방식으로 응답 대기
        } catch (WebClientResponseException e) {
            // WebClient 예외 처리 (예: 네트워크 문제, Kakao API 응답 오류 등)
            throw new InvalidTokenException("Failed to validate Kakao Access Token: " + e.getMessage());
        }
    }
}
