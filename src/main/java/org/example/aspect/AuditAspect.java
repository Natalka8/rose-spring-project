package org.example.aspect;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.AfterReturning;
import org.aspectj.lang.annotation.Aspect;
import org.example.entity.AuditLog;
import org.example.entity.User;
import org.example.repository.AuditLogRepository;
import org.example.security.CustomUserDetails;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import jakarta.servlet.http.HttpServletRequest;
import java.util.Optional;

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class AuditAspect {

    private final AuditLogRepository auditLogRepository;
    private final HttpServletRequest request;
    // Removed userRepository as it's in a different package

    @AfterReturning(
            pointcut = "@annotation(org.example.annotation.Auditable)",
            returning = "result"
    )
    public void auditAction(JoinPoint joinPoint, Object result) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

            // Check if user is authenticated
            if (authentication == null || !authentication.isAuthenticated() ||
                    "anonymousUser".equals(authentication.getPrincipal())) {
                saveAnonymousAudit(joinPoint, result);
                return;
            }

            Object principal = authentication.getPrincipal();

            if (principal instanceof CustomUserDetails userDetails) {
                saveUserAudit(joinPoint, result, userDetails);
            } else {
                saveAnonymousAudit(joinPoint, result);
            }

        } catch (Exception e) {
            // Use logger instead of System.err and printStackTrace
            log.error("Error while writing audit: {}", e.getMessage(), e);
        }
    }

    private void saveUserAudit(JoinPoint joinPoint, Object result, CustomUserDetails userDetails) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        // Get additional parameters
        String ipAddress = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        String details = buildDetails(joinPoint, result);

        // Create User with only ID (without repository access)
        User user = User.builder()
                .id(userDetails.getId())
                .build();

        // Create audit record
        AuditLog auditLog = AuditLog.builder()
                .user(user)
                .action(className + "." + methodName)
                .details(details)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();

        auditLogRepository.save(auditLog);
        log.debug("Audit recorded for user ID: {}, action: {}",
                userDetails.getId(), className + "." + methodName);
    }

    private void saveAnonymousAudit(JoinPoint joinPoint, Object result) {
        String methodName = joinPoint.getSignature().getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        String ipAddress = getClientIpAddress(request);
        String userAgent = request.getHeader("User-Agent");
        String details = buildDetails(joinPoint, result);

        AuditLog auditLog = AuditLog.builder()
                .user(null) // Anonymous user
                .action(className + "." + methodName)
                .details(details)
                .ipAddress(ipAddress)
                .userAgent(userAgent)
                .build();

        auditLogRepository.save(auditLog);
        log.debug("Audit recorded for anonymous user, action: {}",
                className + "." + methodName);
    }

    private String buildDetails(JoinPoint joinPoint, Object result) {
        StringBuilder details = new StringBuilder();

        try {
            // Get annotation for additional information
            org.example.annotation.Auditable auditableAnnotation = getAuditableAnnotation(joinPoint);

            if (auditableAnnotation != null && !auditableAnnotation.value().isEmpty()) {
                details.append("Description: ").append(auditableAnnotation.value()).append("\n");
            }

            details.append("Method: ").append(joinPoint.getSignature().toShortString()).append("\n");

            // Method arguments (if allowed)
            if (auditableAnnotation == null || auditableAnnotation.logArguments()) {
                Object[] args = joinPoint.getArgs();
                if (args.length > 0) {
                    details.append("Arguments (").append(args.length).append("):\n");
                    for (int i = 0; i < args.length; i++) {
                        details.append("  [").append(i).append("] ");
                        if (args[i] != null) {
                            String className = args[i].getClass().getSimpleName();
                            details.append(className).append(": ");

                            // Limit output for security
                            String stringValue = args[i].toString();
                            int maxLength = 100;
                            if (stringValue.length() > maxLength) {
                                stringValue = stringValue.substring(0, maxLength) + "...[truncated]";
                            }
                            // Escape line breaks
                            stringValue = stringValue.replace("\n", "\\n").replace("\r", "\\r");
                            details.append(stringValue);
                        } else {
                            details.append("null");
                        }
                        details.append("\n");
                    }
                }
            }

            // Result (if allowed)
            if (auditableAnnotation == null || auditableAnnotation.logResult()) {
                details.append("Result: ");
                if (result != null) {
                    String resultClassName = result.getClass().getSimpleName();
                    details.append(resultClassName);

                    // Output value for simple types and DTOs
                    if (result instanceof String || result instanceof Number || result instanceof Boolean) {
                        String stringValue = result.toString();
                        int maxLength = 200;
                        if (stringValue.length() > maxLength) {
                            stringValue = stringValue.substring(0, maxLength) + "...[truncated]";
                        }
                        details.append(" = ").append(stringValue);
                    } else if (result instanceof java.util.Collection) {
                        details.append(" (size = ").append(((java.util.Collection<?>) result).size()).append(")");
                    }
                } else {
                    details.append("void/null");
                }
            }

        } catch (Exception e) {
            details.append("Error building details: ").append(e.getMessage());
        }

        return details.toString();
    }

    private org.example.annotation.Auditable getAuditableAnnotation(JoinPoint joinPoint) {
        try {
            // Get annotation from method
            var signature = joinPoint.getSignature();
            var method = joinPoint.getTarget().getClass()
                    .getDeclaredMethod(signature.getName(),
                            getParameterClasses(joinPoint.getArgs()));
            return method.getAnnotation(org.example.annotation.Auditable.class);
        } catch (Exception e) {
            log.debug("Failed to get @Auditable annotation: {}", e.getMessage());
            return null;
        }
    }

    private Class<?>[] getParameterClasses(Object[] args) {
        if (args == null || args.length == 0) {
            return new Class<?>[0];
        }

        Class<?>[] classes = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            classes[i] = args[i] != null ? args[i].getClass() : Object.class;
        }
        return classes;
    }

    private String getClientIpAddress(HttpServletRequest request) {
        if (request == null) {
            return "unknown";
        }

        // Check various headers that may contain the real IP
        String[] headers = {
                "X-Forwarded-For",
                "Proxy-Client-IP",
                "WL-Proxy-Client-IP",
                "HTTP_X_FORWARDED_FOR",
                "HTTP_X_FORWARDED",
                "HTTP_X_CLUSTER_CLIENT_IP",
                "HTTP_CLIENT_IP",
                "HTTP_FORWARDED_FOR",
                "HTTP_FORWARDED",
                "HTTP_VIA",
                "REMOTE_ADDR"
        };

        for (String header : headers) {
            String ip = request.getHeader(header);
            if (ip != null && !ip.isEmpty() && !"unknown".equalsIgnoreCase(ip)) {
                // There may be multiple IPs separated by commas
                String[] ips = ip.split(",");
                return ips[0].trim();
            }
        }

        return request.getRemoteAddr();
    }
}