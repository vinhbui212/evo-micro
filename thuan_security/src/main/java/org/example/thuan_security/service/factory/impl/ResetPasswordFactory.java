package org.example.thuan_security.service.factory.impl;

import org.example.thuan_security.model.LoginType;
import org.example.thuan_security.request.RegisterRequest;
import org.example.thuan_security.request.ResetPasswordRequest;
import org.example.thuan_security.service.factory.ResetPasswordStrategy;
import org.example.thuan_security.service.keycloak.UserKCLService;
import org.example.thuan_security.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class ResetPasswordFactory implements ResetPasswordStrategy {
    @Autowired
    UserKCLService userKCLService;
    @Autowired
    UserService userService;
    @Value("${keycloak.enabled}")
    private boolean isKeycloakEnabled;

    @Override
    public String resetPassword( String userId,  ResetPasswordRequest request){
        LoginType loginType = isKeycloakEnabled ? LoginType.KEYCLOAK : LoginType.DB;

        switch (loginType) {
            case KEYCLOAK:
                userKCLService.resetPassword(userId, request);
                return "Reset Pass KL";
            case DB:
            default:
                userService.resetPassword(userId, request);
                return "Reset Pass DB";

        }
    }
}
