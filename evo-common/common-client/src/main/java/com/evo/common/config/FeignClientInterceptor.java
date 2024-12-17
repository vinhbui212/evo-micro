package com.evo.common.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

@Slf4j
public class FeignClientInterceptor implements RequestInterceptor {
    @Value("${spring.application.client-id}")
    private String client_id;
    @Value("${spring.application.client-secret}")
    private String client_secret;

    @Override
    public void apply(RequestTemplate requestTemplate) {
        String token = getClientToken();
        if (token != null && !token.isEmpty()) {
            requestTemplate.header("Authorization", "Bearer " + token);
        }
    }
    private String getClientToken() {

        String clientId = client_id;
        String clientSecret = client_secret;
        String tokenUrl = "http://localhost:8081/api/auth/iam/client-token/{clientId}/{clientSecret}";
        RestTemplate restTemplate = new RestTemplate();
        try {
            // Call the auth service to get the token
            ResponseEntity<String> response = restTemplate.getForEntity(
                    tokenUrl,
                    String.class,
                    clientId,
                    clientSecret
            );
            if (response.getStatusCode().is2xxSuccessful()) {
                return response.getBody(); // Assuming the token is the plain response body
            } else {
                throw new RuntimeException("Failed to retrieve token. Status: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error while fetching client token: " + e.getMessage(), e);
        }
    }
}
