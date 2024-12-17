package org.example.thuan_security.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Builder
@Data
@AllArgsConstructor
@NoArgsConstructor
public class DefaultClientTokenResponse {
    private String clientId;
    private String clientSecret;
}
