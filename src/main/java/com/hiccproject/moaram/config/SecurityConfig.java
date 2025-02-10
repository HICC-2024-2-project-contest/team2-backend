package com.hiccproject.moaram.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final KakaoAuthFilter kakaoAuthFilter;
    private final AuthenticatedUrlsConfig authenticatedUrlsConfig;
    private final CorsProperties corsProperties; // CORS 설정 불러오기

    public SecurityConfig(KakaoAuthFilter kakaoAuthFilter, AuthenticatedUrlsConfig authenticatedUrlsConfig, CorsProperties corsProperties) {
        this.kakaoAuthFilter = kakaoAuthFilter;
        this.authenticatedUrlsConfig = authenticatedUrlsConfig;
        this.corsProperties = corsProperties;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(authenticatedUrlsConfig.getAuthenticatedUrls().toArray(new String[0])).authenticated()
                        .anyRequest().permitAll()
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
