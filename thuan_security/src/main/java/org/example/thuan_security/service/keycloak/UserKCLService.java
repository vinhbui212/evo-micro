package org.example.thuan_security.service.keycloak;

import org.example.thuan_security.request.RegisterRequest;
import org.example.thuan_security.request.ResetPasswordRequest;
import org.example.thuan_security.response.UserKCLResponse;

public interface UserKCLService {
    UserKCLResponse register(RegisterRequest request);
    String updateEnabled(String id,RegisterRequest request);
    String resetPassword( String userId,  ResetPasswordRequest request);

}
