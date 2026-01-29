package org.example.repository;

import org.example.entity.GameSession;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GameSessionRepository extends JpaRepository<GameSession, Long> {

    // ========== BASIC SEARCH METHODS ==========

    Optional<GameSession> findByUser_IdAndEndTimeIsNull(Long userId);
    List<GameSession> findByUser_Id(Long userId);
    List<GameSession> findByStatus(GameSession.Status status);
    List<GameSession> findByStartTimeAfter(LocalDateTime startTime);
    List<GameSession> findByStartTimeBetween(LocalDateTime start, LocalDateTime end);
    List<GameSession> findByEndTimeIsNull();
    List<GameSession> findByEndTimeIsNotNull();

    // ========== SIMPLE JPQL QUERIES ==========

    @Query("SELECT gs FROM GameSession gs WHERE gs.user.id = :userId ORDER BY gs.startTime DESC")
    List<GameSession> findUserSessionsOrdered(@Param("userId") Long userId);

    @Query("SELECT gs FROM GameSession gs WHERE gs.status = 'ACTIVE' AND gs.startTime < :cutoffTime")
    List<GameSession> findStaleActiveSessions(@Param("cutoffTime") LocalDateTime cutoffTime);

    @Query("SELECT COUNT(gs) FROM GameSession gs WHERE gs.status = 'ACTIVE'")
    long countActiveSessions();

    @Query("SELECT gs FROM GameSession gs WHERE gs.user.id = :userId AND gs.status = 'ACTIVE'")
    Optional<GameSession> findActiveSessionByUserId(@Param("userId") Long userId);

    @Query("SELECT gs FROM GameSession gs WHERE gs.ipAddress = :ipAddress AND gs.startTime > :since")
    List<GameSession> findByIpAddressSince(@Param("ipAddress") String ipAddress,
                                           @Param("since") LocalDateTime since);

    @Query("SELECT DISTINCT gs.user.id FROM GameSession gs WHERE gs.startTime > :since")
    List<Long> findActiveUserIdsSince(@Param("since") LocalDateTime since);

    @Query("SELECT COUNT(gs) FROM GameSession gs WHERE gs.user.id = :userId")
    long countSessionsByUserId(@Param("userId") Long userId);

    @Query("SELECT gs FROM GameSession gs WHERE gs.user.id = :userId " +
            "AND gs.startTime >= :startDate AND gs.startTime < :endDate " +
            "ORDER BY gs.startTime DESC")
    List<GameSession> findByUserIdAndDateRange(@Param("userId") Long userId,
                                               @Param("startDate") LocalDateTime startDate,
                                               @Param("endDate") LocalDateTime endDate);

    @Query("SELECT gs.user.id, COUNT(gs) as sessionCount FROM GameSession gs " +
            "WHERE gs.startTime >= :startDate " +
            "GROUP BY gs.user.id " +
            "ORDER BY sessionCount DESC")
    List<Object[]> getTopUsersBySessionCount(@Param("startDate") LocalDateTime startDate);

    // ========== METHODS WITH PAGINATION ==========

    @Query("SELECT gs FROM GameSession gs WHERE gs.user.id = :userId")
    List<GameSession> findByUserIdWithPagination(@Param("userId") Long userId, Pageable pageable);

    @Query("SELECT gs FROM GameSession gs ORDER BY gs.startTime DESC")
    List<GameSession> findAllOrderedByStartTime(Pageable pageable);

    @Query("SELECT gs FROM GameSession gs WHERE gs.status = 'ACTIVE' ORDER BY gs.startTime DESC")
    List<GameSession> findActiveSessionsOrdered(Pageable pageable);
}