package org.example.security;

import org.example.entity.User;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.stream.Collectors;

public class CustomUserDetails implements UserDetails {

    private final User user;

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return user.getRoles().stream()
                .map(role -> {
                    String roleName = role.startsWith("ROLE_") ? role : "ROLE_" + role;
                    return new SimpleGrantedAuthority(roleName);
                })
                .collect(Collectors.toSet());
    }

    @Override
    public String getPassword() {
        return user.getPassword();
    }

    @Override
    public String getUsername() {
        return user.getUsername();
    }

    @Override
    public boolean isAccountNonExpired() {
        return user.isAccountNonExpired();
    }

    @Override
    public boolean isAccountNonLocked() {
        return user.isAccountNonLocked();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return user.isCredentialsNonExpired();
    }

    @Override
    public boolean isEnabled() {
        return user.isEnabled();
    }

    // Additional methods for accessing user data

    public Long getId() {
        return user.getId();
    }

    public String getEmail() {
        return user.getEmail();
    }

    public User getUser() {
        return user;
    }

    public String getFirstName() {
        return user.getFirstName();
    }

    public String getLastName() {
        return user.getLastName();
    }

    public String getBio() {
        return user.getBio();
    }

    // FIXED: using internal enum User.UserStatus
    public User.UserStatus getStatus() {
        return user.getStatus();
    }

    public int getLoginAttempts() {
        return user.getLoginAttempts();
    }

    public LocalDateTime getLastLoginAt() {
        return user.getLastLoginAt();
    }

    public LocalDateTime getCreatedAt() {
        return user.getCreatedAt();
    }

    public LocalDateTime getUpdatedAt() {
        return user.getUpdatedAt();
    }

    public LocalDateTime getDeletedAt() {
        return user.getDeletedAt();
    }

    // Role check methods

    public boolean hasRole(String role) {
        return user.hasRole(role);
    }

    public boolean hasAnyRole(String... roles) {
        for (String role : roles) {
            if (user.hasRole(role)) {
                return true;
            }
        }
        return false;
    }

    public boolean hasAuthority(String authority) {
        return getAuthorities().stream()
                .anyMatch(auth -> auth.getAuthority().equals(authority));
    }

    // Method to get full name
    public String getFullName() {
        if (user.getFirstName() != null && user.getLastName() != null) {
            return user.getFirstName() + " " + user.getLastName();
        } else if (user.getFirstName() != null) {
            return user.getFirstName();
        } else {
            return user.getUsername();
        }
    }
}