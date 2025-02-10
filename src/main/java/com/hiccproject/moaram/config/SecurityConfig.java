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
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final KakaoAuthFilter kakaoAuthFilter;
    private final AuthenticatedUrlsConfig authenticatedUrlsConfig;
    private final CorsProperties corsProperties;

    public SecurityConfig(KakaoAuthFilter kakaoAuthFilter, AuthenticatedUrlsConfig authenticatedUrlsConfig, CorsProperties corsProperties) {
        this.kakaoAuthFilter = kakaoAuthFilter;
        this.authenticatedUrlsConfig = authenticatedUrlsConfig;
        this.corsProperties = corsProperties;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        return http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))  // CORS 설정 적용
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

    // CORS 설정
    @Bean
    public UrlBasedCorsConfigurationSource corsConfigurationSource() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();

        // CORS 속성들 설정
        config.setAllowedOriginPatterns(corsProperties.getAllowedOrigins());  // 허용된 출처
        config.setAllowedMethods(corsProperties.getAllowedMethods());  // 허용된 HTTP 메서드
        config.setAllowedHeaders(corsProperties.getAllowedHeaders());  // 허용된 헤더
        config.setAllowCredentials(corsProperties.isAllowCredentials());  // 자격 증명 허용

        // 모든 경로에 대해 CORS 설정 적용
        source.registerCorsConfiguration("/**", config);
        return source;
    }
}
