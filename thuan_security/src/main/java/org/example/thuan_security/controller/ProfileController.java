package org.example.thuan_security.controller;


import jakarta.validation.Valid;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.example.thuan_security.request.RegisterRequest;
import org.example.thuan_security.response.UserKCLResponse;
import org.example.thuan_security.service.keycloak.UserKCLService;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@RequestMapping("/api/keycloak")
public class ProfileController {
    UserKCLService profileService;

    @PostMapping("/register")
    org.example.thuan_security.response.ApiResponse1<UserKCLResponse> register(@RequestBody @Valid RegisterRequest request) {
        return org.example.thuan_security.response.ApiResponse1.<UserKCLResponse>builder()
                .result(profileService.register(request))
                .build();
    }

//    @GetMapping("/profiles")
//    ApiResponse<List<UserKCLResponse>> getAllProfiles() {
//        return ApiResponse1.<List<UserKCLResponse>>builder()
//                .result(profileService.getAllProfiles())
//                .build();
//    }
}
