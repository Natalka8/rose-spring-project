package org.example.entity;

import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener.class)
@ToString(exclude = {"password"}) // LEAVE ONLY EXISTING FIELDS
public class User {

    public enum UserStatus {
        PENDING,       // Awaiting email confirmation
        ACTIVE,        // Active user
        INACTIVE,      // Inactive (user disabled)
        SUSPENDED,     // Temporary suspension (violations)
        BANNED,        // Permanent ban ← ADDED
        DELETED        // Deleted account
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false, length = 50)
    private String username;

    @Column(unique = true, nullable = false, length = 100)
    private String email;

    @Column(nullable = false, length = 255)
    private String password;

    @Column(name = "first_name", length = 50)
    private String firstName;

    @Column(name = "last_name", length = 50)
    private String lastName;

    @Column(columnDefinition = "TEXT")
    private String bio;

    @Enumerated(EnumType.STRING)
    @Column(length = 20)
    @Builder.Default
    private UserStatus status = UserStatus.ACTIVE;

    @Column(nullable = false)
    @Builder.Default
    private boolean enabled = true;

    @Column(name = "account_non_expired", nullable = false)
    @Builder.Default
    private boolean accountNonExpired = true;

    @Column(name = "account_non_locked", nullable = false)
    @Builder.Default
    private boolean accountNonLocked = true;

    @Column(name = "credentials_non_expired", nullable = false)
    @Builder.Default
    private boolean credentialsNonExpired = true;

    @Column(name = "last_login_at")
    private LocalDateTime lastLoginAt;

    @Column(name = "login_attempts")
    @Builder.Default
    private int loginAttempts = 0;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "role")
    @Builder.Default
    private Set<String> roles = new HashSet<>();

    @CreatedDate
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    // Helper methods
    public void addRole(String role) {
        if (roles == null) {
            roles = new HashSet<>();
        }
        this.roles.add(role);
    }

    public void removeRole(String role) {
        if (roles != null) {
            this.roles.remove(role);
        }
    }

    public boolean hasRole(String role) {
        return roles != null && this.roles.contains(role);
    }

    public void incrementLoginAttempts() {
        this.loginAttempts++;
    }

    public void resetLoginAttempts() {
        this.loginAttempts = 0;
    }

    public void lockAccount() {
        this.accountNonLocked = false;
        this.enabled = false;
        this.status = UserStatus.SUSPENDED;
    }

    public void unlockAccount() {
        this.accountNonLocked = true;
        this.enabled = true;
        this.loginAttempts = 0;
        this.status = UserStatus.ACTIVE;
    }

    public void banAccount() {
        this.accountNonLocked = false;
        this.enabled = false;
        this.status = UserStatus.BANNED;
    }

    public boolean canLogin() {
        return enabled && accountNonExpired && accountNonLocked && credentialsNonExpired &&
                (status == UserStatus.ACTIVE || status == UserStatus.PENDING);
    }
}