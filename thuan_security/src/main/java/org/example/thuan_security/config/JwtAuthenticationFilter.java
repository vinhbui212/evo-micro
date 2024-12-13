//package org.example.thuan_security.config;
//
//import jakarta.servlet.FilterChain;
//import jakarta.servlet.ServletException;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import lombok.RequiredArgsConstructor;
//import lombok.extern.slf4j.Slf4j;
//import org.example.thuan_security.config.security.JwtTokenProvider;
//import org.example.thuan_security.config.security.UserDetailService;
//import org.example.thuan_security.model.Users;
//import org.example.thuan_security.repository.UserRepository;
//import org.example.thuan_security.service.BlackListService;
//import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
//import org.springframework.security.core.Authentication;
//import org.springframework.security.core.context.SecurityContextHolder;
//import org.springframework.security.core.userdetails.UserDetails;
//import org.springframework.security.oauth2.jwt.Jwt;
//import org.springframework.stereotype.Component;
//import org.springframework.util.StringUtils;
//import org.springframework.web.filter.OncePerRequestFilter;
//
//import java.io.IOException;
//import java.util.List;
//import java.util.Set;
//
//@Component
//@RequiredArgsConstructor
//@Slf4j
//public class JwtAuthenticationFilter extends OncePerRequestFilter {
//    private final JwtTokenProvider jwtTokenProvider;
//    private final UserDetailService userDetailService;
//    private final UserRepository userRepository;
//    private final BlackListService blackList;
//    @Override
//    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
//        String token = getTokenFromRequest(request);
//        log.info("Token: {}", token);
//
//        try {
//            if (StringUtils.hasText(token) && jwtTokenProvider.validateToken(token) ) {
//                if (blackList.isTokenBlacklisted(token)) {
//                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
//                    response.getWriter().write("Token is blacklisted");
//                    return;
//                }
//                String email = jwtTokenProvider.extractClaims(token);
//                log.info("Extracted email: {}", email);
//
//                UserDetails userDetails = userDetailService.loadUserByUsername(email);
//                log.info("User Details: {}", userDetails);
//
//                if (userDetails != null) {
//                    Users users = userRepository.findByEmail(email);
//                    Set<String> roles =users.getRoles(); // Tên claim tùy thuộc vào cách bạn đặt trong JWT
//                    if (roles != null && !roles.isEmpty()) {
//                        String role = roles.iterator().next(); // Lấy phần tử đầu tiên trong Set
//                        log.info("Role: {}", role); // In ra role, ví dụ: "ROLE_ADMIN"
//                    }
//
//                    UsernamePasswordAuthenticationToken authenticationToken =
//                            new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
//                    SecurityContextHolder.getContext().setAuthentication(authenticationToken);
//                }
//            }
//        } catch (Exception e) {
//            log.error("Could not set user authentication in security context", e);
//        }
//
//        filterChain.doFilter(request, response);
//    }
//
//    public String getTokenFromRequest(HttpServletRequest request) {
//        String bearerToken = request.getHeader("Authorization");
//        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
//            return bearerToken.substring(7);
//        }
//        return null;
//    }
//    public String getCurrentUsername() {
//        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
//
//        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
//            Jwt jwt = (Jwt) authentication.getPrincipal();
//
//            String username = jwt.getClaimAsString("sub");
//            return username;
//        }
//
//        return null;
//    }
//}
