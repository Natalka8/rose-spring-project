package org.example.controller;

import org.example.dto.*;
import org.example.service.AuthService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Authentication", description = "User authentication and authorization endpoints")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register")
    @Operation(
            summary = "Register new user",
            description = "Creates a new user account with the provided credentials"
    )
    @ResponseStatus(HttpStatus.CREATED)
    public ResponseEntity<ApiResponse<AuthResponse>> register(
            @Valid @RequestBody RegisterRequest request) {

        log.info("Registration attempt for email: {}", request.getEmail());
        AuthResponse response = authService.register(request);

        log.info("User registered successfully: {}", request.getEmail());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(ApiResponse.success("User registered successfully", response));
    }

    @PostMapping("/login")
    @Operation(
            summary = "Authenticate user",
            description = "Authenticates user with email/username and password"
    )
    public ResponseEntity<ApiResponse<AuthResponse>> login(
            @Valid @RequestBody LoginRequest request,
            HttpServletRequest httpRequest) {

        String clientIp = getClientIp(httpRequest);
        log.info("Login attempt from IP: {} for user: {}", clientIp, request.getUsernameOrEmail());

        AuthResponse response = authService.login(request, clientIp);

        log.info("User logged in successfully: {}", request.getUsernameOrEmail());
        return ResponseEntity.ok(ApiResponse.success("Login successful", response));
    }

    @PostMapping("/refresh")
    @Operation(
            summary = "Refresh access token",
            description = "Uses refresh token to obtain new access token"
    )
    public ResponseEntity<ApiResponse<AuthResponse>> refreshToken(
            @RequestHeader("Authorization")
            @Parameter(description = "Refresh token starting with 'Bearer '", required = true)
            String refreshToken) {

        log.debug("Token refresh request");

        String token = extractToken(refreshToken);
        AuthResponse response = authService.refreshToken(token);

        return ResponseEntity.ok(ApiResponse.success("Token refreshed successfully", response));
    }

    @PostMapping("/logout")
    @Operation(
            summary = "Logout user",
            description = "Invalidates the current user's tokens"
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<String>> logout(  // Changed to String
                                                        @RequestHeader("Authorization")
                                                        @Parameter(description = "Access token starting with 'Bearer '", required = true)
                                                        String accessToken) {

        String token = extractToken(accessToken);
        authService.logout(token);
        log.info("User logged out successfully");

        return ResponseEntity.ok(ApiResponse.success("Logout successful", "User logged out")); // Added data
    }

    @PostMapping("/logout-all")
    @Operation(
            summary = "Logout from all devices",
            description = "Invalidates all user's active sessions"
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<String>> logoutAll(  // Changed to String
                                                           @RequestHeader("Authorization") String accessToken) {

        String token = extractToken(accessToken);
        authService.logoutAll(token);
        log.info("User logged out from all devices");

        return ResponseEntity.ok(ApiResponse.success("Logged out from all devices", "All sessions terminated")); // Added data
    }

    @PostMapping("/forgot-password")
    @Operation(
            summary = "Request password reset",
            description = "Sends password reset instructions to user's email"
    )
    public ResponseEntity<ApiResponse<String>> forgotPassword(  // Changed to String
                                                                @Valid @RequestBody ForgotPasswordRequest request) {

        log.info("Password reset requested for email: {}", request.getEmail());
        authService.forgotPassword(request);

        return ResponseEntity.ok(ApiResponse.success(
                "If the email exists, password reset instructions have been sent",
                "Reset email processed"
        ));
    }

    @PostMapping("/reset-password")
    @Operation(
            summary = "Reset password",
            description = "Resets password using token from email"
    )
    public ResponseEntity<ApiResponse<String>> resetPassword(  // Changed to String
                                                               @RequestParam String token,
                                                               @Valid @RequestBody ResetPasswordRequest request) {

        log.info("Password reset attempt with token");
        ResetPasswordDto resetPasswordDto = new ResetPasswordDto(token, request.getNewPassword());
        authService.resetPassword(resetPasswordDto);

        return ResponseEntity.ok(ApiResponse.success("Password reset successful", "Password updated"));
    }

    @GetMapping("/me")
    @Operation(
            summary = "Get current user info",
            description = "Returns information about currently authenticated user"
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<UserResponse>> getCurrentUser(
            @RequestHeader("Authorization") String accessToken) {

        String token = extractToken(accessToken);
        UserResponse user = authService.getCurrentUser(token);
        return ResponseEntity.ok(ApiResponse.success("User information retrieved", user));
    }

    @PostMapping("/verify-email")
    @Operation(
            summary = "Verify email",
            description = "Verifies user's email using verification token"
    )
    public ResponseEntity<ApiResponse<String>> verifyEmail(  // Changed to String
                                                             @RequestParam String token) {

        log.info("Email verification attempt with token");
        authService.verifyEmail(token);

        return ResponseEntity.ok(ApiResponse.success("Email verified successfully", "Email confirmed"));
    }

    @PostMapping("/resend-verification")
    @Operation(
            summary = "Resend verification email",
            description = "Resends email verification link"
    )
    public ResponseEntity<ApiResponse<String>> resendVerification(  // Changed to String
                                                                    @Valid @RequestBody ResendVerificationRequest request) {

        log.info("Verification email resend requested for: {}", request.getEmail());
        authService.resendVerification(request);

        return ResponseEntity.ok(ApiResponse.success("Verification email sent if account exists", "Email processed"));
    }

    @GetMapping("/validate-token")
    @Operation(
            summary = "Validate token",
            description = "Checks if provided token is valid"
    )
    public ResponseEntity<ApiResponse<TokenValidationResponse>> validateToken(
            @RequestParam String token) {

        TokenValidationResponse validation = authService.validateToken(token);
        return ResponseEntity.ok(ApiResponse.success("Token validation completed", validation));
    }

    @PostMapping("/change-password")
    @Operation(
            summary = "Change password",
            description = "Changes password for authenticated user"
    )
    @SecurityRequirement(name = "bearerAuth")
    public ResponseEntity<ApiResponse<String>> changePassword(  // Changed to String
                                                                @RequestHeader("Authorization") String accessToken,
                                                                @Valid @RequestBody ChangePasswordRequest request) {

        String token = extractToken(accessToken);
        authService.changePassword(token, request);
        log.info("Password changed successfully");

        return ResponseEntity.ok(ApiResponse.success("Password changed successfully", "Password updated"));
    }

    @GetMapping("/health")
    @Operation(
            summary = "Auth service health check",
            description = "Checks if authentication service is running"
    )
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        return ResponseEntity.ok(ApiResponse.success("Service healthy", "Auth service is running"));
    }

    // Helper methods
    private String getClientIp(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }

        String xForwardedForHeader = request.getHeader("X-Forwarded-For");
        if (xForwardedForHeader != null && !xForwardedForHeader.isEmpty()) {
            return xForwardedForHeader.split(",")[0].trim();
        }

        String xRealIpHeader = request.getHeader("X-Real-IP");
        if (xRealIpHeader != null && !xRealIpHeader.isEmpty()) {
            return xRealIpHeader;
        }

        return request.getRemoteAddr();
    }

    private String extractToken(String authHeader) {
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            return authHeader.substring(7);
        }
        return authHeader;
    }
}