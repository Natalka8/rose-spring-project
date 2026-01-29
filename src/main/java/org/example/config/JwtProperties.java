package org.example.config;
import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private String secret = "test-jwt-secret-1234567890-for-development-only";
    private long expiration = 86400000; // 24 hours
    private long refreshExpiration = 604800000; // 7 days
    private String issuer = "rose-game";
}