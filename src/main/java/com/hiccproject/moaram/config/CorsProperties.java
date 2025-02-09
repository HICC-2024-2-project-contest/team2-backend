package com.hiccproject.moaram.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Getter
@Configuration
@ConfigurationProperties(prefix = "cors")
public class CorsProperties {
    private List<String> allowedOrigins;
    private List<String> allowedMethods;
    private List<String> allowedHeaders;
    @Setter
    private boolean allowCredentials;

    public void setAllowedOrigins(String allowedOrigins) {
        this.allowedOrigins = List.of(allowedOrigins.split(","));
    }

    public void setAllowedMethods(String allowedMethods) {
        this.allowedMethods = List.of(allowedMethods.split(","));
    }

    public void setAllowedHeaders(String allowedHeaders) {
        this.allowedHeaders = List.of(allowedHeaders.split(","));
    }

}
