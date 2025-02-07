package com.hiccproject.moaram.config;

import com.hiccproject.moaram.dto.KakaoUserInfoDto;
import com.hiccproject.moaram.exception.InvalidTokenException;
import com.hiccproject.moaram.service.KakaoAuthService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

@Component
public class KakaoAuthFilter extends OncePerRequestFilter {

    private final KakaoAuthService kakaoAuthService;

    public KakaoAuthFilter(KakaoAuthService kakaoAuthService) {
        this.kakaoAuthService = kakaoAuthService;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String token = extractToken(request);

        if (token != null) {
            try {
                // Token을 검증하고 사용자 정보를 가져옵니다.
                KakaoUserInfoDto userInfo = kakaoAuthService.validateTokenAndGetUser(token);

                // 사용자 정보를 기반으로 인증 객체 생성
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(userInfo, null, null);

                // SecurityContextHolder에 인증 정보를 설정하여 인증된 상태로 처리하도록 합니다.
                SecurityContextHolder.getContext().setAuthentication(authentication);

                // 요청의 "userInfo" 속성에 사용자 정보 저장
                request.setAttribute("userInfo", userInfo);

            } catch (InvalidTokenException e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
                return;
            }
        }

        filterChain.doFilter(request, response);
    }

    private String extractToken(HttpServletRequest request) {
        String authHeader = request.getHeader("Authorization");
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return null;
    }
}
