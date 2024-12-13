package com.evo.common.webapp.security;

public interface TokenCacheService {

    String INVALID_REFRESH_TOKEN_CACHE = "invalid-refresh-token";
    String INVALID_TOKEN_CACHE = "invalid-access-token";

    void invalidToken(String token);

    void invalidRefreshToken(String refreshToken);

    boolean isExisted(String cacheName, String token);

    default boolean isInvalidToken(String token) {
        return isExisted(INVALID_TOKEN_CACHE, token);
    }

    default boolean isInvalidRefreshToken(String refreshToken) {
        return isExisted(INVALID_REFRESH_TOKEN_CACHE, refreshToken);
    }

}
