package org.example.thuan_security.service.keycloak;


import feign.QueryMap;
import org.example.thuan_security.request.*;
import org.example.thuan_security.response.TokenExchangeResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@FeignClient(name = "identity-client", url = "${idp.url}")
public interface IdentityClient {
    @PostMapping(value = "${idp.token-endpoint}",
            consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    TokenExchangeResponse exchangeToken(@QueryMap TokenExchangeParam param);

    @PostMapping(value = "${idp.register-endpoint}",
            consumes = MediaType.APPLICATION_JSON_VALUE)
    ResponseEntity<?> createUser(
            @RequestHeader("authorization") String token,
            @RequestBody UserCreationParam param);

    @PostMapping(value = "${idp.logout-endpoint}",
                    consumes = MediaType.APPLICATION_FORM_URLENCODED_VALUE)
    String logout(@RequestHeader("authorization") String token, @QueryMap LogoutRequest request);

    @PutMapping(value= "${idp.update-endpoint}")
    String updateUser(@RequestHeader("authorization") String token,@PathVariable String userId, @RequestBody RegisterRequest request);

    @PutMapping(value= "${idp.resetpass-endpoint}")
    String resetPassword(@RequestHeader("authorization") String token,@PathVariable String userId, @RequestBody ResetPasswordRequest request);
}

