//package org.example.storage_service.config;
//
//import jakarta.servlet.http.HttpServletRequest;
//import lombok.RequiredArgsConstructor;
//import org.example.storage_service.config.JwkAuthenticationManagerResolver;
//import org.example.storage_service.config.JwtProperties;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.config.Customizer;
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.oauth2.jwt.JwtDecoder;
//import org.springframework.security.oauth2.jwt.JwtDecoders;
//import org.springframework.security.oauth2.jwt.JwtValidators;
//import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationFilter;
//import org.springframework.security.authentication.AuthenticationManagerResolver;
//
//@Configuration
//@EnableGlobalMethodSecurity(prePostEnabled = true)
//@RequiredArgsConstructor
//@EnableWebSecurity
//public class SecurityConfig {
//
//    private final JwtProperties jwtProperties;
//
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
//        http
//                .authorizeRequests(author-> {
//                author.requestMatchers("/api/public/**").permitAll();
//                author.anyRequest().authenticated();
//                })
//                .oauth2ResourceServer(oauth2 -> oauth2.authenticationManagerResolver(jwkResolver(jwtProperties)));
//
//
//
//        return http.build();
//    }
//
//    public AuthenticationManagerResolver<HttpServletRequest> jwkResolver(JwtProperties jwtProperties) {
//        return new JwkAuthenticationManagerResolver(jwtProperties);
//    }
//}
