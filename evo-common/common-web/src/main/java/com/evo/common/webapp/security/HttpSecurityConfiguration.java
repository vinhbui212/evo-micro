package com.evo.common.webapp.security;

import com.evo.common.webapp.config.ActionLogFilter;
import com.evo.common.webapp.config.JwtProperties;
import jakarta.servlet.http.HttpServletRequest;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.Bean;
import org.springframework.security.authentication.AuthenticationManagerResolver;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.server.resource.web.authentication.BearerTokenAuthenticationFilter;
import org.springframework.security.web.SecurityFilterChain;

@Slf4j
@EnableWebSecurity
@EnableFeignClients(basePackages = {"com.evo.common.client"})
@EnableMethodSecurity(securedEnabled = true)
public class HttpSecurityConfiguration {

    private final ActionLogFilter actionLogFilter;
    private final CustomAuthenticationFilter customAuthenticationFilter;
    private final ForbiddenTokenFilter forbiddenTokenFilter;
    private final JwtProperties jwtProperties;

    public HttpSecurityConfiguration(ActionLogFilter actionLogFilter,
                                     CustomAuthenticationFilter customAuthenticationFilter,
                                     ForbiddenTokenFilter forbiddenTokenFilter, JwtProperties jwtProperties) {
        this.actionLogFilter = actionLogFilter;
        this.customAuthenticationFilter = customAuthenticationFilter;
        this.forbiddenTokenFilter = forbiddenTokenFilter;
        this.jwtProperties = jwtProperties;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http.csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(sessionManagement -> sessionManagement.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorizeHttpRequests ->
                        authorizeHttpRequests
                                .requestMatchers("/").permitAll()
                                .requestMatchers("/health").permitAll()
                                .requestMatchers("/api/certificate/.well-known/jwks.json").permitAll()
                                .requestMatchers("/api/public/**").permitAll()
                                .requestMatchers("/api/authenticate/**").permitAll()
                                .requestMatchers("/api/**").authenticated()
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .authenticationManagerResolver(this.jwkResolver(this.jwtProperties)));
        http.addFilterAfter(this.forbiddenTokenFilter, BearerTokenAuthenticationFilter.class);
        http.addFilterAfter(this.customAuthenticationFilter, BearerTokenAuthenticationFilter.class);
        http.addFilterAfter(this.actionLogFilter, BearerTokenAuthenticationFilter.class);
        // @formatter:on
        return http.build();
    }


    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    public AuthenticationManagerResolver<HttpServletRequest> jwkResolver(JwtProperties jwtProperties) {
        return new JwkAuthenticationManagerResolver(jwtProperties);
    }

}
