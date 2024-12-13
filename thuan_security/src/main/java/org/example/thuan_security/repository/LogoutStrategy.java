package org.example.thuan_security.repository;

import org.example.thuan_security.request.LogoutRequest;

public interface LogoutStrategy {
    String logout(LogoutRequest request);
}
