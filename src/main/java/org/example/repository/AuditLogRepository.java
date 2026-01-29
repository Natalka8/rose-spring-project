package org.example.repository;

import org.example.entity.AuditLog;
import org.example.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface AuditLogRepository extends JpaRepository<AuditLog, Long> {

    /**
     * Search records by user
     */
    Page<AuditLog> findByUser(User user, Pageable pageable);

    /**
     * Search records by user ID
     */
    Page<AuditLog> findByUserId(Long userId, Pageable pageable);

    /**
     * Search records by action
     */
    Page<AuditLog> findByActionContaining(String action, Pageable pageable);

    /**
     * Search records by IP address
     */
    List<AuditLog> findByIpAddress(String ipAddress);

    /**
     * Extended filter with pagination support
     */
    @Query("SELECT a FROM AuditLog a WHERE " +
            "(:userId IS NULL OR a.user.id = :userId) AND " +
            "(:action IS NULL OR a.action LIKE %:action%) AND " +
            "(:ipAddress IS NULL OR a.ipAddress = :ipAddress) AND " +
            "(:startDate IS NULL OR a.createdAt >= :startDate) AND " +
            "(:endDate IS NULL OR a.createdAt <= :endDate) " +
            "ORDER BY a.createdAt DESC")
    Page<AuditLog> findFilteredLogs(@Param("userId") Long userId,
                                    @Param("action") String action,
                                    @Param("ipAddress") String ipAddress,
                                    @Param("startDate") LocalDateTime startDate,
                                    @Param("endDate") LocalDateTime endDate,
                                    Pageable pageable);

    /**
     * Record count for period
     */
    @Query("SELECT COUNT(a) FROM AuditLog a WHERE a.createdAt >= :startDate AND a.createdAt <= :endDate")
    long countByPeriod(@Param("startDate") LocalDateTime startDate,
                       @Param("endDate") LocalDateTime endDate);

    /**
     * Action statistics for period
     */
    @Query("SELECT a.action, COUNT(a) FROM AuditLog a " +
            "WHERE a.createdAt >= :startDate AND a.createdAt <= :endDate " +
            "GROUP BY a.action " +
            "ORDER BY COUNT(a) DESC")
    List<Object[]> getActionStatistics(@Param("startDate") LocalDateTime startDate,
                                       @Param("endDate") LocalDateTime endDate);

    /**
     * Delete old records (e.g., older than N days)
     */
    @Query("DELETE FROM AuditLog a WHERE a.createdAt < :date")
    void deleteOldLogs(@Param("date") LocalDateTime date);

    /**
     * Last N records for user
     */
    @Query("SELECT a FROM AuditLog a WHERE a.user.id = :userId " +
            "ORDER BY a.createdAt DESC")
    List<AuditLog> findRecentByUserId(@Param("userId") Long userId,
                                      Pageable pageable);

    /**
     * Search by User-Agent
     */
    @Query("SELECT a FROM AuditLog a WHERE a.userAgent LIKE %:userAgent%")
    List<AuditLog> findByUserAgentContaining(@Param("userAgent") String userAgent);
}