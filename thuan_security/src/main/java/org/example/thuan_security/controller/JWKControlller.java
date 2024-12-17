package org.example.thuan_security.controller;

import lombok.RequiredArgsConstructor;
import org.example.thuan_security.config.security.JwtTokenProvider;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
@RestController
@RequiredArgsConstructor
@RequestMapping("")
public class JWKControlller {
    private final JwtTokenProvider jwtTokenProvider;
    @GetMapping("/api/certificate/.well-known/jwks.json")
    Map<String, Object> keys() {
        return this.jwtTokenProvider.jwkSet().toJSONObject();
    }
}
