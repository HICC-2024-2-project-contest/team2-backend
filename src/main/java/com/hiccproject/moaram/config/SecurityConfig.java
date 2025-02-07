package com.hiccproject.moaram.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final KakaoAuthFilter kakaoAuthFilter;
    private final AllowedUrlsConfig allowedUrlsConfig;

    public SecurityConfig(KakaoAuthFilter kakaoAuthFilter, AllowedUrlsConfig allowedUrlsConfig) {
        this.kakaoAuthFilter = kakaoAuthFilter;
        this.allowedUrlsConfig = allowedUrlsConfig;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(allowedUrlsConfig.getAllowedUrls().toArray(new String[0])).permitAll()  // 설정된 URL 사용
                        .anyRequest().authenticated()
                )
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint((request, response, authException) -> {
                            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Unauthorized");
                        })
                )
                .addFilterBefore(kakaoAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}
