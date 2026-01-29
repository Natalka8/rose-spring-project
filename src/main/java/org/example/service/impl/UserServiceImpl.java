package org.example.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.dto.RegisterRequest;
import org.example.dto.UpdateUserRequest;
import org.example.dto.UserProfileRequest;
import org.example.dto.UserResponse;
import org.example.entity.User;
import org.example.exception.BusinessException;
import org.example.exception.ResourceNotFoundException;
import org.example.exception.ValidationException;
import org.example.mapper.UserMapper;
import org.example.repository.UserRepository;
import org.example.service.UserService;
import org.example.util.Constants;
import org.example.util.ValidationUtils;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.security.authentication.AnonymousAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * Implementation of user service
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * Register new user
     */
    @Override
    @Transactional
    public UserResponse registerUser(RegisterRequest registerRequest) {
        log.info("Регистрация нового пользователя: {}", registerRequest.getUsername());

        // Check if user with such username exists
        if (userRepository.existsByUsername(registerRequest.getUsername())) {
            throw new BusinessException("Пользователь с таким именем уже существует");
        }

        // Check if user with such email exists
        if (registerRequest.getEmail() != null &&
                userRepository.existsByEmail(registerRequest.getEmail())) {
            throw new BusinessException("Пользователь с таким email уже существует");
        }

        // Password validation
        ValidationUtils.validatePassword(registerRequest.getPassword());

        // Email validation (if provided)
        if (registerRequest.getEmail() != null && !registerRequest.getEmail().isEmpty()) {
            ValidationUtils.validateEmail(registerRequest.getEmail());
        }

        // Create new user
        User user = User.builder()
                .username(registerRequest.getUsername())
                .email(registerRequest.getEmail())
                .password(passwordEncoder.encode(registerRequest.getPassword()))
                .firstName(registerRequest.getFirstName())
                .lastName(registerRequest.getLastName())
                .status(User.UserStatus.ACTIVE) // Using full path User.UserStatus
                .enabled(true)
                .accountNonExpired(true)
                .accountNonLocked(true)
                .credentialsNonExpired(true)
                .lastLoginAt(LocalDateTime.now())
                .loginAttempts(0)
                .roles(Set.of(Constants.ROLE_USER))
                .build();

        // Save user
        User savedUser = userRepository.save(user);
        log.info("Пользователь успешно зарегистрирован: {}", savedUser.getUsername());

        return userMapper.toDto(savedUser);
    }

    /**
     * Get user by ID
     */
    @Override
    @Cacheable(value = "users", key = "#id")
    public UserResponse getUserById(Long id) {
        log.debug("Получение пользователя по ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь", "id", id));

        checkUserAccess(user);
        return userMapper.toDto(user);
    }

    /**
     * Get user by username
     */
    @Override
    @Cacheable(value = "users", key = "#username")
    public UserResponse getUserByUsername(String username) {
        log.debug("Получение пользователя по имени: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь", "username", username));

        checkUserAccess(user);
        return userMapper.toDto(user);
    }

    /**
     * Get current authenticated user
     */
    @Override
    public UserResponse getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null ||
                authentication instanceof AnonymousAuthenticationToken ||
                !authentication.isAuthenticated()) {
            throw new BusinessException("Пользователь не аутентифицирован");
        }

        String username = authentication.getName();
        return getUserByUsername(username);
    }

    /**
     * Update user profile
     */
    @Override
    @Transactional
    @CachePut(value = "users", key = "#id")
    public UserResponse updateUser(Long id, UpdateUserRequest updateRequest) {
        log.info("Обновление профиля пользователя ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь", "id", id));

        checkUserAccess(user);

        // Update user fields
        if (updateRequest.getFirstName() != null) {
            user.setFirstName(updateRequest.getFirstName());
        }

        if (updateRequest.getLastName() != null) {
            user.setLastName(updateRequest.getLastName());
        }

        if (updateRequest.getEmail() != null && !updateRequest.getEmail().isEmpty()) {
            // Check if email is not used by another user
            Optional<User> existingUser = userRepository.findByEmail(updateRequest.getEmail());
            if (existingUser.isPresent() && !existingUser.get().getId().equals(id)) {
                throw new BusinessException("Email уже используется другим пользователем");
            }
            ValidationUtils.validateEmail(updateRequest.getEmail());
            user.setEmail(updateRequest.getEmail());
        }

        if (updateRequest.getBio() != null) {
            user.setBio(updateRequest.getBio());
        }

        user.setUpdatedAt(LocalDateTime.now());

        User updatedUser = userRepository.save(user);
        log.info("Профиль пользователя обновлен: {}", updatedUser.getUsername());

        return userMapper.toDto(updatedUser);
    }

    /**
     * Change password
     */
    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public void changePassword(Long id, String oldPassword, String newPassword) {
        log.info("Смена пароля для пользователя ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь", "id", id));

        // Check old password
        if (!passwordEncoder.matches(oldPassword, user.getPassword())) {
            throw new ValidationException("Неверный старый пароль");
        }

        // Validate new password
        ValidationUtils.validatePassword(newPassword);

        // Check that new password is different from old one
        if (passwordEncoder.matches(newPassword, user.getPassword())) {
            throw new ValidationException("Новый пароль должен отличаться от старого");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        user.setCredentialsNonExpired(true);
        user.setUpdatedAt(LocalDateTime.now());

        userRepository.save(user);
        log.info("Пароль успешно изменен для пользователя: {}", user.getUsername());
    }

    /**
     * Delete user (soft delete)
     */
    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public void deleteUser(Long id) {
        log.info("Удаление пользователя ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь", "id", id));

        checkUserAccess(user);

        // Soft delete
        user.setEnabled(false);
        user.setDeletedAt(LocalDateTime.now());
        user.setStatus(User.UserStatus.DELETED); // Using full path

        userRepository.save(user);
        log.info("Пользователь отмечен как удаленный: {}", user.getUsername());
    }

    /**
     * Get all users (with pagination)
     */
    @Override
    public Page<UserResponse> getAllUsers(Pageable pageable) {
        log.debug("Получение всех пользователей с пагинацией: {}", pageable);

        Page<User> usersPage = userRepository.findAllByEnabledTrue(pageable);
        return usersPage.map(userMapper::toDto);
    }

    /**
     * Search users by various criteria
     */
    @Override
    public List<UserResponse> searchUsers(Specification<User> spec) {
        log.debug("Поиск пользователей по спецификации");

        List<User> users = userRepository.findAll(spec);
        return users.stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    /**
     * Update user status
     */
    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#id")
    public UserResponse updateUserStatus(Long id, User.UserStatus status) { // Parameter type changed
        log.info("Обновление статуса пользователя ID: {} на {}", id, status);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь", "id", id));

        user.setStatus(status);

        switch (status) {
            case ACTIVE -> {
                user.setEnabled(true);
                user.setAccountNonLocked(true);
                user.setLoginAttempts(0);
            }
            case PENDING -> {
                user.setEnabled(false);
                user.setAccountNonLocked(false);
            }
            case INACTIVE -> user.setEnabled(false);
            case SUSPENDED -> {
                user.setEnabled(false);
                user.setAccountNonLocked(false);
            }
            case BANNED -> {
                user.setEnabled(false);
                user.setAccountNonLocked(false);
            }
            case DELETED -> {
                user.setEnabled(false);
                user.setDeletedAt(LocalDateTime.now());
            }
        }

        user.setUpdatedAt(LocalDateTime.now());
        User updatedUser = userRepository.save(user);

        log.info("Статус пользователя {} обновлен на {}",
                updatedUser.getUsername(), updatedUser.getStatus());

        return userMapper.toDto(updatedUser);
    }

    /**
     * Update last login time
     */
    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#username")
    public void updateLastLogin(String username) {
        log.debug("Обновление времени последнего входа для пользователя: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь", "username", username));

        user.setLastLoginAt(LocalDateTime.now());
        user.setLoginAttempts(0); // Reset login attempts counter

        userRepository.save(user);
    }

    /**
     * Increment failed login attempts counter
     */
    @Override
    @Transactional
    @CacheEvict(value = "users", key = "#username")
    public void incrementFailedLoginAttempts(String username) {
        log.debug("Увеличение счетчика неудачных попыток входа для пользователя: {}", username);

        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь", "username", username));

        int attempts = user.getLoginAttempts() + 1;
        user.setLoginAttempts(attempts);

        // Block account after 5 failed attempts
        if (attempts >= 5) {
            user.setAccountNonLocked(false);
            user.setStatus(User.UserStatus.SUSPENDED); // Using full path
            log.warn("Аккаунт пользователя {} заблокирован из-за множества неудачных попыток входа",
                    user.getUsername());
        }

        userRepository.save(user);
    }

    /**
     * Check if user exists by username
     */
    @Override
    public boolean existsByUsername(String username) {
        return userRepository.existsByUsername(username);
    }

    /**
     * Check if user exists by email
     */
    @Override
    public boolean existsByEmail(String email) {
        return userRepository.existsByEmail(email);
    }

    /**
     * Get user statistics
     */
    @Override
    public UserStats getUsersStats() {
        log.debug("Получение статистики пользователей");

        long totalUsers = userRepository.count();
        long activeUsers = userRepository.countByStatus(User.UserStatus.ACTIVE); // Using full path
        long suspendedUsers = userRepository.countByStatus(User.UserStatus.SUSPENDED); // Using full path
        long newUsersToday = userRepository.countByCreatedAtAfter(LocalDateTime.now().minusDays(1));

        return new UserStats(totalUsers, activeUsers, suspendedUsers, newUsersToday);
    }

    /**
     * Update user profile (another version)
     */
    @Override
    @Transactional
    @CachePut(value = "users", key = "#id")
    public UserResponse updateUserProfile(Long id, UserProfileRequest profileRequest) {
        log.info("Обновление профиля пользователя ID: {}", id);

        User user = userRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь", "id", id));

        checkUserAccess(user);

        // Profile data validation
        validateProfileRequest(profileRequest);

        // Update data
        userMapper.updateUserFromProfileRequest(profileRequest, user);
        user.setUpdatedAt(LocalDateTime.now());

        User updatedUser = userRepository.save(user);
        log.info("Профиль пользователя обновлен: {}", updatedUser.getUsername());

        return userMapper.toDto(updatedUser);
    }

    /**
     * Get user by email
     */
    @Override
    public UserResponse getUserByEmail(String email) {
        log.debug("Получение пользователя по email: {}", email);

        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Пользователь", "email", email));

        checkUserAccess(user);
        return userMapper.toDto(user);
    }

    /**
     * Check user access
     * User can edit only their own profile, admin can edit any
     */
    private void checkUserAccess(User user) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException("Доступ запрещен: пользователь не аутентифицирован");
        }

        String currentUsername = authentication.getName();
        boolean isAdmin = authentication.getAuthorities().stream()
                .anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));

        // User can view/edit only their own profile
        // Admin can view/edit any profile
        if (!isAdmin && !user.getUsername().equals(currentUsername)) {
            throw new BusinessException("Доступ запрещен: вы можете редактировать только свой профиль");
        }
    }

    /**
     * Validate profile update request
     */
    private void validateProfileRequest(UserProfileRequest request) {
        if (request.getFirstName() != null && request.getFirstName().length() > 50) {
            throw new ValidationException("Имя не может быть длиннее 50 символов");
        }

        if (request.getLastName() != null && request.getLastName().length() > 50) {
            throw new ValidationException("Фамилия не может быть длиннее 50 символов");
        }

        if (request.getBio() != null && request.getBio().length() > 500) {
            throw new ValidationException("Биография не может быть длиннее 500 символов");
        }
    }

    /**
     * Class for user statistics
     */
    public static class UserStats {
        private final long totalUsers;
        private final long activeUsers;
        private final long suspendedUsers;
        private final long newUsersToday;

        public UserStats(long totalUsers, long activeUsers, long suspendedUsers, long newUsersToday) {
            this.totalUsers = totalUsers;
            this.activeUsers = activeUsers;
            this.suspendedUsers = suspendedUsers;
            this.newUsersToday = newUsersToday;
        }

        public long getTotalUsers() {
            return totalUsers;
        }

        public long getActiveUsers() {
            return activeUsers;
        }

        public long getSuspendedUsers() {
            return suspendedUsers;
        }

        public long getNewUsersToday() {
            return newUsersToday;
        }

        @Override
        public String toString() {
            return String.format(
                    "UserStats{total=%d, active=%d, suspended=%d, newToday=%d}",
                    totalUsers, activeUsers, suspendedUsers, newUsersToday
            );
        }
    }
}