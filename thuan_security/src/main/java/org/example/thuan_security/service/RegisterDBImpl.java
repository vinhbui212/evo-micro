package org.example.thuan_security.service;

import org.example.thuan_security.service.factory.RegisterStrategy;
import org.example.thuan_security.request.RegisterRequest;
import org.example.thuan_security.response.UserKCLResponse;
import org.example.thuan_security.service.user.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class RegisterDBImpl implements RegisterStrategy {
    @Autowired
    UserService userService;
    @Override
    public UserKCLResponse register(RegisterRequest registerRequest) {
        return userService.register(registerRequest);
    }
}
