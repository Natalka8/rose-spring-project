package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.RegisterRequest;
import org.example.dto.UserResponse;
import org.example.entity.User;
import org.example.repository.UserRepository;
import org.example.security.JwtTokenProvider;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SecurityService {

    private final UserService userService;
    private final UserRepository userRepository;
    private final JwtTokenProvider jwtTokenProvider;
    private final UserDetailsService userDetailsService;
    private final AuthenticationManager authenticationManager;
    private final PasswordEncoder passwordEncoder;

    // Constants for calculations
    private static final int DEFAULT_MAX_HEALTH = 100;
    private static final int DEFAULT_MAX_MANA = 100;
    private static final int DEFAULT_MAX_STAMINA = 100;

    /**
     * Register new user
     */
    public Map<String, Object> register(String username, String email, String password) {
        log.info("Регистрация пользователя: {}", username);

        try {
            // Create registration request
            RegisterRequest registerRequest = new RegisterRequest();
            registerRequest.setUsername(username);
            registerRequest.setEmail(email);
            registerRequest.setPassword(password);

            // Use UserService for registration
            UserResponse userResponse = userService.registerUser(registerRequest);

            // Get user for further processing
            User user = userRepository.findById(userResponse.getId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found after registration"));

            // Load UserDetails for token generation
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // Generate tokens
            String accessToken = jwtTokenProvider.generateToken(username);
            String refreshToken = jwtTokenProvider.generateRefreshToken(username);

            log.info("Пользователь {} успешно зарегистрирован", username);

            return createAuthResponse(user, userDetails, accessToken, refreshToken, "User registered successfully");

        } catch (IllegalArgumentException e) {
            log.error("Ошибка регистрации пользователя {}: {}", username, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при регистрации пользователя {}: {}", username, e.getMessage());
            throw new RuntimeException("Registration failed: " + e.getMessage());
        }
    }

    /**
     * Register user with class and race selection
     */
    public Map<String, Object> registerWithClass(String username, String email, String password,
                                                 String characterClass, String race) {
        log.info("Регистрация пользователя {} с классом {} и расой {}",
                username, characterClass, race);

        try {
            // Create registration request
            RegisterRequest registerRequest = new RegisterRequest();
            registerRequest.setUsername(username);
            registerRequest.setEmail(email);
            registerRequest.setPassword(password);

            UserResponse userResponse = userService.registerUser(registerRequest);
            User user = userRepository.findById(userResponse.getId())
                    .orElseThrow(() -> new IllegalArgumentException("User not found after registration"));

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            String accessToken = jwtTokenProvider.generateToken(username);
            String refreshToken = jwtTokenProvider.generateRefreshToken(username);

            return createAuthResponse(user, userDetails, accessToken, refreshToken,
                    "User registered successfully as " + characterClass + " " + race);

        } catch (IllegalArgumentException e) {
            log.error("Ошибка регистрации пользователя {} с классом: {}", username, e.getMessage());
            throw e;
        }
    }

    /**
     * Authenticate user
     */
    public Map<String, Object> login(String username, String password) {
        log.info("Аутентификация пользователя: {}", username);

        try {
            // Authenticate via Spring Security
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password)
            );

            SecurityContextHolder.getContext().setAuthentication(authentication);

            // Get user
            User user = userRepository.findByUsername(username)
                    .orElseThrow(() -> new IllegalArgumentException("User not found"));

            // Update last login time via UserService
            userService.updateLastLogin(username);

            UserDetails userDetails = userDetailsService.loadUserByUsername(username);
            String accessToken = jwtTokenProvider.generateToken(username);
            String refreshToken = jwtTokenProvider.generateRefreshToken(username);

            log.info("Пользователь {} успешно аутентифицирован", username);

            return createAuthResponse(user, userDetails, accessToken, refreshToken, "Login successful");

        } catch (IllegalArgumentException e) {
            log.warn("Ошибка аутентификации пользователя {}: {}", username, e.getMessage());
            throw e;
        } catch (Exception e) {
            log.error("Неожиданная ошибка при аутентификации пользователя {}: {}", username, e.getMessage());
            throw new RuntimeException("Authentication failed: " + e.getMessage());
        }
    }

    /**
     * Logout from system
     */
    public Map<String, Object> logout(String token) {
        log.info("Выход из системы");

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated()) {
            String username = authentication.getName();
            log.info("Пользователь {} вышел из системы", username);
        }

        SecurityContextHolder.clearContext();

        return Map.of(
                "success", true,
                "message", "Logout successful"
        );
    }

    /**
     * Refresh access token
     */
    public Map<String, Object> refreshToken(String refreshToken) {
        log.info("Обновление токена");

        if (!jwtTokenProvider.validateToken(refreshToken)) {
            throw new IllegalArgumentException("Invalid or expired refresh token");
        }

        String username = jwtTokenProvider.getUsernameFromToken(refreshToken);

        // Get user via UserService
        UserResponse userResponse = userService.getUserByUsername(username);
        User user = userRepository.findById(userResponse.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!user.isEnabled()) {
            throw new IllegalArgumentException("User account is disabled");
        }

        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String newAccessToken = jwtTokenProvider.generateToken(username);
        String newRefreshToken = jwtTokenProvider.generateRefreshToken(username);

        return Map.of(
                "success", true,
                "message", "Token refreshed successfully",
                "accessToken", newAccessToken,
                "refreshToken", newRefreshToken,
                "tokenType", "Bearer"
        );
    }

    /**
     * Validate token
     */
    public Map<String, Object> validateToken(String token) {
        boolean isValid = jwtTokenProvider.validateToken(token);
        String username = null;
        User user = null;

        if (isValid) {
            username = jwtTokenProvider.getUsernameFromToken(token);
            try {
                UserResponse userResponse = userService.getUserByUsername(username);
                user = userRepository.findById(userResponse.getId())
                        .orElseThrow(() -> new IllegalArgumentException("User not found"));
                if (!user.isEnabled()) {
                    throw new IllegalArgumentException("User account is disabled");
                }
            } catch (IllegalArgumentException e) {
                isValid = false;
            }
        }

        Map<String, Object> response = new HashMap<>();
        response.put("valid", isValid);
        if (username != null) {
            response.put("username", username);
        }
        response.put("message", isValid ? "Token is valid" : "Token is invalid or expired");

        if (user != null) {
            response.put("userId", user.getId());
            response.put("username", user.getUsername());
            response.put("email", user.getEmail());
            response.put("enabled", user.isEnabled());
            response.put("status", user.getStatus() != null ? user.getStatus().name() : "ACTIVE");
        }

        return response;
    }

    /**
     * Get current user
     */
    public Map<String, Object> getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated() ||
                authentication.getPrincipal().equals("anonymousUser")) {
            throw new IllegalArgumentException("No authenticated user found");
        }

        String username = authentication.getName();
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        return createUserProfileResponse(user, authentication);
    }

    /**
     * Change password
     */
    public Map<String, Object> changePassword(Long userId, String oldPassword, String newPassword) {
        log.info("Смена пароля для пользователя с ID: {}", userId);

        try {
            userService.changePassword(userId, oldPassword, newPassword);
            return Map.of(
                    "success", true,
                    "message", "Password changed successfully"
            );
        } catch (IllegalArgumentException e) {
            log.error("Ошибка смены пароля для пользователя с ID {}: {}", userId, e.getMessage());
            throw e;
        }
    }

    /**
     * Get user profile by ID
     */
    public Map<String, Object> getUserProfile(Long userId) {
        log.debug("Получение профиля пользователя с ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        Map<String, Object> profile = createUserProfileResponse(user, authentication);
        profile.remove("roles");

        return profile;
    }

    /**
     * Activate account
     */
    public Map<String, Object> activateAccount(Long userId) {
        log.info("Активация учетной записи пользователя с ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setEnabled(true);
        user.setStatus(User.UserStatus.ACTIVE);
        userRepository.save(user);

        return Map.of(
                "success", true,
                "message", "Account activated successfully"
        );
    }

    /**
     * Deactivate account
     */
    public Map<String, Object> deactivateAccount(Long userId) {
        log.info("Деактивация учетной записи пользователя с ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setEnabled(false);
        user.setStatus(User.UserStatus.INACTIVE);
        userRepository.save(user);

        return Map.of(
                "success", true,
                "message", "Account deactivated successfully"
        );
    }

    /**
     * Lock account (temporary)
     */
    public Map<String, Object> lockAccount(Long userId) {
        log.info("Блокировка учетной записи пользователя с ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.lockAccount(); // Use method from User
        userRepository.save(user);

        return Map.of(
                "success", true,
                "message", "Account locked successfully"
        );
    }

    /**
     * Unlock account
     */
    public Map<String, Object> unlockAccount(Long userId) {
        log.info("Разблокировка учетной записи пользователя с ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.unlockAccount(); // Use method from User
        userRepository.save(user);

        return Map.of(
                "success", true,
                "message", "Account unlocked successfully"
        );
    }

    /**
     * Ban user (permanent lock)
     */
    public Map<String, Object> banAccount(Long userId) {
        log.info("Бан пользователя с ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.banAccount(); // USE NEW METHOD FROM USER
        userRepository.save(user);

        return Map.of(
                "success", true,
                "message", "Account banned successfully"
        );
    }

    /**
     * Mark account as deleted
     */
    public Map<String, Object> markAsDeleted(Long userId) {
        log.info("Пометка аккаунта как удаленного для пользователя с ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setEnabled(false);
        user.setDeletedAt(LocalDateTime.now());
        user.setStatus(User.UserStatus.DELETED);
        userRepository.save(user);

        return Map.of(
                "success", true,
                "message", "Account marked as deleted successfully"
        );
    }

    /**
     * Reset password (administrative function)
     */
    public Map<String, Object> resetPassword(Long userId, String newPassword) {
        log.info("Сброс пароля для пользователя с ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);

        return Map.of(
                "success", true,
                "message", "Password reset successfully"
        );
    }

    /**
     * Check username availability
     */
    public Map<String, Object> checkUsernameAvailability(String username) {
        boolean exists = userService.existsByUsername(username);
        return Map.of(
                "available", !exists,
                "username", username,
                "message", !exists ? "Username is available" : "Username is already taken"
        );
    }

    /**
     * Check email availability
     */
    public Map<String, Object> checkEmailAvailability(String email) {
        boolean exists = userService.existsByEmail(email);
        return Map.of(
                "available", !exists,
                "email", email,
                "message", !exists ? "Email is available" : "Email is already registered"
        );
    }

    /**
     * Get user by username
     */
    public UserResponse getUserByUsername(String username) {
        return userService.getUserByUsername(username);
    }

    /**
     * Get user by ID
     */
    public UserResponse getUserById(Long userId) {
        return userService.getUserById(userId);
    }

    /**
     * Check login ability for user
     */
    public Map<String, Object> checkLoginAbility(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        boolean canLogin = user.canLogin();
        String status = user.getStatus() != null ? user.getStatus().name() : "UNKNOWN";

        return Map.of(
                "canLogin", canLogin,
                "userId", userId,
                "username", user.getUsername(),
                "enabled", user.isEnabled(),
                "accountNonLocked", user.isAccountNonLocked(),
                "status", status,
                "message", canLogin ? "User can login" : "User cannot login"
        );
    }

    /**
     * Reset login attempts counter
     */
    public Map<String, Object> resetLoginAttempts(Long userId) {
        log.info("Сброс счетчика попыток входа для пользователя с ID: {}", userId);

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        user.resetLoginAttempts();
        userRepository.save(user);

        return Map.of(
                "success", true,
                "message", "Login attempts reset successfully",
                "userId", userId,
                "loginAttempts", 0
        );
    }

    // ==================== HELPER METHODS ====================

    private Map<String, Object> createAuthResponse(User user, UserDetails userDetails,
                                                   String accessToken, String refreshToken,
                                                   String message) {
        Map<String, Object> response = new HashMap<>();

        response.put("success", true);
        response.put("message", message);
        response.put("accessToken", accessToken);
        response.put("refreshToken", refreshToken);
        response.put("tokenType", "Bearer");

        // Main information
        response.put("userId", user.getId());
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("enabled", user.isEnabled());
        response.put("status", user.getStatus() != null ? user.getStatus().name() : "ACTIVE");

        // Roles
        response.put("roles", userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()));

        return response;
    }

    private Map<String, Object> createUserProfileResponse(User user, Authentication authentication) {
        Map<String, Object> response = new HashMap<>();

        // Main information
        response.put("id", user.getId());
        response.put("username", user.getUsername());
        response.put("email", user.getEmail());
        response.put("enabled", user.isEnabled());
        response.put("accountNonLocked", user.isAccountNonLocked());
        response.put("accountNonExpired", user.isAccountNonExpired());
        response.put("credentialsNonExpired", user.isCredentialsNonExpired());
        response.put("status", user.getStatus() != null ? user.getStatus().name() : "ACTIVE");

        // Additional fields from User
        addOptionalField(response, "firstName", user.getFirstName());
        addOptionalField(response, "lastName", user.getLastName());
        addOptionalField(response, "bio", user.getBio());

        // Calculated values for game
        response.put("maxHealth", DEFAULT_MAX_HEALTH);
        response.put("maxMana", DEFAULT_MAX_MANA);
        response.put("maxStamina", DEFAULT_MAX_STAMINA);

        // Base characteristics
        response.put("strength", 10);
        response.put("agility", 10);
        response.put("intelligence", 10);
        response.put("vitality", 10);
        response.put("luck", 5);

        // Timestamps
        addOptionalField(response, "createdAt", user.getCreatedAt());
        addOptionalField(response, "updatedAt", user.getUpdatedAt());
        addOptionalField(response, "lastLoginAt", user.getLastLoginAt());
        addOptionalField(response, "deletedAt", user.getDeletedAt());
        response.put("lastActivity", LocalDateTime.now());

        // Roles (only for authenticated requests)
        if (authentication != null) {
            response.put("roles", authentication.getAuthorities().stream()
                    .map(GrantedAuthority::getAuthority)
                    .collect(Collectors.toList()));
        }

        // Statistics (if available)
        response.put("loginAttempts", user.getLoginAttempts());

        return response;
    }

    private void addOptionalField(Map<String, Object> response, String fieldName, Object value) {
        if (value != null) {
            response.put(fieldName, value);
        }
    }
}