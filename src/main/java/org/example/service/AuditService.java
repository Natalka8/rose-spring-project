package org.example.service;

import org.example.entity.AuditLog;
import org.example.entity.User;
import org.example.repository.AuditLogRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

/**
 * Service for auditing user actions
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class AuditService {

    private final AuditLogRepository auditLogRepository;
    private final HttpServletRequest request;

    // Constants for standard actions
    public static final String ACTION_LOGIN = "LOGIN";
    public static final String ACTION_LOGIN_FAILED = "LOGIN_FAILED";
    public static final String ACTION_LOGOUT = "LOGOUT";
    public static final String ACTION_REGISTER = "REGISTER";
    public static final String ACTION_UPDATE_PROFILE = "UPDATE_PROFILE";
    public static final String ACTION_CHANGE_PASSWORD = "CHANGE_PASSWORD";
    public static final String ACTION_DELETE_ACCOUNT = "DELETE_ACCOUNT";
    public static final String ACTION_UPDATE_USER_STATUS = "UPDATE_USER_STATUS";
    public static final String ACTION_TASK_CREATED = "TASK_CREATED";
    public static final String ACTION_TASK_UPDATED = "TASK_UPDATED";
    public static final String ACTION_TASK_DELETED = "TASK_DELETED";
    public static final String ACTION_GAME_SESSION_STARTED = "GAME_SESSION_STARTED";
    public static final String ACTION_GAME_SESSION_COMPLETED = "GAME_SESSION_COMPLETED";

    /**
     * Log user action by user ID
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logAction(Long userId, String action, String details) {
        try {
            // Create User only with ID for AuditLog
            User user = null;
            if (userId != null) {
                user = User.builder()
                        .id(userId)
                        .build();
            }

            AuditLog logEntry = AuditLog.builder()
                    .user(user)  // Pass User object (can be null)
                    .action(action)
                    .details(details)
                    .ipAddress(getClientIp())
                    .userAgent(request.getHeader("User-Agent"))
                    .createdAt(LocalDateTime.now())
                    .build();

            auditLogRepository.save(logEntry);
            log.debug("Действие записано в аудит: {} для пользователя ID: {}", action, userId);
        } catch (Exception e) {
            // Log error, but do not interrupt main flow
            log.error("Ошибка при записи в аудит-лог для пользователя ID {}: {}", userId, e.getMessage());
        }
    }

    /**
     * Log user action by User object
     */
    @Async
    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void logAction(User user, String action, String details) {
        try {
            AuditLog logEntry = AuditLog.builder()
                    .user(user)  // Pass ready User object
                    .action(action)
                    .details(details)
                    .ipAddress(getClientIp())
                    .userAgent(request.getHeader("User-Agent"))
                    .createdAt(LocalDateTime.now())
                    .build();

            auditLogRepository.save(logEntry);
            log.debug("Действие записано в аудит: {} для пользователя: {}", action,
                    user != null ? user.getUsername() : "null");
        } catch (Exception e) {
            // Log error, but do not interrupt main flow
            log.error("Ошибка при записи в аудит-лог для пользователя {}: {}",
                    user != null ? user.getUsername() : "null", e.getMessage());
        }
    }

    /**
     * Log system login
     */
    @Async
    public void logLogin(User user, boolean success) {
        String action = success ? ACTION_LOGIN : ACTION_LOGIN_FAILED;
        String details = success ? "Успешный вход в систему" : "Неудачная попытка входа";
        logAction(user, action, details);
    }

    /**
     * Log system logout
     */
    @Async
    public void logLogout(User user) {
        logAction(user, ACTION_LOGOUT, "Выход из системы");
    }

    /**
     * Log registration
     */
    @Async
    public void logRegistration(User user) {
        logAction(user, ACTION_REGISTER, "Регистрация нового пользователя");
    }

    /**
     * Log profile update
     */
    @Async
    public void logProfileUpdate(User user) {
        logAction(user, ACTION_UPDATE_PROFILE, "Обновление профиля пользователя");
    }

    /**
     * Log password change
     */
    @Async
    public void logPasswordChange(User user) {
        logAction(user, ACTION_CHANGE_PASSWORD, "Смена пароля пользователя");
    }

    /**
     * Log account deletion
     */
    @Async
    public void logAccountDeletion(User user) {
        logAction(user, ACTION_DELETE_ACCOUNT, "Удаление аккаунта пользователя");
    }

    /**
     * Log user status change
     */
    @Async
    public void logUserStatusChange(User user, String oldStatus, String newStatus) {
        String details = String.format("Изменение статуса пользователя с %s на %s", oldStatus, newStatus);
        logAction(user, ACTION_UPDATE_USER_STATUS, details);
    }

    /**
     * Log failed login attempt with counter increment
     */
    @Async
    public void logFailedLoginAttempt(String username, int attemptCount) {
        String details = String.format("Неудачная попытка входа. Попытка №%d", attemptCount);
        logAction((User) null, ACTION_LOGIN_FAILED, details);
    }

    /**
     * Log without user (for system actions)
     */
    @Async
    public void logSystemAction(String action, String details) {
        logAction((Long) null, action, details);
    }

    /**
     * Get client IP address considering proxy
     */
    private String getClientIp() {
        try {
            // Check proxy headers
            String xForwardedFor = request.getHeader("X-Forwarded-For");
            if (xForwardedFor != null && !xForwardedFor.isEmpty()) {
                // Take first IP from list (client IP)
                return xForwardedFor.split(",")[0].trim();
            }

            String xRealIp = request.getHeader("X-Real-IP");
            if (xRealIp != null && !xRealIp.isEmpty()) {
                return xRealIp;
            }

            // If no proxy headers, take remote address
            return request.getRemoteAddr();
        } catch (Exception e) {
            log.warn("Ошибка при получении IP-адреса клиента: {}", e.getMessage());
            return "unknown";
        }
    }

    /**
     * Get client User-Agent
     */
    public String getUserAgent() {
        try {
            return request.getHeader("User-Agent");
        } catch (Exception e) {
            log.warn("Ошибка при получении User-Agent: {}", e.getMessage());
            return "unknown";
        }
    }

    /**
     * Check if audit logging is enabled (for conditions)
     */
    public boolean isAuditEnabled() {
        // Can add configuration check logic
        return true; // Enabled by default
    }
}