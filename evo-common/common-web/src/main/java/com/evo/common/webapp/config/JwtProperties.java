package com.evo.common.webapp.config;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.boot.autoconfigure.security.oauth2.resource.OAuth2ResourceServerProperties;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

import java.util.Map;

@EqualsAndHashCode(callSuper = true)
@Data
@Component
@ConfigurationProperties(
        prefix = "spring.security.oauth2.resourceserver.jwt"
)
public class JwtProperties extends OAuth2ResourceServerProperties.Jwt {
    private Map<String, String> jwkSetUris;
}
