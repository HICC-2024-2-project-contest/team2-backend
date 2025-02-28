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

        if (token != null) { // 토큰이 있을 경우에만 검증
            try {
                KakaoUserInfoDto kakaoUserInfoDto = kakaoAuthService.validateTokenAndGetUser(token);
                UsernamePasswordAuthenticationToken authentication =
                        new UsernamePasswordAuthenticationToken(kakaoUserInfoDto, null, null);

                SecurityContextHolder.getContext().setAuthentication(authentication);
                request.setAttribute("kakaoUserInfoDto", kakaoUserInfoDto);

            } catch (InvalidTokenException e) {
                response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid Token");
                return;
            }
        }

        // 토큰이 없으면 아무 작업도 하지 않고 다음 필터 진행
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
