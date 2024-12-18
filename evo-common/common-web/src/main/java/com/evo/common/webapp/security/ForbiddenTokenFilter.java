package com.evo.common.webapp.security;

import com.evo.common.webapp.config.RedisService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
@Slf4j
@RequiredArgsConstructor
public class ForbiddenTokenFilter extends OncePerRequestFilter {

    private final TokenCacheService tokenCacheService;
    private final RedisService redisService;
    private final List<String> excludedPaths = List.of("/login", "/register", "/public/**", "/css/**", "/js/**", "/images/**");



    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        log.info("Kiểm tra ForbiddenTokenFilter cho request: {}", request.getRequestURI());

        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        if (authentication instanceof JwtAuthenticationToken jwtAuthenticationToken) {
            String token = jwtAuthenticationToken.getToken().getTokenValue();

            try {
                boolean isBlacklisted = redisService.isEntryExist( token);

                if (isBlacklisted) {
                    log.warn("Token đã bị blacklist: {}", token);
                    response.setStatus(HttpServletResponse.SC_FORBIDDEN); // Trả về 403 Forbidden
                    response.getWriter().write("Token đã bị blacklist");
                    return; // Dừng lại và không chuyển tiếp request
                }
            } catch (Exception e) {
                log.error("Lỗi trong khi kiểm tra token blacklist: {}", e.getMessage());
                response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR); // Trả về 500 Internal Server Error
                return; // Dừng lại và không chuyển tiếp request
            }
        }

        filterChain.doFilter(request, response);
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        String requestUri = request.getRequestURI();

        // Bỏ qua các URL không cần kiểm tra
        for (String excludedPath : excludedPaths) {
            if (requestUri.startsWith(excludedPath)) {
                log.info("Bỏ qua ForbiddenTokenFilter cho URL: {}", requestUri);
                return true;
            }
        }

        SecurityContext securityContext = SecurityContextHolder.getContext();
        Authentication authentication = securityContext.getAuthentication();

        if (authentication == null) {
            return true; // Không có Authentication thì không cần kiểm tra
        }

        if (authentication instanceof JwtAuthenticationToken) {
            return !authentication.isAuthenticated(); // Nếu chưa authenticated thì bỏ qua filter
        }

        return authentication instanceof AnonymousAuthenticationToken;
    }
}
