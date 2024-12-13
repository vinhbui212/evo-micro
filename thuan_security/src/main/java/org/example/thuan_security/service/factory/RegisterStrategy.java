package org.example.thuan_security.service.factory;

import org.example.thuan_security.request.RegisterRequest;
import org.example.thuan_security.response.UserKCLResponse;

public interface RegisterStrategy {
    UserKCLResponse register(RegisterRequest registerRequest);
}
