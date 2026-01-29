package org.example.repository;

import org.example.entity.Achievement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AchievementRepository extends JpaRepository<Achievement, Long> {

    // Basic search methods by code and name
    Optional<Achievement> findByAchievementCode(String achievementCode);
    Optional<Achievement> findByAchievementName(String achievementName);
    List<Achievement> findByDisplayName(String displayName);

    // Search by achievement type
    List<Achievement> findByAchievementType(String achievementType);

    // Search by category
    List<Achievement> findByCategory(String category);
    List<Achievement> findByCategoryAndSubcategory(String category, String subcategory);

    // Search by difficulty
    List<Achievement> findByDifficulty(String difficulty);

    // Search for active achievements
    List<Achievement> findByIsActive(Boolean isActive);

    // Search for secret achievements
    List<Achievement> findByIsSecret(Boolean isSecret);

    // Search for hidden achievements
    List<Achievement> findByIsHidden(Boolean isHidden);

    // Search by reward (points)
    List<Achievement> findByRewardPointsGreaterThanEqual(Integer points);
    List<Achievement> findByRewardPointsBetween(Integer minPoints, Integer maxPoints);

    // Search by required points
    List<Achievement> findByPointsRequiredLessThanEqual(Integer points);

    // Search by presence of reward
    List<Achievement> findByRewardTitleIsNotNull();
    List<Achievement> findByRewardBadgeIsNotNull();

    // Search for featured achievements
    List<Achievement> findByIsFeatured(Boolean isFeatured);

    // Search by version
    List<Achievement> findByVersion(Integer version);

    // Search by partial name/description
    List<Achievement> findByAchievementNameContainingIgnoreCase(String namePart);
    List<Achievement> findByDisplayNameContainingIgnoreCase(String namePart);
    List<Achievement> findByDescriptionContainingIgnoreCase(String descriptionPart);

    // Complex queries

    @Query("SELECT a FROM Achievement a WHERE a.category = :category AND a.difficulty = :difficulty")
    List<Achievement> findByCategoryAndDifficulty(@Param("category") String category,
                                                  @Param("difficulty") String difficulty);

    @Query("SELECT a FROM Achievement a WHERE a.rewardPoints >= :minPoints AND a.isActive = true")
    List<Achievement> findActiveByMinRewardPoints(@Param("minPoints") Integer minPoints);

    @Query("SELECT a FROM Achievement a WHERE a.pointsRequired <= :maxPoints AND a.isActive = true")
    List<Achievement> findAvailableByMaxPointsRequired(@Param("maxPoints") Integer maxPoints);

    // Get achievements with maximum reward
    @Query("SELECT a FROM Achievement a WHERE a.isActive = true ORDER BY a.rewardPoints DESC")
    List<Achievement> findTopByRewardPoints();

    // Get most difficult achievements
    @Query("SELECT a FROM Achievement a WHERE a.isActive = true ORDER BY a.pointsRequired DESC")
    List<Achievement> findHardestAchievements();

    // Get featured achievements
    @Query("SELECT a FROM Achievement a WHERE a.isFeatured = true AND a.isActive = true")
    List<Achievement> findFeaturedAchievements();

    // Count achievements by category
    @Query("SELECT a.category, COUNT(a) FROM Achievement a WHERE a.isActive = true GROUP BY a.category")
    List<Object[]> countByCategory();

    // Count achievements by type
    @Query("SELECT a.achievementType, COUNT(a) FROM Achievement a WHERE a.isActive = true GROUP BY a.achievementType")
    List<Object[]> countByAchievementType();

    // Existence check
    boolean existsByAchievementCode(String achievementCode);
    boolean existsByAchievementName(String achievementName);

    // Deletion
    void deleteByAchievementCode(String achievementCode);

    // Get achievements by predecessor ID
    @Query("SELECT a FROM Achievement a WHERE a.prerequisiteAchievementId = :prerequisiteId")
    List<Achievement> findByPrerequisiteId(@Param("prerequisiteId") Long prerequisiteId);

    // Get achievement chains
    @Query("SELECT a FROM Achievement a WHERE a.prerequisiteAchievementId IS NULL AND a.isActive = true")
    List<Achievement> findBaseAchievements();

    // Search by multiple types
    @Query("SELECT a FROM Achievement a WHERE a.achievementType IN :types AND a.isActive = true")
    List<Achievement> findByTypes(@Param("types") List<String> types);

    // Search for achievements with title reward
    @Query("SELECT a FROM Achievement a WHERE a.rewardTitle IS NOT NULL AND a.rewardTitle != ''")
    List<Achievement> findWithTitleReward();

    // Get distinct categories
    @Query("SELECT DISTINCT a.category FROM Achievement a WHERE a.category IS NOT NULL")
    List<String> findDistinctCategories();

    // Get distinct types
    @Query("SELECT DISTINCT a.achievementType FROM Achievement a WHERE a.achievementType IS NOT NULL")
    List<String> findDistinctAchievementTypes();
}