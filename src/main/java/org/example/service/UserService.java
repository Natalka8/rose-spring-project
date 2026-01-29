package org.example.service;

import org.example.dto.RegisterRequest;
import org.example.dto.UpdateUserRequest;
import org.example.dto.UserProfileRequest;
import org.example.dto.UserResponse;
import org.example.entity.User;
import org.example.service.impl.UserServiceImpl.UserStats;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;

import java.util.List;

public interface UserService {

    UserResponse registerUser(RegisterRequest registerRequest);

    UserResponse getUserById(Long id);

    UserResponse getUserByUsername(String username);

    UserResponse getCurrentUser();

    UserResponse updateUser(Long id, UpdateUserRequest updateRequest);

    void changePassword(Long id, String oldPassword, String newPassword);

    void deleteUser(Long id);

    Page<UserResponse> getAllUsers(Pageable pageable);

    List<UserResponse> searchUsers(Specification<User> spec);

    UserResponse updateUserStatus(Long id, User.UserStatus status); // Используем внутренний enum

    void updateLastLogin(String username);

    void incrementFailedLoginAttempts(String username);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    UserStats getUsersStats();

    UserResponse updateUserProfile(Long id, UserProfileRequest profileRequest);

    UserResponse getUserByEmail(String email);
}