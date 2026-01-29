package org.example.repository;

import org.example.entity.User;
import org.example.entity.UserAchievement;
import org.example.entity.Achievement;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface UserAchievementRepository extends JpaRepository<UserAchievement, Long> {

    // ========== BASIC SEARCH METHODS ==========

    // By user
    List<UserAchievement> findByUser(User user);
    List<UserAchievement> findByUser_Id(Long userId);

    // By achievement
    List<UserAchievement> findByAchievement(Achievement achievement);
    List<UserAchievement> findByAchievement_Id(Long achievementId);

    // Existence check
    boolean existsByUser_IdAndAchievement_Id(Long userId, Long achievementId);

    // Get specific record
    UserAchievement findByUser_IdAndAchievement_Id(Long userId, Long achievementId);

    // ========== STATUS METHODS ==========

    List<UserAchievement> findByUser_IdAndCompletedTrue(Long userId);
    List<UserAchievement> findByUser_IdAndCompletedFalse(Long userId);
    List<UserAchievement> findByCompletedTrue();
    List<UserAchievement> findByCompletedFalse();

    // ========== PROGRESS METHODS ==========

    List<UserAchievement> findByUser_IdAndProgressGreaterThanEqual(Long userId, Integer minProgress);
    List<UserAchievement> findByUser_IdAndProgress(Long userId, Integer progress);

    // ========== TIME METHODS ==========

    List<UserAchievement> findByUnlockedAtBetween(LocalDateTime start, LocalDateTime end);
    List<UserAchievement> findByUnlockedAtAfter(LocalDateTime date);
    List<UserAchievement> findByCompletedAtBetween(LocalDate start, LocalDate end);
    List<UserAchievement> findByCompletedAtAfter(LocalDate date);

    // ========== REWARD METHODS ==========

    // Fixed methods using JPQL queries
    @Query("SELECT ua FROM UserAchievement ua WHERE ua.achievement.rewardExperience > :minExperience")
    List<UserAchievement> findByAchievementRewardExperienceGreaterThan(@Param("minExperience") Integer minExperience);

    @Query("SELECT ua FROM UserAchievement ua WHERE ua.achievement.rewardGold > :minGold")
    List<UserAchievement> findByAchievementRewardGoldGreaterThan(@Param("minGold") Integer minGold);

    // ========== SORTING METHODS ==========

    List<UserAchievement> findByUser_IdOrderByUnlockedAtDesc(Long userId);
    List<UserAchievement> findByUser_IdOrderByCompletedAtDesc(Long userId);

    // ========== STATISTICAL METHODS ==========

    Long countByUser_Id(Long userId);
    Long countByUser_IdAndCompletedTrue(Long userId);
    Long countByUnlockedAtAfter(LocalDateTime date);
    Long countByAchievement_Id(Long achievementId);

    // ========== JPQL QUERIES ==========

    // Date statistics
    @Query("SELECT DATE(ua.unlockedAt), COUNT(ua) FROM UserAchievement ua " +
            "WHERE ua.unlockedAt BETWEEN :startDate AND :endDate " +
            "AND ua.completed = true " +
            "GROUP BY DATE(ua.unlockedAt) " +
            "ORDER BY DATE(ua.unlockedAt)")
    List<Object[]> countAchievementsByDate(@Param("startDate") LocalDateTime startDate,
                                           @Param("endDate") LocalDateTime endDate);

    // User's completed achievements (newest first)
    @Query("SELECT ua FROM UserAchievement ua " +
            "WHERE ua.user.id = :userId " +
            "AND ua.completed = true " +
            "ORDER BY ua.completedAt DESC")
    List<UserAchievement> findCompletedByUserOrderByDateDesc(@Param("userId") Long userId);

    // Count completed achievements for period
    @Query("SELECT COUNT(ua) FROM UserAchievement ua " +
            "WHERE ua.completed = true " +
            "AND ua.completedAt BETWEEN :start AND :end")
    Long countCompletedBetween(@Param("start") LocalDate start,
                               @Param("end") LocalDate end);

    // Unique users with achievement
    @Query("SELECT COUNT(DISTINCT ua.user.id) FROM UserAchievement ua " +
            "WHERE ua.achievement.id = :achievementId " +
            "AND ua.completed = true")
    Long countUniqueUsersWithAchievement(@Param("achievementId") Long achievementId);

    // Top users by number of achievements
    @Query("SELECT ua.user.id, COUNT(ua) as achievementCount " +
            "FROM UserAchievement ua " +
            "WHERE ua.completed = true " +
            "GROUP BY ua.user.id " +
            "ORDER BY achievementCount DESC")
    List<Object[]> findTopUsersByAchievements(Pageable pageable);

    // ========== SEARCH METHODS BY NAME AND DESCRIPTION ==========

    // Search by achievement name (using achievementName)
    @Query("SELECT ua FROM UserAchievement ua WHERE ua.achievement.achievementName LIKE %:name%")
    List<UserAchievement> findByAchievementNameContaining(@Param("name") String name);

    // Search by display name
    @Query("SELECT ua FROM UserAchievement ua WHERE ua.achievement.displayName LIKE %:name%")
    List<UserAchievement> findByAchievementDisplayNameContaining(@Param("name") String name);

    // Search by description
    @Query("SELECT ua FROM UserAchievement ua WHERE ua.achievement.description LIKE %:description%")
    List<UserAchievement> findByAchievementDescriptionContaining(@Param("description") String description);

    // Search by achievement code
    @Query("SELECT ua FROM UserAchievement ua WHERE ua.achievement.achievementCode = :code")
    List<UserAchievement> findByAchievementCode(@Param("code") String code);

    // ========== CATEGORY METHODS ==========

    // Category statistics for user
    @Query("SELECT ua.achievement.category, COUNT(ua) FROM UserAchievement ua " +
            "WHERE ua.user.id = :userId " +
            "AND ua.completed = true " +
            "GROUP BY ua.achievement.category")
    List<Object[]> countByCategoryForUser(@Param("userId") Long userId);

    // Achievements by category
    @Query("SELECT ua FROM UserAchievement ua WHERE ua.achievement.category = :category")
    List<UserAchievement> findByCategory(@Param("category") String category);

    // User's achievements by category
    @Query("SELECT ua FROM UserAchievement ua WHERE ua.user.id = :userId AND ua.achievement.category = :category")
    List<UserAchievement> findByUserAndCategory(@Param("userId") Long userId,
                                                @Param("category") String category);

    // ========== TYPE AND DIFFICULTY METHODS ==========

    @Query("SELECT ua FROM UserAchievement ua WHERE ua.achievement.achievementType = :type")
    List<UserAchievement> findByAchievementType(@Param("type") String type);

    @Query("SELECT ua FROM UserAchievement ua WHERE ua.achievement.difficulty = :difficulty")
    List<UserAchievement> findByDifficulty(@Param("difficulty") String difficulty);

    // ========== UPDATE METHODS ==========

    @Modifying
    @Transactional
    @Query("UPDATE UserAchievement ua SET ua.progress = :progress WHERE ua.id = :id")
    int updateProgress(@Param("id") Long id, @Param("progress") Integer progress);

    @Modifying
    @Transactional
    @Query("UPDATE UserAchievement ua " +
            "SET ua.completed = true, " +
            "    ua.progress = 100, " +
            "    ua.completedAt = :completedAt, " +
            "    ua.unlockedAt = :unlockedAt " +
            "WHERE ua.id = :id")
    int completeAchievement(@Param("id") Long id,
                            @Param("completedAt") LocalDate completedAt,
                            @Param("unlockedAt") LocalDateTime unlockedAt);

    // ========== DELETE METHODS ==========

    void deleteByUser_Id(Long userId);
    void deleteByAchievement_Id(Long achievementId);

    // ========== COMBINED METHODS ==========

    List<UserAchievement> findByUserAndCompletedAtAfter(User user, LocalDate date);

    // Alternative method for findByUser_Id
    @Query("SELECT ua FROM UserAchievement ua WHERE ua.user.id = :userId")
    List<UserAchievement> findByUserId(@Param("userId") Long userId);

    // ========== ADDITIONAL ANALYTICS METHODS ==========

    // Achievement count by type for user
    @Query("SELECT ua.achievement.achievementType, COUNT(ua) FROM UserAchievement ua " +
            "WHERE ua.user.id = :userId " +
            "AND ua.completed = true " +
            "GROUP BY ua.achievement.achievementType")
    List<Object[]> countByTypeForUser(@Param("userId") Long userId);

    // User's secret achievements
    @Query("SELECT ua FROM UserAchievement ua WHERE ua.user.id = :userId AND ua.achievement.isSecret = true")
    List<UserAchievement> findSecretAchievementsByUser(@Param("userId") Long userId);

    // Featured achievements
    @Query("SELECT ua FROM UserAchievement ua WHERE ua.achievement.isFeatured = true")
    List<UserAchievement> findFeaturedAchievements();

    // Achievements by rewards
    @Query("SELECT ua FROM UserAchievement ua WHERE ua.achievement.rewardPoints >= :minPoints")
    List<UserAchievement> findByMinRewardPoints(@Param("minPoints") Integer minPoints);
}