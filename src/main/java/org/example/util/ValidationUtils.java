// src/main/java/org/example/util/ValidationUtils.java
package org.example.util;

import org.example.exception.BusinessException;

public class ValidationUtils {

    public static void validatePassword(String password) {
        if (password == null || password.length() < 8) {
            throw new BusinessException("Пароль должен содержать минимум 8 символов");
        }

        if (!password.matches(".*[A-Z].*")) {
            throw new BusinessException("Пароль должен содержать хотя бы одну заглавную букву");
        }

        if (!password.matches(".*[0-9].*")) {
            throw new BusinessException("Пароль должен содержать хотя бы одну цифру");
        }
    }

    public static void validateEmail(String email) {
        if (email == null || !email.matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new BusinessException("Неверный формат email");
        }
    }
}