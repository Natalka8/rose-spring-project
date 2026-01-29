// src/main/java/org/example/aspect/LoggingAspect.java
package org.example.aspect;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.core.annotation.Order;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import jakarta.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.time.Duration;
import java.time.Instant;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * Aspect for centralized logging
 * Logs:
 * 1. Method entry/exit
 * 2. Execution time
 * 3. Parameters and results
 * 4. Errors and exceptions
 * 5. User and request information
 */
@Slf4j
@Aspect
@Component
@Order(1) // High priority for logging
public class LoggingAspect {

    /**
     * Logging all public methods in controllers
     */
    @Pointcut("execution(public * org.example.controller.*.*(..))")
    public void controllerMethods() {}

    /**
     * Logging all public methods in services
     */
    @Pointcut("execution(public * org.example.service.*.*(..))")
    public void serviceMethods() {}

    /**
     * Logging all methods with @Loggable annotation
     */
    @Pointcut("@annotation(org.example.annotation.Loggable)")
    public void loggableMethods() {}

    /**
     * Logging all methods in the application package
     */
    @Pointcut("within(org.example..*)")
    public void applicationPackage() {}

    /**
     * Main entry point for logging - combination of several pointcuts
     */
    @Pointcut("controllerMethods() || serviceMethods() || loggableMethods()")
    public void loggablePointcut() {}

    /**
     * Logging before method execution
     */
    @Before("loggablePointcut()")
    public void logBefore(JoinPoint joinPoint) {
        Method method = getMethod(joinPoint);
        String methodName = method.getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        // Get user information
        String username = getCurrentUsername();

        // Get HTTP request information (if available)
        String requestInfo = getRequestInfo();

        // Log method parameters
        String parameters = getMethodParameters(joinPoint);

        log.info("""
            🔹 METHOD STARTED
            ┌────────────────────────────────────
            │ User:             {}
            │ Class:            {}
            │ Method:           {}
            │ Parameters:       {}
            │ HTTP Request:     {}
            └────────────────────────────────────""",
                username, className, methodName, parameters, requestInfo);
    }

    /**
     * Logging after successful method execution
     */
    @AfterReturning(
            pointcut = "loggablePointcut()",
            returning = "result"
    )
    public void logAfterReturning(JoinPoint joinPoint, Object result) {
        Method method = getMethod(joinPoint);
        String methodName = method.getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String username = getCurrentUsername();

        // Format result for logging
        String resultString = formatResult(result);

        log.info("""
            ✅ METHOD COMPLETED SUCCESSFULLY
            ┌────────────────────────────────────
            │ User:             {}
            │ Class:            {}
            │ Method:           {}
            │ Result:           {}
            └────────────────────────────────────""",
                username, className, methodName, resultString);
    }

    /**
     * Logging method execution time
     */
    @Around("loggablePointcut()")
    public Object logExecutionTime(ProceedingJoinPoint joinPoint) throws Throwable {
        Instant start = Instant.now();

        try {
            // Proceed with method execution
            Object result = joinPoint.proceed();

            // Calculate execution time
            Duration duration = Duration.between(start, Instant.now());

            // Log execution time for slow methods
            if (duration.toMillis() > 100) { // More than 100ms - slow method
                logWarnExecutionTime(joinPoint, duration);
            } else {
                logDebugExecutionTime(joinPoint, duration);
            }

            return result;

        } catch (Throwable throwable) {
            // Log execution time even on error
            Duration duration = Duration.between(start, Instant.now());
            logErrorExecutionTime(joinPoint, duration, throwable);
            throw throwable;
        }
    }

    /**
     * Logging when an exception occurs
     */
    @AfterThrowing(
            pointcut = "loggablePointcut()",
            throwing = "exception"
    )
    public void logAfterThrowing(JoinPoint joinPoint, Throwable exception) {
        Method method = getMethod(joinPoint);
        String methodName = method.getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();
        String username = getCurrentUsername();
        String parameters = getMethodParameters(joinPoint);

        // Determine log level based on exception type
        if (exception instanceof org.springframework.security.access.AccessDeniedException) {
            log.warn("""
                ⚠️  ACCESS DENIED
                ┌────────────────────────────────────
                │ User:             {}
                │ Class:            {}
                │ Method:           {}
                │ Parameters:       {}
                │ Exception:        {}
                │ Message:          {}
                └────────────────────────────────────""",
                    username, className, methodName, parameters,
                    exception.getClass().getSimpleName(), exception.getMessage());
        } else {
            log.error("""
                ❗ METHOD ERROR
                ┌────────────────────────────────────
                │ User:             {}
                │ Class:            {}
                │ Method:           {}
                │ Parameters:       {}
                │ Exception:        {}
                │ Message:          {}
                └────────────────────────────────────""",
                    username, className, methodName, parameters,
                    exception.getClass().getSimpleName(), exception.getMessage(),
                    exception);
        }
    }

    /**
     * Logging after method completion (success or error)
     */
    @After("loggablePointcut()")
    public void logAfter(JoinPoint joinPoint) {
        if (log.isDebugEnabled()) {
            Method method = getMethod(joinPoint);
            String methodName = method.getName();
            String className = joinPoint.getTarget().getClass().getSimpleName();

            log.debug("""
                🔚 METHOD COMPLETED
                ┌────────────────────────────────────
                │ Class:            {}
                │ Method:           {}
                └────────────────────────────────────""",
                    className, methodName);
        }
    }

    // ========== HELPER METHODS ==========

