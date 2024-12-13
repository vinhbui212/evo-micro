package org.example.thuan_security.service.factory.impl;

import lombok.RequiredArgsConstructor;
import org.example.thuan_security.model.LoginType;
import org.example.thuan_security.service.RegisterDBImpl;
import org.example.thuan_security.service.RegisterKLImpl;
import org.example.thuan_security.service.factory.RegisterStrategy;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RegisterFactory {
    private final RegisterKLImpl registerKL;
    private final RegisterDBImpl registerDB;

    @Value("${keycloak.enabled}")
    private boolean isKeycloakEnabled;

    public RegisterStrategy getLoginStrategy() {
        LoginType loginType = isKeycloakEnabled ? LoginType.KEYCLOAK : LoginType.DB;

        switch (loginType) {
            case KEYCLOAK:
                return registerKL;
            case DB:
            default:
                return registerDB;
        }
    }
}
