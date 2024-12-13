package org.example.thuan_security.service.user;

import org.example.thuan_security.request.*;
import org.example.thuan_security.response.*;
import org.springframework.data.domain.Page;

import java.util.List;

public interface UserService {
    LoginResponse login(LoginRequest loginRequest);

    LoginResponse validateLoginWithOtp(String email, String otp);

    UserKCLResponse register(RegisterRequest registerRequest);

    UserResponse getUserInfo(String token);

    ApiResponse updateUserInfo(String token, UserResponse userResponse);

    boolean isVerifiedAccount(String email);

    ApiResponse changePassword(ChangePasswordRequest changePasswordRequest,String email);

    ApiResponse sendMailForgotPassword(String email);

    ApiResponse changeForgotPassword(String email, String otp, String newPassword);

    String createUser(RegisterRequest registerRequest);

     String updateEnabled(String id, RegisterRequest request);

    String resetPassword( String userId,  ResetPasswordRequest request);


    Page<UserResponse> getAllUsers(SearchRequest searchRequest);

    String deleteUser(Long userId);

//    Page<UserResponse> searchUsers(SearchRequest searchRequest);

    Page<UserResponse> searchUsers(UserSearchRequest request);

}