    /**
     * Get current user information
     */
    private String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return "anonymous";
        }
        return authentication.getName();
    }

    /**
     * Get HTTP request information
     */
    private String getRequestInfo() {
        try {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                return String.format("%s %s",
                        request.getMethod(),
                        request.getRequestURI());
            }
        } catch (IllegalStateException e) {
            // Not in HTTP request context
        }
        return "N/A";
    }

    /**
     * Format method parameters for logging
     */
    private String getMethodParameters(JoinPoint joinPoint) {
        Object[] args = joinPoint.getArgs();
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        String[] parameterNames = signature.getParameterNames();

        if (args == null || args.length == 0) {
            return "No parameters";
        }

        StringBuilder params = new StringBuilder();
        for (int i = 0; i < args.length; i++) {
            String paramName = parameterNames != null && i < parameterNames.length
                    ? parameterNames[i]
                    : "arg" + i;

            // Do not log passwords and other sensitive data
            if (isSensitiveParameter(paramName)) {
                params.append(paramName).append("=[PROTECTED]");
            } else {
                params.append(paramName).append("=").append(formatObject(args[i]));
            }

            if (i < args.length - 1) {
                params.append(", ");
            }
        }

        return params.toString();
    }

    /**
     * Check if parameter is sensitive
     */
    private boolean isSensitiveParameter(String paramName) {
        String lowerParamName = paramName.toLowerCase();
        return lowerParamName.contains("password") ||
                lowerParamName.contains("token") ||
                lowerParamName.contains("secret") ||
                lowerParamName.contains("key");
    }

    /**
     * Format object for logging
     */
    private String formatObject(Object obj) {
        if (obj == null) {
            return "null";
        }

        // For arrays
        if (obj.getClass().isArray()) {
            return Arrays.toString((Object[]) obj);
        }

        // For collections
        if (obj instanceof Iterable) {
            return ((Iterable<?>) obj).toString();
        }

        // Limit string length
        String str = obj.toString();
        if (str.length() > 100) {
            return str.substring(0, 100) + "... [truncated]";
        }

        return str;
    }

    /**
     * Format method result
     */
    private String formatResult(Object result) {
        if (result == null) {
            return "void/null";
        }

        // For ResponseEntity and similar
        if (result.toString().contains("ResponseEntity")) {
            return result.getClass().getSimpleName();
        }

        return formatObject(result);
    }

    /**
     * Get method from JoinPoint
     */
    private Method getMethod(JoinPoint joinPoint) {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        return signature.getMethod();
    }

    /**
     * Log execution time for slow methods
     */
    private void logWarnExecutionTime(ProceedingJoinPoint joinPoint, Duration duration) {
        Method method = getMethod(joinPoint);
        String methodName = method.getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        log.warn("""
            ⏱️  SLOW METHOD
            ┌────────────────────────────────────
            │ Class:            {}
            │ Method:           {}
            │ Execution Time:   {} ms
            │ Status:           SLOW
            └────────────────────────────────────""",
                className, methodName, duration.toMillis());
    }

    /**
     * Log execution time for fast methods (debug level)
     */
    private void logDebugExecutionTime(ProceedingJoinPoint joinPoint, Duration duration) {
        if (log.isDebugEnabled()) {
            Method method = getMethod(joinPoint);
            String methodName = method.getName();
            String className = joinPoint.getTarget().getClass().getSimpleName();

            log.debug("""
                ⏱️  EXECUTION TIME
                ┌────────────────────────────────────
                │ Class:            {}
                │ Method:           {}
                │ Execution Time:   {} ms
                │ Status:           NORMAL
                └────────────────────────────────────""",
                    className, methodName, duration.toMillis());
        }
    }

    /**
     * Log execution time on error
     */
    private void logErrorExecutionTime(ProceedingJoinPoint joinPoint, Duration duration, Throwable throwable) {
        Method method = getMethod(joinPoint);
        String methodName = method.getName();
        String className = joinPoint.getTarget().getClass().getSimpleName();

        log.error("""
            ⏱️  EXECUTION TIME WITH ERROR
            ┌────────────────────────────────────
            │ Class:            {}
            │ Method:           {}
            │ Execution Time:   {} ms
            │ Exception:        {}
            │ Status:           ERROR
            └────────────────────────────────────""",
                className, methodName, duration.toMillis(),
                throwable.getClass().getSimpleName());
    }

    /**
     * Log HTTP controller calls with detailed information
     */
    @Before("controllerMethods()")
    public void logHttpRequestDetails(JoinPoint joinPoint) {
        try {
            ServletRequestAttributes attributes =
                    (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();

                Map<String, String> headers = new HashMap<>();
                request.getHeaderNames().asIterator()
                        .forEachRemaining(headerName ->
                                headers.put(headerName, request.getHeader(headerName)));

                // Remove sensitive headers
                headers.keySet().removeIf(key ->
                        key.toLowerCase().contains("authorization") ||
                                key.toLowerCase().contains("cookie"));

                log.info("""
                    🌐 HTTP REQUEST
                    ┌────────────────────────────────────
                    │ Method:           {}
                    │ URI:              {}
                    │ IP:               {}
                    │ User-Agent:       {}
                    │ Headers:          {}
                    └────────────────────────────────────""",
                        request.getMethod(),
                        request.getRequestURI(),
                        request.getRemoteAddr(),
                        request.getHeader("User-Agent"),
                        headers);
            }
        } catch (Exception e) {
            // Ignore errors during logging
        }
    }
}