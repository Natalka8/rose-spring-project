package org.example.service;

import org.example.dto.*;

public interface AuthService {

    AuthResponse register(RegisterRequest request);

    AuthResponse login(LoginRequest request, String clientIp);

    AuthResponse refreshToken(String refreshToken);

    void logout(String accessToken);

    void logoutAll(String accessToken);

    void forgotPassword(ForgotPasswordRequest request);

    void resetPassword(ResetPasswordRequest request);

    void resetPassword(ResetPasswordDto resetPasswordDto);

    UserResponse getCurrentUser(String accessToken);

    void verifyEmail(String token);

    void resendVerification(ResendVerificationRequest request);

    TokenValidationResponse validateToken(String token);

    void changePassword(String accessToken, ChangePasswordRequest request);
}