package org.example.thuan_security.service;

import org.example.thuan_security.service.factory.RegisterStrategy;
import org.example.thuan_security.request.RegisterRequest;
import org.example.thuan_security.response.UserKCLResponse;
import org.example.thuan_security.service.keycloak.UserKCLService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegisterKLImpl implements RegisterStrategy {
    @Autowired
    UserKCLService userKCLService;
    @Override
    public UserKCLResponse register(RegisterRequest registerRequest) {
        return userKCLService.register(registerRequest);
    }
}
