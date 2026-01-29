package org.example.repository;

import org.example.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {

    // ========== BASIC SEARCHES ==========

    // Fixed: findByActiveTrue() instead of findByIsActiveTrue()
    List<Location> findByActiveTrue();

    List<Location> findByLocationType(Location.LocationType locationType);

    List<Location> findByZoneType(Location.ZoneType zoneType);

    List<Location> findByRequiredLevelLessThanEqual(Integer level);

    List<Location> findByVisitCountGreaterThan(Long minVisits);

    // Existence check
    boolean existsByTitle(String title);

    // Search by difficulty
    List<Location> findByDifficulty(Location.Difficulty difficulty);

    List<Location> findByRequiredLevelBetween(Integer min, Integer max);

    // Search by coordinates
    List<Location> findByCoordinateXAndCoordinateY(Integer x, Integer y);

    List<Location> findByCoordinateXAndCoordinateYAndCoordinateZ(Integer x, Integer y, Integer z);

    // Search by status
    List<Location> findByStatus(Location.LocationStatus status);

    // Search by unlocked status
    List<Location> findByIsUnlocked(Boolean isUnlocked);

    // Search by title
    Optional<Location> findByTitle(String title);

    // Search by title (case-insensitive)
    Optional<Location> findByTitleIgnoreCase(String title);

    List<Location> findByTitleContainingIgnoreCase(String title);

    // ========== CUSTOM QUERIES ==========

    @Query("SELECT l FROM Location l WHERE l.status = org.example.entity.Location.LocationStatus.ACTIVE AND l.isUnlocked = true")
    List<Location> findActiveAndUnlockedLocations();

    @Query("SELECT l FROM Location l WHERE LOWER(l.title) LIKE LOWER(CONCAT('%', :query, '%')) OR LOWER(l.description) LIKE LOWER(CONCAT('%', :query, '%'))")
    List<Location> searchByTitleOrDescription(@Param("query") String query);

    @Query("SELECT l FROM Location l WHERE l.requiredLevel <= :level AND l.status = org.example.entity.Location.LocationStatus.ACTIVE ORDER BY l.requiredLevel ASC, l.title ASC")
    List<Location> findAccessibleLocations(@Param("level") Integer level);

    // Enhanced method with faction and class checks
    @Query("SELECT l FROM Location l WHERE " +
            "l.status = org.example.entity.Location.LocationStatus.ACTIVE AND " +
            "l.isUnlocked = true AND " +
            "(l.requiredLevel IS NULL OR l.requiredLevel <= :playerLevel) AND " +
            "(l.minLevel IS NULL OR l.minLevel <= :playerLevel) AND " +
            "(l.maxLevel IS NULL OR l.maxLevel >= :playerLevel) AND " +
            "(l.requiredFaction IS NULL OR l.requiredFaction = '' OR l.requiredFaction = :faction OR l.requiredFaction = 'NEUTRAL') AND " +
            "(l.factionRestriction IS NULL OR l.factionRestriction = '' OR l.factionRestriction = 'ANY' OR l.factionRestriction = :faction) AND " +
            "(l.classRestriction IS NULL OR l.classRestriction = '' OR l.classRestriction = 'ANY' OR l.classRestriction = :playerClass)")
    List<Location> findAccessibleLocationsForPlayer(
            @Param("playerLevel") Integer playerLevel,
            @Param("faction") String faction,
            @Param("playerClass") String playerClass);

    @Query("SELECT l FROM Location l ORDER BY l.visitCount DESC")
    List<Location> findMostVisitedLocations();

    @Query("SELECT l FROM Location l WHERE l.status = org.example.entity.Location.LocationStatus.ACTIVE ORDER BY l.requiredLevel ASC")
    List<Location> findAllActiveSortedByLevel();

    @Query("SELECT l FROM Location l WHERE l.coordinateX IS NOT NULL AND l.coordinateY IS NOT NULL")
    List<Location> findAllWithCoordinates();

    // Search locations for specific player type
    @Query("SELECT l FROM Location l WHERE " +
            "l.status = org.example.entity.Location.LocationStatus.ACTIVE AND " +
            "l.isUnlocked = true AND " +
            "(:isSafeZone IS NULL OR l.isSafeZone = :isSafeZone) AND " +
            "(:isPvpEnabled IS NULL OR l.isPvpEnabled = :isPvpEnabled) AND " +
            "(:isInstance IS NULL OR l.isInstance = :isInstance)")
    List<Location> findLocationsByProperties(
            @Param("isSafeZone") Boolean isSafeZone,
            @Param("isPvpEnabled") Boolean isPvpEnabled,
            @Param("isInstance") Boolean isInstance);

    // ========== STATISTICAL QUERIES ==========

    @Query("SELECT COUNT(l) FROM Location l WHERE l.status = org.example.entity.Location.LocationStatus.ACTIVE")
    long countActiveLocations();

    @Query("SELECT COUNT(l) FROM Location l WHERE l.status = org.example.entity.Location.LocationStatus.ACTIVE AND l.isUnlocked = true")
    long countActiveAndUnlockedLocations();

    @Query("SELECT l.difficulty, COUNT(l) FROM Location l WHERE l.status = org.example.entity.Location.LocationStatus.ACTIVE GROUP BY l.difficulty")
    List<Object[]> countByDifficulty();

    @Query("SELECT l.locationType, COUNT(l) FROM Location l WHERE l.status = org.example.entity.Location.LocationStatus.ACTIVE GROUP BY l.locationType")
    List<Object[]> countByLocationType();

    @Query("SELECT l.zoneType, COUNT(l) FROM Location l WHERE l.status = org.example.entity.Location.LocationStatus.ACTIVE GROUP BY l.zoneType")
    List<Object[]> countByZoneType();

    @Query("SELECT MAX(l.visitCount) FROM Location l")
    Long getMaxVisitCount();

    @Query("SELECT SUM(l.visitCount) FROM Location l WHERE l.status = org.example.entity.Location.LocationStatus.ACTIVE")
    Long getTotalVisits();

    // ========== UPDATES ==========

    @Modifying
    @Transactional
    @Query("UPDATE Location l SET l.visitCount = l.visitCount + 1 WHERE l.id = :locationId")
    int incrementVisitCount(@Param("locationId") Long locationId);

    @Modifying
    @Transactional
    @Query("UPDATE Location l SET l.isUnlocked = :isUnlocked WHERE l.id = :locationId")
    int setUnlockedStatus(@Param("locationId") Long locationId, @Param("isUnlocked") Boolean isUnlocked);

    @Modifying
    @Transactional
    @Query("UPDATE Location l SET l.status = :status WHERE l.id = :locationId")
    int updateStatus(@Param("locationId") Long locationId, @Param("status") Location.LocationStatus status);

    @Modifying
    @Transactional
    @Query("UPDATE Location l SET l.visitCount = 0, l.playerDeaths = 0, l.monsterKills = 0 WHERE l.id = :locationId")
    int resetStatistics(@Param("locationId") Long locationId);

    @Modifying
    @Transactional
    @Query("UPDATE Location l SET l.active = :active WHERE l.id = :locationId")
    int updateActiveStatus(@Param("locationId") Long locationId, @Param("active") Boolean active);

    // ========== ADDITIONAL METHODS ==========

    @Query("SELECT DISTINCT l.locationType FROM Location l WHERE l.locationType IS NOT NULL AND l.status = org.example.entity.Location.LocationStatus.ACTIVE")
    List<Location.LocationType> findAllLocationTypes();

    @Query("SELECT DISTINCT l.zoneType FROM Location l WHERE l.zoneType IS NOT NULL AND l.status = org.example.entity.Location.LocationStatus.ACTIVE")
    List<Location.ZoneType> findAllZoneTypes();

    @Query("SELECT DISTINCT l.difficulty FROM Location l WHERE l.difficulty IS NOT NULL AND l.status = org.example.entity.Location.LocationStatus.ACTIVE")
    List<Location.Difficulty> findAllDifficulties();

    @Query("SELECT l FROM Location l WHERE l.isPvpEnabled = true AND l.status = org.example.entity.Location.LocationStatus.ACTIVE")
    List<Location> findAllPvpLocations();

    @Query("SELECT l FROM Location l WHERE l.isSafeZone = true AND l.status = org.example.entity.Location.LocationStatus.ACTIVE")
    List<Location> findAllSafeZones();

    @Query("SELECT l FROM Location l WHERE l.isInstance = true AND l.status = org.example.entity.Location.LocationStatus.ACTIVE")
    List<Location> findAllInstances();

    @Query("SELECT l FROM Location l WHERE l.isDungeon = true AND l.status = org.example.entity.Location.LocationStatus.ACTIVE")
    List<Location> findAllDungeons();

    @Query("SELECT l FROM Location l WHERE l.isRaid = true AND l.status = org.example.entity.Location.LocationStatus.ACTIVE")
    List<Location> findAllRaids();

    @Query("SELECT l FROM Location l WHERE l.worldId = :worldId AND l.status = org.example.entity.Location.LocationStatus.ACTIVE")
    List<Location> findByWorldId(@Param("worldId") Integer worldId);

    @Query("SELECT l FROM Location l WHERE l.regionId = :regionId AND l.status = org.example.entity.Location.LocationStatus.ACTIVE")
    List<Location> findByRegionId(@Param("regionId") Integer regionId);

    @Query("SELECT l FROM Location l WHERE l.parentLocation.id = :parentId AND l.status = org.example.entity.Location.LocationStatus.ACTIVE")
    List<Location> findByParentLocationId(@Param("parentId") Long parentId);

    // Search locations without parent location
    @Query("SELECT l FROM Location l WHERE l.parentLocation IS NULL AND l.status = org.example.entity.Location.LocationStatus.ACTIVE")
    List<Location> findRootLocations();

    // Native queries for geo-search
    @Query(value = "SELECT * FROM locations l WHERE " +
            "SQRT(POWER(l.coordinate_x - :x, 2) + POWER(l.coordinate_y - :y, 2)) <= :radius AND " +
            "l.status = 'ACTIVE' AND l.is_unlocked = true",
            nativeQuery = true)
    List<Location> findNearbyLocations(@Param("x") Integer x, @Param("y") Integer y, @Param("radius") Double radius);

    @Query(value = "SELECT * FROM locations l WHERE " +
            "SQRT(POWER(l.coordinate_x - :x, 2) + POWER(l.coordinate_y - :y, 2) + POWER(COALESCE(l.coordinate_z, 0) - :z, 2)) <= :radius AND " +
            "l.status = 'ACTIVE' AND l.is_unlocked = true",
            nativeQuery = true)
    List<Location> findNearbyLocations3D(@Param("x") Integer x, @Param("y") Integer y, @Param("z") Integer z, @Param("radius") Double radius);

    // Search by entry cost
    @Query("SELECT l FROM Location l WHERE " +
            "l.status = org.example.entity.Location.LocationStatus.ACTIVE AND " +
            "l.isUnlocked = true AND " +
            "(l.entryCost <= :maxCost OR l.accessCostGold <= :maxCost) " +
            "ORDER BY COALESCE(l.entryCost, 0) + COALESCE(l.accessCostGold, 0)")
    List<Location> findAffordableLocations(@Param("maxCost") Integer maxCost);

    // Search locations with first visit rewards
    @Query("SELECT l FROM Location l WHERE " +
            "l.status = org.example.entity.Location.LocationStatus.ACTIVE AND " +
            "l.isUnlocked = true AND " +
            "l.firstVisitReward > 0")
    List<Location> findLocationsWithFirstVisitReward();

    // Pagination queries
    @Query("SELECT l FROM Location l WHERE l.status = org.example.entity.Location.LocationStatus.ACTIVE ORDER BY l.createdAt DESC")
    List<Location> findRecentLocations();

    // Search autocomplete
    @Query("SELECT l.title FROM Location l WHERE " +
            "l.status = org.example.entity.Location.LocationStatus.ACTIVE AND " +
            "LOWER(l.title) LIKE LOWER(CONCAT(:prefix, '%')) " +
            "ORDER BY l.title")
    List<String> autocompleteTitles(@Param("prefix") String prefix);

    // Check location accessibility for player
    @Query("SELECT CASE WHEN COUNT(l) > 0 THEN true ELSE false END FROM Location l WHERE " +
            "l.id = :locationId AND " +
            "l.status = org.example.entity.Location.LocationStatus.ACTIVE AND " +
            "l.isUnlocked = true AND " +
            "(l.requiredLevel IS NULL OR l.requiredLevel <= :playerLevel) AND " +
            "(l.minLevel IS NULL OR l.minLevel <= :playerLevel) AND " +
            "(l.maxLevel IS NULL OR l.maxLevel >= :playerLevel) AND " +
            "(l.requiredFaction IS NULL OR l.requiredFaction = '' OR l.requiredFaction = :faction OR l.requiredFaction = 'NEUTRAL')")
    boolean isLocationAccessible(@Param("locationId") Long locationId,
                                 @Param("playerLevel") Integer playerLevel,
                                 @Param("faction") String faction);

    // ========== ACTIVITY METHODS ==========

    // Search active locations with sorting
    @Query("SELECT l FROM Location l WHERE l.active = true ORDER BY l.title ASC")
    List<Location> findAllActiveSortedByName();

    // Search inactive locations
    List<Location> findByActiveFalse();

    // Search by activity and status
    @Query("SELECT l FROM Location l WHERE l.active = :active AND l.status = :status")
    List<Location> findByActiveAndStatus(@Param("active") Boolean active,
                                         @Param("status") Location.LocationStatus status);

    // Count active locations by type
    @Query("SELECT l.locationType, COUNT(l) FROM Location l WHERE l.active = true GROUP BY l.locationType")
    List<Object[]> countActiveByLocationType();

    // Count active locations by zone
    @Query("SELECT l.zoneType, COUNT(l) FROM Location l WHERE l.active = true GROUP BY l.zoneType")
    List<Object[]> countActiveByZoneType();

    // Count active locations by difficulty
    @Query("SELECT l.difficulty, COUNT(l) FROM Location l WHERE l.active = true GROUP BY l.difficulty")
    List<Object[]> countActiveByDifficulty();
}