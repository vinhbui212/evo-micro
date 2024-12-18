package org.example.thuan_security.service;

import lombok.RequiredArgsConstructor;
import org.example.thuan_security.config.security.JwtTokenProvider;
import org.example.thuan_security.model.Blacklist;
import org.example.thuan_security.repository.BlacklistRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class BlackListService {
    private final BlacklistRepository blacklistTokenRepository;
    private final JwtTokenProvider jwtTokenProvider;
    public void addTokenToBlacklist(String token) throws Exception {
        if(isTokenBlacklisted(token)){
            throw new Exception("Logged out");
        }
        Blacklist blacklistToken = new Blacklist();
        blacklistToken.setToken(token);
        LocalDateTime expirationTime=jwtTokenProvider.extractExpiration(token);
        blacklistToken.setExpirationTime(expirationTime);

        blacklistTokenRepository.save(blacklistToken);
    }
    public boolean isTokenBlacklisted(String token) {
        Optional<Blacklist> blacklistToken = blacklistTokenRepository.findByToken(token);
        return blacklistToken.isPresent();
    }
    public void removeExpiredTokens() {
        blacklistTokenRepository.deleteByExpirationTimeBefore(LocalDateTime.now());
    }
}