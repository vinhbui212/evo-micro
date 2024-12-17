//package org.example.thuan_security.config;
//
//import jakarta.servlet.http.HttpServletRequest;
//import lombok.RequiredArgsConstructor;
//import org.example.thuan_security.config.security.JwtAuthenticationEntryPoint;
//import org.example.thuan_security.config.security.JwtTokenProvider;
//import org.example.thuan_security.config.security.UserDetailService;
//import org.springframework.beans.factory.annotation.Value;
//import org.springframework.context.annotation.Bean;
//import org.springframework.context.annotation.Configuration;
//import org.springframework.security.authentication.AuthenticationManager;
//import org.springframework.security.authentication.AuthenticationManagerResolver;
//import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
//import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
//import org.springframework.security.config.annotation.web.builders.HttpSecurity;
//import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
//import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
//import org.springframework.security.crypto.password.PasswordEncoder;
//import org.springframework.security.oauth2.jwt.JwtDecoder;
//import org.springframework.security.oauth2.jwt.JwtDecoders;
//import org.springframework.security.oauth2.jwt.JwtValidators;
//import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
//import org.springframework.security.oauth2.server.resource.web.BearerTokenAuthenticationFilter;
//import org.springframework.security.web.SecurityFilterChain;
//import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
//
//@Configuration
//@EnableWebSecurity
//@RequiredArgsConstructor
//@EnableGlobalMethodSecurity(prePostEnabled = true)
//public class SecurityConfig {
//
//    private final JwtTokenProvider jwtTokenProvider;
//    private final UserDetailService userDetailService;
//    private final JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;
//    private final JwtAuthenticationFilter jwtAuthenticationFilter;
//    private final CustomPermissionEvaluator customPermissionEvaluator;
//
//    private final JwtProperties jwtProperties;
//
//
//
//    @Bean
//    public static PasswordEncoder passwordEncoder() {
//        return new BCryptPasswordEncoder();
//    }
//
//
//
//    private final String[] SWAGGER_ENDPOINT = {"/swagger-ui.html", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui/index.html", "/api/certificate/**","/api/excel/**"};
//
//    @Bean
//    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
//        httpSecurity
//                .csrf(csrf -> csrf.disable())
//                .authorizeRequests(authorizeRequests -> {
//                    authorizeRequests.requestMatchers("api/auth/**").permitAll();
//                    authorizeRequests.requestMatchers(SWAGGER_ENDPOINT).permitAll();
//                    authorizeRequests.anyRequest().authenticated();
//                })
//                .exceptionHandling(exception -> exception.authenticationEntryPoint(jwtAuthenticationEntryPoint))
//                .oauth2ResourceServer(oauth2 -> oauth2
//                        .authenticationManagerResolver(jwkResolver(jwtProperties))
//                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
//                )
//                .addFilterAfter(jwtAuthenticationFilter, BearerTokenAuthenticationFilter.class);
//
//        return httpSecurity.build();
//    }
//
//    public AuthenticationManagerResolver<HttpServletRequest> jwkResolver(JwtProperties jwtProperties) {
//        return new JwkAuthenticationManagerResolver(jwtProperties);
//    }
//
//    @Bean
//    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
//        return configuration.getAuthenticationManager();
//    }
//
//}
