package org.example.thuan_security.config.security;

import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.KeyUse;
import com.nimbusds.jose.jwk.RSAKey;
import io.jsonwebtoken.*;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.example.thuan_security.model.Users;
import org.example.thuan_security.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.encrypt.KeyStoreKeyFactory;
import org.springframework.stereotype.Service;

import java.security.KeyPair;

import java.security.interfaces.RSAPublicKey;
import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;
import java.util.UUID;

@Service
@Slf4j
public class JwtTokenProvider {
    private final UserRepository userRepository;
    @Value("${keystore.file}")
    private String keyStore;
    @Value("${keystore.password}")
    private String password;
    @Value("${keystore.alias}")
    private String alias;

    private KeyPair keyPair;

    @Value("${jwt.expiration.time}")
    private int EXPIRATION_TIME;

    public JwtTokenProvider(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @PostConstruct
    public void JwtTokenProvider() {
        try {
            keyPair = keyPair(keyStore, password, alias);
        } catch (Exception e) {
            log.error("Error initializing KeyPair: {}", e.getMessage());
            throw new IllegalStateException("Unable to initialize KeyPair", e);
        }
    }

    private KeyPair keyPair(String keyStore, String keyStorePassword, String alias) {
        try {
            ClassPathResource resource = new ClassPathResource(keyStore);
            if (!resource.exists()) {
                log.error("Keystore file [{}] does not exist in the classpath", keyStore);
                throw new IllegalStateException("Keystore file cannot be found");
            }

            KeyStoreKeyFactory keyStoreKeyFactory = new KeyStoreKeyFactory(resource, keyStorePassword.toCharArray());
            return keyStoreKeyFactory.getKeyPair(alias);
        } catch (IllegalStateException e) {
            log.error("Cannot load keys from store: {}", e.getMessage(), e);
            throw e;
        }
    }

    public JWKSet jwkSet() {
        RSAKey.Builder builder = new RSAKey.Builder((RSAPublicKey) this.keyPair.getPublic()).keyUse(KeyUse.SIGNATURE).algorithm(JWSAlgorithm.RS256).keyID(UUID.randomUUID().toString());
        return new JWKSet(builder.build());
    }


    public String createToken(Authentication authentication, String email) {
        long now = Instant.now().toEpochMilli();
        Date validity = new Date(now + EXPIRATION_TIME);
        Users users= userRepository.findByEmail(email);
        String id = String.valueOf(users.getId());
        List<String> roles = authentication.getAuthorities().stream().map(
                item -> item.getAuthority()).toList();
        String role = roles.get(0);
        log.info(role);
        return Jwts.builder()
                .setSubject(authentication.getName())
                .claim("user_id", id)
                .claim("email", email)
                .claim("role", role)
                .signWith(SignatureAlgorithm.RS256, keyPair.getPrivate())
                .setIssuedAt(new Date(now))
                .setExpiration(validity)
                .compact();
    }

    public boolean validateToken(String authToken) {
        try {
            // Parse the JWT token and get the claims
            Claims claims = Jwts.parser()
                    .setSigningKey(keyPair.getPublic()) // Use the public key to validate the JWT
                    .parseClaimsJws(authToken)
                    .getBody();

            // Log claims for debugging purposes
            log.info("Claims: {}", claims);

            // Check if it is a Keycloak or internal token based on the claims
            if (claims.containsKey("preferred_username")) {
                log.info("This is a Keycloak token. preferred_username: {}", claims.get("preferred_username"));
            } else if (claims.containsKey("sub")) {
                log.info("This is an internal token. sub: {}", claims.get("sub"));
            } else {
                log.warn("Token does not contain expected claims: preferred_username or sub");
                return false; // Token is invalid if it doesn't contain expected claims
            }

            // If we reach here, the token is valid
            return true;
        } catch (MalformedJwtException e) {
            log.error("Invalid JWT token: {}", e.getMessage());
        } catch (ExpiredJwtException e) {
            log.error("JWT token is expired: {}", e.getMessage());
        } catch (UnsupportedJwtException e) {
            log.error("JWT token is unsupported: {}", e.getMessage());
        } catch (IllegalArgumentException e) {
            log.error("JWT claims string is empty: {}", e.getMessage());
        } catch (Exception e) {
            log.error("Error validating JWT token: {}", e.getMessage());
        }
        return false;
    }

    public String extractClaims(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(keyPair.getPublic())
                .parseClaimsJws(token)
                .getBody();
        return claims.getSubject();
    }


    public LocalDateTime extractExpiration(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(keyPair.getPublic())
                .parseClaimsJws(token)
                .getBody();
        return claims.getExpiration().toInstant().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime();
    }

    public String extractRole(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(keyPair.getPublic())
                .parseClaimsJws(token)
                .getBody();
        return claims.get("role", String.class);
    }
}