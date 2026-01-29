package org.example.repository;

import org.example.entity.GameAction;
import org.example.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface GameActionRepository extends JpaRepository<GameAction, Long> {

    // === Basic search methods ===
    List<GameAction> findByUser(User user);
    List<GameAction> findByUserId(Long userId);
    List<GameAction> findByActionType(String actionType);
    List<GameAction> findByLocationId(Long locationId);
    List<GameAction> findByStatus(String status);

    // === Methods with pagination ===
    Page<GameAction> findByUserId(Long userId, Pageable pageable);
    Page<GameAction> findByActionType(String actionType, Pageable pageable);
    Page<GameAction> findByStatus(String status, Pageable pageable);

    // === Methods with time range ===
    List<GameAction> findByActionTimeAfter(LocalDateTime after);
    List<GameAction> findByActionTimeBetween(LocalDateTime start, LocalDateTime end);

    // === Combined methods ===
    List<GameAction> findByUserIdAndActionType(Long userId, String actionType);
    List<GameAction> findByUserIdAndActionTimeAfter(Long userId, LocalDateTime after);
    List<GameAction> findByActionTypeAndActionTimeBetween(String actionType,
                                                          LocalDateTime start,
                                                          LocalDateTime end);

    // === Methods with experience and gold ===
    List<GameAction> findByExperienceGainedGreaterThan(Integer minExperience);
    List<GameAction> findByGoldGainedGreaterThan(Integer minGold);

    // === Methods with sorting ===
    List<GameAction> findByUserIdOrderByActionTimeDesc(Long userId);
    List<GameAction> findByUserIdAndActionTimeAfterOrderByActionTimeAsc(Long userId, LocalDateTime after);
    List<GameAction> findAllByOrderByActionTimeDesc();

    // === Statistical methods ===
    @Query("SELECT COUNT(a) FROM GameAction a WHERE a.user.id = :userId")
    long countByUserId(@Param("userId") Long userId);

    @Query("SELECT COUNT(a) FROM GameAction a WHERE a.actionTime >= :after")
    long countByActionTimeAfter(@Param("after") LocalDateTime after);

    @Query("SELECT COUNT(DISTINCT a.user.id) FROM GameAction a WHERE a.actionTime BETWEEN :start AND :end")
    Long countUniqueUsersBetween(@Param("start") LocalDateTime start,
                                 @Param("end") LocalDateTime end);

    // === Grouped queries ===
    @Query("SELECT DISTINCT a.user.id FROM GameAction a WHERE a.actionTime >= :after")
    List<Long> findDistinctUserIdsAfter(@Param("after") LocalDateTime after);

    @Query("SELECT a.actionType, COUNT(a) FROM GameAction a WHERE a.user.id = :userId GROUP BY a.actionType")
    List<Object[]> countByActionTypeForUser(@Param("userId") Long userId);

    @Query("SELECT DATE(a.actionTime), COUNT(a) FROM GameAction a " +
            "WHERE a.actionTime BETWEEN :start AND :end " +
            "GROUP BY DATE(a.actionTime) " +
            "ORDER BY DATE(a.actionTime)")
    List<Object[]> countActionsByDate(@Param("start") LocalDateTime start,
                                      @Param("end") LocalDateTime end);

    @Query("SELECT a.actionType, COUNT(a) FROM GameAction a " +
            "WHERE a.actionTime BETWEEN :start AND :end " +
            "GROUP BY a.actionType")
    List<Object[]> countActionsByType(@Param("start") LocalDateTime start,
                                      @Param("end") LocalDateTime end);


    @Query("SELECT a.user.id, COUNT(a) as actionCount " +
            "FROM GameAction a " +
            "WHERE a.actionTime >= :after " +
            "GROUP BY a.user.id " +
            "ORDER BY actionCount DESC")
    List<Object[]> getTopActiveUsers(@Param("after") LocalDateTime after);

    // === Special methods ===
    @Query("SELECT a FROM GameAction a WHERE a.user.id = :userId AND DATE(a.actionTime) = CURRENT_DATE")
    List<GameAction> findTodayByUserId(@Param("userId") Long userId);

    // === Universal search method with filters and sorting ===
    @Query("SELECT a FROM GameAction a WHERE " +
            "(:userId IS NULL OR a.user.id = :userId) AND " +
            "(:actionType IS NULL OR a.actionType = :actionType) AND " +
            "(:startDate IS NULL OR a.actionTime >= :startDate) AND " +
            "(:endDate IS NULL OR a.actionTime <= :endDate)")
    Page<GameAction> findActionsWithFilters(@Param("userId") Long userId,
                                            @Param("actionType") String actionType,
                                            @Param("startDate") LocalDateTime startDate,
                                            @Param("endDate") LocalDateTime endDate,
                                            Pageable pageable);
}