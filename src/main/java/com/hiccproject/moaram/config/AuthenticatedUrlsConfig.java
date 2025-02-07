package com.hiccproject.moaram.config;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.List;

@Setter
@Getter
@Component
@ConfigurationProperties(prefix = "app")
public class AuthenticatedUrlsConfig {

    private List<String> authenticatedUrls;

}
