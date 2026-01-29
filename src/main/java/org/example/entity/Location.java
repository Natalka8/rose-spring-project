package org.example.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "locations")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString
public class Location {

    public enum LocationType {
        CITY("Город"),
        VILLAGE("Деревня"),
        FOREST("Лес"),
        MOUNTAIN("Горы"),
        CAVE("Пещера"),
        DUNGEON("Подземелье"),
        TEMPLE("Храм"),
        CASTLE("Замок"),
        TAVERN("Таверна"),
        SHOP("Магазин"),
        ARENA("Арена"),
        HARBOR("Порт"),
        DESERT("Пустыня"),
        SWAMP("Болото"),
        RUINS("Руины"),
        TOWN("Городское поселение"),
        RAID("Рейд"),
        OTHER("Другое");

        private final String displayName;

        LocationType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum ZoneType {
        SAFE("Безопасная"),
        COMBAT("Боевая"),
        PVP("PvP"),
        RESTRICTED("Ограниченная"),
        INSTANCE("Инстанс");

        private final String displayName;

        ZoneType(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum LocationStatus {
        ACTIVE("Активна"),
        INACTIVE("Неактивна"),
        MAINTENANCE("На обслуживании"),
        CLOSED("Закрыта"),
        HIDDEN("Скрыта");

        private final String displayName;

        LocationStatus(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    public enum Difficulty {
        VERY_EASY("Очень легкая"),
        EASY("Легкая"),
        NORMAL("Средняя"),
        HARD("Сложная"),
        VERY_HARD("Очень сложная"),
        EPIC("Эпическая"),
        LEGENDARY("Легендарная");

        private final String displayName;

        Difficulty(String displayName) {
            this.displayName = displayName;
        }

        public String getDisplayName() {
            return displayName;
        }
    }

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // Basic information
    @Column(name = "title", nullable = false, unique = true, length = 100)
    private String title;

    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    @Column(name = "description", length = 2000)
    private String description;

    @Column(name = "lore", columnDefinition = "TEXT")
    private String lore;

    // Type and category
    @Enumerated(EnumType.STRING)
    @Column(name = "location_type", length = 50)
    @Builder.Default
    private LocationType locationType = LocationType.OTHER;

    @Enumerated(EnumType.STRING)
    @Column(name = "zone_type", length = 50)
    @Builder.Default
    private ZoneType zoneType = ZoneType.SAFE;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", length = 20)
    @Builder.Default
    private LocationStatus status = LocationStatus.ACTIVE;

    @Column(name = "category", length = 50)
    private String category;

    // Levels and difficulty
    @Column(name = "required_level")
    @Builder.Default
    private Integer requiredLevel = 1;

    @Column(name = "min_level")
    @Builder.Default
    private Integer minLevel = 1;

    @Column(name = "max_level")
    @Builder.Default
    private Integer maxLevel = 100;

    @Column(name = "recommended_level")
    private Integer recommendedLevel;

    @Enumerated(EnumType.STRING)
    @Column(name = "difficulty", length = 20)
    @Builder.Default
    private Difficulty difficulty = Difficulty.NORMAL;

    @Column(name = "difficulty_description", length = 100)
    private String difficultyDescription;

    // Geography and coordinates
    @Column(name = "world_id")
    @Builder.Default
    private Integer worldId = 1;

    @Column(name = "region_id")
    private Integer regionId;

    @Column(name = "coordinate_x")
    private Integer coordinateX;

    @Column(name = "coordinate_y")
    private Integer coordinateY;

    @Column(name = "coordinate_z")
    private Integer coordinateZ;

    @Column(name = "map_image_url", length = 255)
    private String mapImageUrl;

    @Column(name = "image_url", length = 255)
    private String imageUrl;

    // Restrictions and access
    @Column(name = "is_unlocked")
    @Builder.Default
    private Boolean isUnlocked = true;

    @Column(name = "max_players")
    @Builder.Default
    private Integer maxPlayers = 100;

    @Column(name = "min_players_required")
    @Builder.Default
    private Integer minPlayersRequired = 1;

    @Column(name = "max_players_required")
    private Integer maxPlayersRequired;

    @Column(name = "required_faction", length = 50)
    private String requiredFaction;

    @Column(name = "required_quest_id")
    private Long requiredQuestId;

    @Column(name = "required_item_id")
    private Long requiredItemId;

    @Column(name = "prerequisite_location_id")
    private Long prerequisiteLocationId;

    // Entry cost
    @Column(name = "entry_cost")
    @Builder.Default
    private Integer entryCost = 0;

    @Column(name = "access_cost_gold")
    @Builder.Default
    private Integer accessCostGold = 0;

    @Column(name = "access_cost_item_id")
    private Long accessCostItemId;

    // Properties and flags
    @Column(name = "is_safe_zone")
    @Builder.Default
    private Boolean isSafeZone = false;

    @Column(name = "is_pvp_enabled")
    @Builder.Default
    private Boolean isPvpEnabled = false;

    @Column(name = "is_instance")
    @Builder.Default
    private Boolean isInstance = false;

    @Column(name = "is_dungeon")
    @Builder.Default
    private Boolean isDungeon = false;

    @Column(name = "is_raid")
    @Builder.Default
    private Boolean isRaid = false;

    @Column(name = "can_fast_travel")
    @Builder.Default
    private Boolean canFastTravel = true;

    @Column(name = "can_teleport")
    @Builder.Default
    private Boolean canTeleport = true;

    @Column(name = "can_respawn")
    @Builder.Default
    private Boolean canRespawn = true;

    // Resources and timers
    @Column(name = "respawn_time_seconds")
    @Builder.Default
    private Integer respawnTimeSeconds = 60;

    @Column(name = "instance_duration_minutes")
    private Integer instanceDurationMinutes;

    @Column(name = "cooldown_minutes")
    @Builder.Default
    private Integer cooldownMinutes = 0;

    // Audio and visual effects
    @Column(name = "weather_patterns", columnDefinition = "JSON")
    private String weatherPatterns;

    @Column(name = "ambient_sounds", columnDefinition = "JSON")
    @Builder.Default
    private String ambientSounds = "{}";

    @Column(name = "music_track", length = 100)
    private String musicTrack;

    // NPC, monsters and spawns
    @Column(name = "spawn_npc_ids", columnDefinition = "JSON")
    private String spawnNpcIds;

    @Column(name = "spawn_monster_ids", columnDefinition = "JSON")
    private String spawnMonsterIds;

    @Column(name = "spawn_points", columnDefinition = "JSON")
    private String spawnPoints;

    @Column(name = "actions", columnDefinition = "TEXT")
    @Builder.Default
    private String actions = "{}";

    @Column(name = "target_locations", columnDefinition = "TEXT")
    @Builder.Default
    private String targetLocations = "{}";

    // Loot and rewards
    @Column(name = "first_visit_reward")
    @Builder.Default
    private Integer firstVisitReward = 0;

    @Column(name = "base_experience_reward")
    @Builder.Default
    private Integer baseExperienceReward = 0;

    @Column(name = "base_gold_reward")
    @Builder.Default
    private Integer baseGoldReward = 0;

    @Column(name = "loot_table_id")
    private Long lootTableId;

    @Column(name = "daily_rewards", columnDefinition = "JSON")
    private String dailyRewards;

    @Column(name = "reward_items", columnDefinition = "JSON")
    private String rewardItems;

    @Column(name = "reward_currency", columnDefinition = "JSON")
    private String rewardCurrency;

    // Reset timers
    @Column(name = "reset_daily")
    @Builder.Default
    private Boolean resetDaily = false;

    @Column(name = "reset_weekly")
    @Builder.Default
    private Boolean resetWeekly = false;

    @Column(name = "reset_monthly")
    @Builder.Default
    private Boolean resetMonthly = false;

    @Column(name = "is_repeatable")
    @Builder.Default
    private Boolean isRepeatable = false;

    @Column(name = "max_completions")
    @Builder.Default
    private Integer maxCompletions = 1;

    // Statistics
    @Column(name = "visit_count")
    @Builder.Default
    private Long visitCount = 0L;

    @Column(name = "player_deaths")
    @Builder.Default
    private Long playerDeaths = 0L;

    @Column(name = "monster_kills")
    @Builder.Default
    private Long monsterKills = 0L;

    // Class and faction restrictions
    @Column(name = "class_restriction", length = 50)
    private String classRestriction;

    @Column(name = "faction_restriction", length = 50)
    private String factionRestriction;

    // Metadata
    @Column(name = "version")
    @Builder.Default
    private Integer version = 1;

    @Column(name = "active")
    @Builder.Default
    private Boolean active = true; // Changed from isActive to active

    @Column(name = "is_secret")
    @Builder.Default
    private Boolean isSecret = false;

    @Column(name = "is_hidden")
    @Builder.Default
    private Boolean isHidden = false;

    @Column(name = "is_featured")
    @Builder.Default
    private Boolean isFeatured = false;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    // Relationships
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "parent_location_id")
    @ToString.Exclude
    private Location parentLocation;

    @PrePersist
    protected void onCreate() {
        if (createdAt == null) {
            createdAt = LocalDateTime.now();
        }
        if (updatedAt == null) {
            updatedAt = LocalDateTime.now();
        }
        if (active == null) {
            active = true;
        }
        if (visitCount == null) {
            visitCount = 0L;
        }
        if (status == null) {
            status = LocationStatus.ACTIVE;
        }
        if (difficultyDescription == null) {
            updateDifficultyDescription();
        }
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    // Helper methods
    public void incrementVisitCount() {
        if (visitCount == null) {
            visitCount = 0L;
        }
        visitCount++;
    }

    public void lock() {
        this.isUnlocked = false;
    }

    public void unlock() {
        this.isUnlocked = true;
    }

    public boolean isAccessibleForLevel(Integer playerLevel) {
        if (playerLevel == null || requiredLevel == null) {
            return false;
        }
        return Boolean.TRUE.equals(isUnlocked) &&
                status == LocationStatus.ACTIVE &&
                playerLevel >= requiredLevel;
    }

    public boolean isAccessible(Integer playerLevel, String playerFaction) {
        if (playerLevel == null) {
            return false;
        }

        if (status != LocationStatus.ACTIVE) {
            return false;
        }

        if (requiredLevel != null && playerLevel < requiredLevel) {
            return false;
        }

        if (minLevel != null && playerLevel < minLevel) {
            return false;
        }

        if (maxLevel != null && playerLevel > maxLevel) {
            return false;
        }

        if (requiredFaction != null && !requiredFaction.isEmpty() &&
                !requiredFaction.equals("NEUTRAL") &&
                !requiredFaction.equals(playerFaction)) {
            return false;
        }

        if (factionRestriction != null && !factionRestriction.isEmpty() &&
                !factionRestriction.equals("ANY") &&
                !factionRestriction.equals(playerFaction)) {
            return false;
        }

        return isUnlocked == null || isUnlocked;
    }

    public String getCoordinatesString() {
        if (coordinateX == null || coordinateY == null) {
            return "Неизвестно";
        }
        if (coordinateZ != null) {
            return String.format("[%d, %d, %d]", coordinateX, coordinateY, coordinateZ);
        }
        return String.format("[%d, %d]", coordinateX, coordinateY);
    }

    private void updateDifficultyDescription() {
        if (difficulty != null) {
            difficultyDescription = difficulty.getDisplayName();
        } else {
            difficultyDescription = "Неизвестная";
        }
    }

    public void setDifficulty(Difficulty difficulty) {
        this.difficulty = difficulty;
        updateDifficultyDescription();
    }

    // Helper method (without conflict with Lombok)
    public boolean isLocationActive() {
        return status == LocationStatus.ACTIVE;
    }

    // Additional methods
    public boolean hasCoordinates() {
        return coordinateX != null && coordinateY != null;
    }

    public boolean isTown() {
        return locationType == LocationType.CITY ||
                locationType == LocationType.VILLAGE ||
                locationType == LocationType.TAVERN ||
                locationType == LocationType.TOWN;
    }

    public boolean isDungeonArea() {
        return locationType == LocationType.DUNGEON ||
                locationType == LocationType.CAVE ||
                locationType == LocationType.TEMPLE ||
                Boolean.TRUE.equals(isDungeon);
    }

    public boolean isCombatZone() {
        return locationType == LocationType.FOREST ||
                locationType == LocationType.MOUNTAIN ||
                locationType == LocationType.DESERT ||
                locationType == LocationType.SWAMP ||
                locationType == LocationType.RUINS ||
                zoneType == ZoneType.COMBAT;
    }

    public boolean requiresEntryCost() {
        return (entryCost != null && entryCost > 0) ||
                (accessCostGold != null && accessCostGold > 0);
    }

    public boolean hasFirstVisitReward() {
        return firstVisitReward != null && firstVisitReward > 0;
    }

    public boolean isPvpLocation() {
        return Boolean.TRUE.equals(isPvpEnabled) ||
                locationType == LocationType.ARENA ||
                zoneType == ZoneType.PVP;
    }

    public boolean isSafeLocation() {
        return Boolean.TRUE.equals(isSafeZone) || isTown() || zoneType == ZoneType.SAFE;
    }

    public boolean isInstanceLocation() {
        return Boolean.TRUE.equals(isInstance) || zoneType == ZoneType.INSTANCE;
    }

    public boolean canPlayerEnter(Integer playerLevel, String playerFaction, String playerClass) {
        if (!isAccessible(playerLevel, playerFaction)) {
            return false;
        }

        if (classRestriction != null && !classRestriction.isEmpty() &&
                !classRestriction.equals("ANY") &&
                !classRestriction.equals(playerClass)) {
            return false;
        }

        if (minPlayersRequired != null && minPlayersRequired > 1) {
            return false;
        }

        return true;
    }

    public Integer getTotalEntryCost() {
        int total = 0;
        if (entryCost != null) total += entryCost;
        if (accessCostGold != null) total += accessCostGold;
        return total;
    }

    public void completeVisit() {
        incrementVisitCount();
        if (visitCount == 1 && firstVisitReward != null && firstVisitReward > 0) {
            // Logic for awarding first visit reward
        }
    }
}