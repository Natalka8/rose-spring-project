package org.example.repository;

import org.example.entity.AchievementTemplate;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AchievementTemplateRepository extends JpaRepository<AchievementTemplate, Long> {

    // ========== METHODS FOR EXISTING FIELDS ==========

    // Search by achievementId (unique code)
    Optional<AchievementTemplate> findByAchievementId(String achievementId);

    // Search by name
    List<AchievementTemplate> findByName(String name);
    List<AchievementTemplate> findByNameContainingIgnoreCase(String name);

    // Search by category
    List<AchievementTemplate> findByCategory(String category);

    // Search by type
    List<AchievementTemplate> findByType(String type);

    // Search by reward type
    List<AchievementTemplate> findByRewardType(String rewardType);

    // Active/inactive achievements
    List<AchievementTemplate> findByIsActiveTrue();
    List<AchievementTemplate> findByIsActiveFalse();
    List<AchievementTemplate> findByIsActive(Boolean isActive);

    // Secret/non-secret achievements
    List<AchievementTemplate> findByIsSecretTrue();
    List<AchievementTemplate> findByIsSecretFalse();
    List<AchievementTemplate> findByIsSecret(Boolean isSecret);

    // Repeatable/non-repeatable achievements
    List<AchievementTemplate> findByIsRepeatableTrue();
    List<AchievementTemplate> findByIsRepeatableFalse();
    List<AchievementTemplate> findByIsRepeatable(Boolean isRepeatable);

    // Search by required level
    List<AchievementTemplate> findByRequiredLevel(Integer requiredLevel);
    List<AchievementTemplate> findByRequiredLevelLessThanEqual(Integer level);
    List<AchievementTemplate> findByRequiredLevelGreaterThanEqual(Integer level);
    List<AchievementTemplate> findByRequiredLevelBetween(Integer minLevel, Integer maxLevel);

    // Search by points
    List<AchievementTemplate> findByPoints(Integer points);
    List<AchievementTemplate> findByPointsGreaterThan(Integer points);
    List<AchievementTemplate> findByPointsLessThan(Integer points);
    List<AchievementTemplate> findByPointsBetween(Integer minPoints, Integer maxPoints);

    // Search by goal
    List<AchievementTemplate> findByGoal(Integer goal);
    List<AchievementTemplate> findByGoalGreaterThan(Integer goal);
    List<AchievementTemplate> findByGoalLessThan(Integer goal);

    // Search by reward value
    List<AchievementTemplate> findByRewardValue(Integer rewardValue);
    List<AchievementTemplate> findByRewardValueGreaterThan(Integer rewardValue);
    List<AchievementTemplate> findByRewardValueLessThan(Integer rewardValue);

    // Search by class
    List<AchievementTemplate> findByClassRestriction(String classRestriction);

    // Search by faction
    List<AchievementTemplate> findByFactionRestriction(String factionRestriction);

    // Search by prerequisite achievement ID
    List<AchievementTemplate> findByPrerequisiteId(String prerequisiteId);

    // Search by maximum completion count
    List<AchievementTemplate> findByMaxCompletions(Integer maxCompletions);
    List<AchievementTemplate> findByMaxCompletionsGreaterThan(Integer completions);

    // Search by cooldown time
    List<AchievementTemplate> findByCooldownMinutes(Integer cooldownMinutes);
    List<AchievementTemplate> findByCooldownMinutesGreaterThan(Integer minutes);

    // Existence check
    boolean existsByAchievementId(String achievementId);

    // ========== COMBINED METHODS ==========

    List<AchievementTemplate> findByCategoryAndIsActiveTrue(String category);
    List<AchievementTemplate> findByTypeAndIsActiveTrue(String type);
    List<AchievementTemplate> findByCategoryAndType(String category, String type);
    List<AchievementTemplate> findByIsActiveTrueAndIsSecretFalse();

    // ========== JPQL QUERIES ==========

    @Query("SELECT a FROM AchievementTemplate a WHERE " +
            "(:name IS NULL OR LOWER(a.name) LIKE LOWER(CONCAT('%', :name, '%'))) AND " +
            "(:category IS NULL OR a.category = :category) AND " +
            "(:type IS NULL OR a.type = :type) AND " +
            "(:minPoints IS NULL OR a.points >= :minPoints) AND " +
            "(:isActive IS NULL OR a.isActive = :isActive) " +
            "ORDER BY a.points DESC")
    List<AchievementTemplate> findWithFilters(
            @Param("name") String name,
            @Param("category") String category,
            @Param("type") String type,
            @Param("minPoints") Integer minPoints,
            @Param("isActive") Boolean isActive);

    @Query("SELECT a FROM AchievementTemplate a WHERE " +
            "a.requiredLevel <= :playerLevel AND " +
            "(a.classRestriction IS NULL OR a.classRestriction = :playerClass) AND " +
            "(a.factionRestriction IS NULL OR a.factionRestriction = :playerFaction) AND " +
            "a.isActive = true " +
            "ORDER BY a.points DESC")
    List<AchievementTemplate> findAvailableForPlayer(
            @Param("playerLevel") Integer playerLevel,
            @Param("playerClass") String playerClass,
            @Param("playerFaction") String playerFaction);

    @Query("SELECT DISTINCT a.category FROM AchievementTemplate a WHERE a.category IS NOT NULL ORDER BY a.category")
    List<String> findAllCategories();

    @Query("SELECT DISTINCT a.type FROM AchievementTemplate a WHERE a.type IS NOT NULL ORDER BY a.type")
    List<String> findAllTypes();

    @Query("SELECT DISTINCT a.rewardType FROM AchievementTemplate a WHERE a.rewardType IS NOT NULL ORDER BY a.rewardType")
    List<String> findAllRewardTypes();

    @Query("SELECT a.category, COUNT(a), SUM(a.points) FROM AchievementTemplate a " +
            "WHERE a.isActive = true GROUP BY a.category ORDER BY COUNT(a) DESC")
    List<Object[]> getCategoryStatistics();

    @Query("SELECT a.type, COUNT(a) FROM AchievementTemplate a WHERE a.isActive = true GROUP BY a.type")
    List<Object[]> getTypeStatistics();

    // ========== STATISTICAL METHODS ==========

    @Query("SELECT COUNT(a) FROM AchievementTemplate a WHERE a.isActive = true")
    long countActive();

    @Query("SELECT COUNT(a) FROM AchievementTemplate a WHERE a.isSecret = true")
    long countSecret();

    @Query("SELECT COUNT(a) FROM AchievementTemplate a WHERE a.isRepeatable = true")
    long countRepeatable();

    @Query("SELECT AVG(a.points) FROM AchievementTemplate a WHERE a.isActive = true")
    Double getAveragePoints();

    @Query("SELECT MAX(a.points) FROM AchievementTemplate a WHERE a.isActive = true")
    Integer getMaxPoints();

    @Query("SELECT MIN(a.points) FROM AchievementTemplate a WHERE a.isActive = true")
    Integer getMinPoints();

    // ========== METHODS WITH PAGINATION ==========

    Page<AchievementTemplate> findByIsActiveTrue(Pageable pageable);

    Page<AchievementTemplate> findByCategory(String category, Pageable pageable);

    Page<AchievementTemplate> findByType(String type, Pageable pageable);

    @Query("SELECT a FROM AchievementTemplate a WHERE a.isActive = true AND a.isSecret = false ORDER BY a.points DESC")
    Page<AchievementTemplate> findPublicAchievements(Pageable pageable);

    // ========== METHODS FOR ADMINISTRATIVE TASKS ==========

    @Query("SELECT a FROM AchievementTemplate a WHERE a.isActive = false")
    List<AchievementTemplate> findAllInactive();

    @Query("SELECT a FROM AchievementTemplate a WHERE a.prerequisiteId = :achievementId")
    List<AchievementTemplate> findDependentAchievements(@Param("achievementId") String achievementId);

    // Search for achievements without prerequisites
    @Query("SELECT a FROM AchievementTemplate a WHERE a.prerequisiteId IS NULL OR a.prerequisiteId = ''")
    List<AchievementTemplate> findStartingAchievements();

    // ========== METHODS FOR INTEGRITY CHECKS ==========

    @Query("SELECT a FROM AchievementTemplate a WHERE a.prerequisiteId IS NOT NULL AND a.prerequisiteId != '' " +
            "AND NOT EXISTS (SELECT a2 FROM AchievementTemplate a2 WHERE a2.achievementId = a.prerequisiteId)")
    List<AchievementTemplate> findAchievementsWithMissingPrerequisites();
}