package org.example.repository;

import org.example.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String email);

    boolean existsByUsername(String username);

    boolean existsByEmail(String email);

    // New methods used in the service
    Page<User> findAllByEnabledTrue(Pageable pageable);

    long countByStatus(User.UserStatus status); // Using internal enum

    long countByCreatedAtAfter(LocalDateTime dateTime);

    // Can add @Query for more complex queries
    @Query("SELECT u FROM User u WHERE u.enabled = true AND u.accountNonLocked = true")
    Page<User> findActiveUsers(Pageable pageable);

    @Query("SELECT COUNT(u) FROM User u WHERE u.createdAt >= :startDate AND u.createdAt < :endDate")
    long countByCreatedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}