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
    private final AuthenticatedUrlsConfig authenticatedUrlsConfig;

    public SecurityConfig(KakaoAuthFilter kakaoAuthFilter, AuthenticatedUrlsConfig authenticatedUrlsConfig) {
        this.kakaoAuthFilter = kakaoAuthFilter;
        this.authenticatedUrlsConfig = authenticatedUrlsConfig;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(authenticatedUrlsConfig.getAuthenticatedUrls().toArray(new String[0])).authenticated() // 특정 URL은 인증 필요
                        .anyRequest().permitAll() // 나머지 URL은 인증 없이 접근 가능
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
