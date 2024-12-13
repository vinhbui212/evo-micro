package org.example.thuan_security.service.factory;

import org.example.thuan_security.request.RegisterRequest;

public interface LockStrategy {
    String deleteStraegy(String id, RegisterRequest registerRequest);
}
