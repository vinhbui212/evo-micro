package org.example.thuan_security.service.factory;

import org.example.thuan_security.request.ResetPasswordRequest;

public interface ResetPasswordStrategy {
    String resetPassword( String userId,  ResetPasswordRequest request);

}
