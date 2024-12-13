package org.example.thuan_security.service;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.UUID;

@Slf4j
@Service
public class RefreshTokenService {

    private static final Cache<String, String> refreshTokenCache = CacheBuilder.newBuilder()
            .expireAfterWrite(7, TimeUnit.DAYS)
            .build();

    public String createRefreshToken(String email) {
        String refreshToken = UUID.randomUUID().toString();
        refreshTokenCache.put(refreshToken, email);
        refreshTokenCache.asMap().forEach((key, value) -> {
            System.out.println("Key: " + key + ", Value: " + value);
        });
        return refreshToken;
    }


    public boolean isRefreshTokenValid(String refreshToken) {
        return refreshTokenCache.getIfPresent(refreshToken) != null;
    }


    public String getEmailFromRefreshToken(String refreshToken) {
        return refreshTokenCache.getIfPresent(refreshToken);
    }


    public void deleteRefreshToken(String email) {
        refreshTokenCache.asMap().entrySet().stream()
                .filter(entry -> entry.getValue().equals(email))
                .map(Map.Entry::getKey)
                .findFirst()
                .ifPresent(refreshTokenCache::invalidate);
    }



}
