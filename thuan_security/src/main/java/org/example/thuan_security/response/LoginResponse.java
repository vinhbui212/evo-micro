package org.example.thuan_security.response;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class LoginResponse {
    private String code;
    private String message;
    private String token;
    private String refreshToken;
    private LocalDateTime expireAt;
}
