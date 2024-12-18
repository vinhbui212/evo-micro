package org.example.thuan_security.controller;

import com.evo.common.UserAuthority;
import com.evo.common.dto.response.Response;
import com.evo.common.webapp.config.RedisService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.thuan_security.config.security.JwtTokenProvider;
import org.example.thuan_security.model.Users;
import org.example.thuan_security.request.ResetPasswordRequest;
import org.example.thuan_security.service.factory.RegisterStrategy;
import org.example.thuan_security.repository.UserRepository;
import org.example.thuan_security.request.LoginRequest;
import org.example.thuan_security.request.RefreshTokenRequest;
import org.example.thuan_security.request.RegisterRequest;
import org.example.thuan_security.response.*;
import org.example.thuan_security.service.*;
import org.example.thuan_security.service.factory.ResetPasswordStrategy;
import org.example.thuan_security.service.factory.impl.RegisterFactory;
import org.example.thuan_security.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import static org.example.thuan_security.model.LogEnum.*;

@Slf4j
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private BlackListService blackList;
    @Autowired
    private UserActivityLogService logService;
    @Autowired
    private JwtTokenProvider jwtTokenProvider;
    @Autowired
    private RefreshTokenService refreshTokenService;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ResetPasswordStrategy resetPasswordStrategy;
    @Autowired
    private  RegisterFactory registerFactory;
    @Autowired
    private BlackListService blackListService;
    @Autowired
    private AuthorityServiceImpl authorityService;
    @Autowired
    private RedisService redisService;
    @PostMapping("/register")
    public UserKCLResponse register(@RequestBody RegisterRequest registerRequest) throws Exception {
        RegisterStrategy loginStrategy = registerFactory.getLoginStrategy();
        log.info(loginStrategy.toString());
        return loginStrategy.register(registerRequest);
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest loginRequest) {
        LoginResponse response = userService.login(loginRequest);

        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @PostMapping("/validate-otp")
    public ResponseEntity<LoginResponse> validateOtp(@RequestParam String email, @RequestParam String otp, HttpServletRequest request) throws Exception {
        LoginResponse response = userService.validateLoginWithOtp(email, otp);
        if (response != null && String.valueOf(HttpStatus.OK.value()).equals(response.getCode())) {
            String ipAddress = convertTov4(request.getRemoteAddr());
            LocalDateTime localDateTime = LocalDateTime.now();
            logService.logActivity(email, String.valueOf(LOGIN), ipAddress, localDateTime);
            return ResponseEntity.ok(response);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
        }
    }

    @GetMapping("/verifiedAccount")
    public String verifiedAccount(@RequestParam String email) {
        userService.isVerifiedAccount(email);
        return "verified";
    }


    @PostMapping("/sendMailForgotPassword")
    public ResponseEntity<ApiResponse> forgotPassword(@RequestParam String email) {
        ApiResponse response = userService.sendMailForgotPassword(email);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @PostMapping("/changeForgotPassword")
    public ResponseEntity<ApiResponse> changeForgotPassword(@RequestParam String email, @RequestParam String token, @RequestParam String newPassword, HttpServletRequest request) throws Exception {
        ApiResponse response = userService.changeForgotPassword(email, token, newPassword);
        String ipAddress = convertTov4(request.getRemoteAddr());
        LocalDateTime localDateTime = LocalDateTime.now();
        logService.logActivity(email, String.valueOf(RESET_PASSWORD), ipAddress, localDateTime);
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<ApiResponse> logout(@RequestParam String token, HttpServletRequest request) throws Exception {
        String email = jwtTokenProvider.extractClaims(token);
        redisService.save(token);
        refreshTokenService.deleteRefreshToken(email);
        String ipAddress = convertTov4(request.getRemoteAddr());
        LocalDateTime localDateTime = LocalDateTime.now();
        logService.logActivity(email, String.valueOf(LOG_OUT), ipAddress, localDateTime);
        return ResponseEntity.ok(new ApiResponse<>(200, "Logged out successfully", 1, null));
    }

    public static String convertTov4(String ipAddress) {
        if ("0:0:0:0:0:0:0:1".equals(ipAddress)) {
            ipAddress = "127.0.0.1";
            return ipAddress;
        }
        return ipAddress;
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<?> refreshToken(@RequestBody RefreshTokenRequest request,HttpServletRequest httpServletRequest) throws Exception {
        String refreshToken = request.getRefreshToken();
        String token=getTokenFromRequest(httpServletRequest);
        if (!refreshTokenService.isRefreshTokenValid(refreshToken)) {
            return ResponseEntity.status(401).body("Invalid or expired refresh token");
        }

        String email = refreshTokenService.getEmailFromRefreshToken(refreshToken);
        log.info(email);
        Users user = userRepository.findByEmail(email);

        if (user != null) {
            List<SimpleGrantedAuthority> authorities = user.getRoles().stream().map(SimpleGrantedAuthority::new).toList();
            String newAccessToken = jwtTokenProvider.createToken(new UsernamePasswordAuthenticationToken(email, null, authorities), email);
            blackListService.addTokenToBlacklist(token);
            return ResponseEntity.ok(new TokenResponse(newAccessToken));
        }
        return ResponseEntity.status(401).body("Invalid or expired refresh token");

    }
    @PostMapping("/{id}/reset-password")
    public String resetPassword(@PathVariable String id,@RequestBody ResetPasswordRequest resetPasswordRequest)  {
        return resetPasswordStrategy.resetPassword(id,resetPasswordRequest);
    }

    @GetMapping("/blacklist")
    public boolean isTokenBlacklisted(@RequestParam String token){
        return blackListService.isTokenBlacklisted(token);
    }

    public String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }
    public String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication != null && authentication.getPrincipal() instanceof Jwt) {
            Jwt jwt = (Jwt) authentication.getPrincipal();

            String username = jwt.getClaimAsString("sub");
            return username;
        }

        return null;
    }
    @GetMapping("/iam/client-token/{clientId}/{clientSecret}")
    public ResponseEntity<?> getClientToken(@PathVariable String clientId, @PathVariable String clientSecret) throws Exception {
        return ResponseEntity.ok(userService.getClientToken(DefaultClientTokenResponse.builder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .build()));
    }
   

    @GetMapping("/api/users/{username}/authorities-by-username")
    Response<UserAuthority> getUserAuthority(@PathVariable String username) {
        return Response.success("Get authorities successful for " + username, authorityService.getUserAuthority(username));
    }
}

