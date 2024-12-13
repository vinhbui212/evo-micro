//package org.example.thuan_security.config;
//
//import com.nimbusds.jwt.JWTParser;
//import jakarta.servlet.http.HttpServletRequest;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.AuthenticationManagerResolver;
//import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
//import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationProvider;
//import org.springframework.security.oauth2.server.resource.web.BearerTokenResolver;
//import org.springframework.security.oauth2.server.resource.web.DefaultBearerTokenResolver;
//import org.springframework.stereotype.Service;
//import org.springframework.util.StringUtils;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.Optional;
//
//@Slf4j
//@Service
//public class JwkAuthenticationManagerResolver implements
//        AuthenticationManagerResolver<HttpServletRequest> {
//
//    private final Map<String, String> issuers;
//    private final Map<String, AuthenticationManager> authenticationManagers = new HashMap<>();
//
//    private final BearerTokenResolver resolver = new DefaultBearerTokenResolver();
//
//    public JwkAuthenticationManagerResolver(org.example.thuan_security.config.JwtProperties jwtProperties) {
//        this.issuers = jwtProperties.getJwkSetUris();
//    }
//
//    @Override
//    public AuthenticationManager resolve(HttpServletRequest request) {
//        String issuerId = toIssuerId(request);
//        return this.authenticationManagers.computeIfAbsent(issuerId, this::fromIssuer);
//    }
//
//    private String toIssuerId(HttpServletRequest request) {
//        String token = this.resolver.resolve(request);
//        try {
//            if (StringUtils.hasText((String) JWTParser.parse(token).getJWTClaimsSet().getClaim("user_id"))) {
//
//                return "internal";
//            } else if (StringUtils.hasText((String) JWTParser.parse(token).getJWTClaimsSet().getClaim("preferred_username"))) {
//                return "sso";
//            } else {
//                throw new RuntimeException("INVALID_INPUT: Unable to determine issuer based on token claims.");
//            }
//        } catch (Exception e) {
//            throw new IllegalArgumentException("Error parsing JWT token.", e);
//        }
//    }
//
//    private AuthenticationManager fromIssuer(String issuerId) {
//        return Optional.ofNullable(this.issuers.get(issuerId))
//                .map(issuer -> NimbusJwtDecoder.withJwkSetUri(issuer).build()) // Sử dụng JWKS URL tương ứng
//                .map(JwtAuthenticationProvider::new) // Sử dụng JwtAuthenticationProvider để xác thực
//                .orElseThrow(() -> new IllegalArgumentException("Unknown issuer: " + issuerId))::authenticate;
//    }
//
//}
