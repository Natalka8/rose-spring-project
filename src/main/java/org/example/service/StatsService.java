package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.*;
import org.example.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class StatsService {

    private final UserRepository userRepository;
    private final GameActionRepository gameActionRepository;
    private final LocationRepository gameLocationRepository;
    private final GameSessionRepository gameSessionRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final AchievementTemplateRepository achievementTemplateRepository;

    @PersistenceContext
    private EntityManager entityManager;

    // ==================== INTERFACES ====================

    @FunctionalInterface
    public interface DataSupplier {
        List<?> getBetween(LocalDateTime start, LocalDateTime end);
    }

    @FunctionalInterface
    private interface StatsSupplier {
        Map<String, Object> get() throws Exception;
    }

    // ==================== MAIN METHODS ====================

    @Transactional(readOnly = true)
    public Map<String, Object> getOverallStats() {
        return createStatsResponse(() -> {
            Map<String, Object> stats = new HashMap<>();

            // Main metrics
            stats.put("totalUsers", userRepository.count());
            stats.put("activeUsers", calculateActiveUsers());
            stats.put("newUsersToday", calculateNewUsersToday());
            stats.put("totalSessions", gameSessionRepository.count());

            // Actions and activity
            stats.put("totalActions", gameActionRepository.count());
            stats.put("actionsToday", calculateActionsToday());
            stats.put("averageActionsPerUser", calculateAverageActionsPerUser());

            // Achievements
            stats.put("totalAchievementsUnlocked", userAchievementRepository.count());
            stats.put("achievementsToday", calculateAchievementsToday());
            stats.put("uniqueAchievementsUnlocked", achievementTemplateRepository.count());

            // Economy
            stats.put("totalGold", calculateTotalGold());
            stats.put("averageGold", calculateAverageGold());

            // Progress
            stats.put("averageLevel", calculateAverageLevel());
            stats.put("maxLevel", calculateMaxLevel());

            // Locations
            stats.put("totalLocations", gameLocationRepository.count());

            // Time
            stats.put("timestamp", LocalDateTime.now());
            stats.put("lastUpdated", LocalDateTime.now());

            return stats;
        }, "общая статистика");
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getUserDetailedStats(Long userId) {
        return createStatsResponse(() -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

            Map<String, Object> stats = new HashMap<>();

            // Basic information
            stats.put("userInfo", createUserMap(user));

            // Action statistics
            List<GameAction> userActions = gameActionRepository.findByUser(user);
            stats.put("actionStats", calculateActionStats(userActions));

            // Session statistics
            List<GameSession> userSessions = findUserSessions(user);
            stats.put("sessionStats", calculateSessionStats(userSessions));

            // Achievement statistics
            List<UserAchievement> userAchievements = userAchievementRepository.findByUser(user);
            stats.put("achievementStats", calculateAchievementStats(userAchievements));

            // Game statistics
            stats.put("gameStats", calculateGameStats(user));

            // Rating and comparison
            stats.put("rank", calculateUserRank(userId));

            // Activity
            stats.put("activityPatterns", calculateActivityPatterns(userActions));

            return stats;
        }, "подробная статистика для пользователя: " + userId);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getTimeSeriesStats(String period, Integer days) {
        return createStatsResponse(() -> {
            LocalDateTime endDate = LocalDateTime.now();
            LocalDateTime startDate = calculateStartDate(period, days, endDate);

            Map<String, Object> stats = new HashMap<>();
            stats.put("period", period);
            stats.put("startDate", startDate);
            stats.put("endDate", endDate);

            // User registrations
            stats.put("registrations", calculateDataByDate(
                    userRepository.findAll().stream()
                            .filter(user -> user.getCreatedAt() != null &&
                                    user.getCreatedAt().isAfter(startDate) &&
                                    user.getCreatedAt().isBefore(endDate))
                            .collect(Collectors.toList()),
                    startDate, endDate
            ));

            // Activity
            stats.put("activity", calculateDataByDate(
                    gameActionRepository.findAll().stream()
                            .filter(action -> action.getActionTime() != null &&
                                    action.getActionTime().isAfter(startDate) &&
                                    action.getActionTime().isBefore(endDate))
                            .collect(Collectors.toList()),
                    startDate, endDate
            ));

            // Achievements
            stats.put("achievements", calculateDataByDate(
                    userAchievementRepository.findAll().stream()
                            .filter(ach -> ach.getUnlockedAt() != null &&
                                    ach.getUnlockedAt().isAfter(startDate) &&
                                    ach.getUnlockedAt().isBefore(endDate))
                            .collect(Collectors.toList()),
                    startDate, endDate
            ));

            return stats;
        }, "временная статистика за период: " + period);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getActionTypeStats(String actionType, LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return createStatsResponse(() -> {
            Map<String, Object> stats = new HashMap<>();

            List<GameAction> actions;
            if (actionType != null && startDateTime != null && endDateTime != null) {
                actions = gameActionRepository.findByActionTypeAndActionTimeBetween(actionType, startDateTime, endDateTime);
            } else if (actionType != null) {
                actions = gameActionRepository.findByActionType(actionType);
            } else if (startDateTime != null && endDateTime != null) {
                actions = gameActionRepository.findByActionTimeBetween(startDateTime, endDateTime);
            } else {
                actions = gameActionRepository.findAll();
            }

            // Grouping by action type
            Map<String, Long> actionCounts = actions.stream()
                    .collect(Collectors.groupingBy(GameAction::getActionType, Collectors.counting()));

            stats.put("totalActions", actions.size());
            stats.put("actionCounts", actionCounts);
            stats.put("uniqueUsers", actions.stream()
                    .map(a -> a.getUser().getId())
                    .distinct()
                    .count());

            if (actionType != null) {
                stats.put("selectedActionType", actionType);
            }

            return stats;
        }, "статистика по типам действий");
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getLocationStats() {
        return createStatsResponse(() -> {
            List<Location> locations = gameLocationRepository.findAll();

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalLocations", locations.size());

            // Count active locations
            long activeLocations = locations.stream()
                    .filter(l -> l.getActive() != null && l.getActive())
                    .count();
            stats.put("activeLocations", activeLocations);

            // Count locations with visits
            long locationsWithVisitsLong = locations.stream()
                    .filter(l -> l.getVisitCount() != null && l.getVisitCount() > 0)
                    .count();
            stats.put("locationsWithVisits", locationsWithVisitsLong);

            // Total visits count (Long)
            Long totalVisits = locations.stream()
                    .map(l -> l.getVisitCount() != null ? l.getVisitCount() : 0L)
                    .reduce(0L, Long::sum);
            stats.put("totalVisits", totalVisits);

            // Average visit count
            double averageVisitCount = locations.stream()
                    .mapToDouble(l -> l.getVisitCount() != null ? l.getVisitCount().doubleValue() : 0.0)
                    .average()
                    .orElse(0.0);
            stats.put("averageVisitCount", averageVisitCount);

            stats.put("topVisitedLocations", getTopVisitedLocations(locations, 10));

            // Statistics by location types
            Map<String, Long> locationsByType = locations.stream()
                    .filter(l -> l.getLocationType() != null)
                    .collect(Collectors.groupingBy(
                            l -> l.getLocationType().name(),
                            Collectors.counting()
                    ));
            stats.put("locationsByType", locationsByType);

            // Statistics by difficulty
            Map<String, Long> locationsByDifficulty = locations.stream()
                    .filter(l -> l.getDifficulty() != null)
                    .collect(Collectors.groupingBy(
                            l -> l.getDifficulty().name(),
                            Collectors.counting()
                    ));
            stats.put("locationsByDifficulty", locationsByDifficulty);

            // Statistics by status
            Map<String, Long> locationsByStatus = locations.stream()
                    .filter(l -> l.getStatus() != null)
                    .collect(Collectors.groupingBy(
                            l -> l.getStatus().name(),
                            Collectors.counting()
                    ));
            stats.put("locationsByStatus", locationsByStatus);

            return stats;
        }, "статистика локаций");
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getAchievementStats() {
        return createStatsResponse(() -> {
            Map<String, Object> stats = new HashMap<>();

            // All achievement templates
            List<AchievementTemplate> allTemplates = achievementTemplateRepository.findAll();
            stats.put("totalAchievements", allTemplates.size());

            // Statistics by categories
            Map<String, Long> byCategory = allTemplates.stream()
                    .filter(a -> a.getCategory() != null)
                    .collect(Collectors.groupingBy(AchievementTemplate::getCategory, Collectors.counting()));
            stats.put("achievementsByCategory", byCategory);

            // Unlocked achievements
            List<UserAchievement> unlockedAchievements = userAchievementRepository.findAll();
            stats.put("totalUnlocked", unlockedAchievements.size());

            long uniqueUsersLong = unlockedAchievements.stream()
                    .map(ua -> ua.getUser().getId())
                    .distinct()
                    .count();
            stats.put("uniqueUsersWithAchievements", uniqueUsersLong);

            return stats;
        }, "статистика достижений");
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getPersonalStats(Long userId) {
        return createStatsResponse(() -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

            Map<String, Object> stats = new HashMap<>();

            // Main metrics
            stats.put("username", user.getUsername());
            stats.put("email", user.getEmail());
            stats.put("enabled", user.isEnabled());
            stats.put("status", user.getStatus() != null ? user.getStatus().name() : "ACTIVE");

            // Activity
            stats.put("createdAt", user.getCreatedAt());
            stats.put("updatedAt", user.getUpdatedAt());
            stats.put("lastLoginAt", user.getLastLoginAt());

            return stats;
        }, "персональная статистика для пользователя: " + userId);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getSessionStats(LocalDateTime startDateTime, LocalDateTime endDateTime) {
        return createStatsResponse(() -> {
            Map<String, Object> stats = new HashMap<>();

            List<GameSession> sessions;
            if (startDateTime != null && endDateTime != null) {
                sessions = gameSessionRepository.findByStartTimeBetween(startDateTime, endDateTime);
            } else {
                sessions = gameSessionRepository.findAll();
            }

            stats.put("totalSessions", sessions.size());

            long activeSessionsLong = sessions.stream()
                    .filter(s -> s.getStatus() != null && s.getStatus().equals("ACTIVE"))
                    .count();
            stats.put("activeSessions", activeSessionsLong);

            // Session durations
            List<Long> durations = sessions.stream()
                    .filter(s -> s.getStartTime() != null && s.getEndTime() != null)
                    .map(s -> ChronoUnit.MINUTES.between(s.getStartTime(), s.getEndTime()))
                    .collect(Collectors.toList());

            stats.put("averageSessionDuration", durations.stream()
                    .mapToLong(Long::longValue)
                    .average()
                    .orElse(0));
            stats.put("minSessionDuration", durations.stream()
                    .mapToLong(Long::longValue)
                    .min()
                    .orElse(0));
            stats.put("maxSessionDuration", durations.stream()
                    .mapToLong(Long::longValue)
                    .max()
                    .orElse(0));

            return stats;
        }, "статистика сессий");
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getLeaderboard(String criteria, Integer limit) {
        return createStatsResponse(() -> {
            List<User> users = userRepository.findAll();
            List<User> sortedUsers = sortAndLimitUsers(users, criteria, limit != null ? limit : 10);

            List<Map<String, Object>> leaderboard = sortedUsers.stream()
                    .map(this::createUserMap)
                    .collect(Collectors.toList());

            Map<String, Object> stats = new HashMap<>();
            stats.put("criteria", criteria);
            stats.put("limit", limit);
            stats.put("leaderboard", leaderboard);
            stats.put("totalPlayers", users.size());
            return stats;
        }, "таблица лидеров по критерию: " + criteria);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getUserStats(Long userId) {
        return getUserDetailedStats(userId);
    }

    // ==================== HELPER METHODS ====================

    private Map<String, Object> createUserMap(User user) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", user.getId());
        map.put("username", user.getUsername());
        map.put("email", user.getEmail());
        map.put("enabled", user.isEnabled());
        map.put("createdAt", user.getCreatedAt());
        map.put("lastLoginAt", user.getLastLoginAt());
        return map;
    }

    private Map<String, Object> calculateActionStats(List<GameAction> actions) {
        Map<String, Object> stats = new HashMap<>();

        stats.put("total", actions.size());
        stats.put("byType", actions.stream()
                .collect(Collectors.groupingBy(GameAction::getActionType, Collectors.counting())));

        if (!actions.isEmpty()) {
            stats.put("firstAction", actions.stream()
                    .min(Comparator.comparing(GameAction::getActionTime))
                    .map(GameAction::getActionTime)
                    .orElse(null));
            stats.put("lastAction", actions.stream()
                    .max(Comparator.comparing(GameAction::getActionTime))
                    .map(GameAction::getActionTime)
                    .orElse(null));
        }

        return stats;
    }

    private Map<String, Object> calculateSessionStats(List<GameSession> sessions) {
        Map<String, Object> stats = new HashMap<>();

        stats.put("total", sessions.size());

        long activeLong = sessions.stream()
                .filter(s -> s.getStatus() != null && s.getStatus().equals("ACTIVE"))
                .count();
        stats.put("active", activeLong);

        // Session durations
        List<Long> durations = sessions.stream()
                .filter(s -> s.getStartTime() != null && s.getEndTime() != null)
                .map(s -> ChronoUnit.MINUTES.between(s.getStartTime(), s.getEndTime()))
                .collect(Collectors.toList());

        if (!durations.isEmpty()) {
            stats.put("averageDuration", durations.stream()
                    .mapToLong(Long::longValue)
                    .average()
                    .orElse(0));
            stats.put("totalDuration", durations.stream()
                    .mapToLong(Long::longValue)
                    .sum());
        }

        return stats;
    }

    private Map<String, Object> calculateAchievementStats(List<UserAchievement> achievements) {
        Map<String, Object> stats = new HashMap<>();

        stats.put("total", achievements.size());

        long completedLong = achievements.stream()
                .filter(a -> a.getCompleted() != null && a.getCompleted())
                .count();
        stats.put("completed", completedLong);

        stats.put("byCategory", achievements.stream()
                .filter(a -> a.getAchievement() != null && a.getAchievement().getCategory() != null)
                .collect(Collectors.groupingBy(
                        a -> a.getAchievement().getCategory(),
                        Collectors.counting()
                )));

        return stats;
    }

    private Map<String, Object> calculateGameStats(User user) {
        Map<String, Object> stats = new HashMap<>();
        // Basic user statistics
        stats.put("username", user.getUsername());
        stats.put("email", user.getEmail());
        stats.put("status", user.getStatus() != null ? user.getStatus().name() : "ACTIVE");
        return stats;
    }

    private int calculateUserRank(Long userId) {
        List<User> users = userRepository.findAll();
        for (int i = 0; i < users.size(); i++) {
            if (users.get(i).getId().equals(userId)) {
                return i + 1;
            }
        }
        return users.size() + 1;
    }

    private Map<String, Object> calculateActivityPatterns(List<GameAction> actions) {
        Map<String, Object> patterns = new HashMap<>();

        if (actions.isEmpty()) {
            return patterns;
        }

        // By day of week
        Map<String, Long> byWeekday = actions.stream()
                .filter(a -> a.getActionTime() != null)
                .collect(Collectors.groupingBy(
                        a -> a.getActionTime().getDayOfWeek().toString(),
                        Collectors.counting()
                ));
        patterns.put("byWeekday", byWeekday);

        // By hour of day
        Map<Integer, Long> byHour = actions.stream()
                .filter(a -> a.getActionTime() != null)
                .collect(Collectors.groupingBy(
                        a -> a.getActionTime().getHour(),
                        Collectors.counting()
                ));
        patterns.put("byHour", byHour);

        return patterns;
    }

    // ==================== UTILITY METHODS ====================

    private Map<String, Object> createStatsResponse(StatsSupplier supplier, String logMessage) {
        Map<String, Object> response = new HashMap<>();
        try {
            response.putAll(supplier.get());
            response.put("success", true);
            response.put("timestamp", LocalDateTime.now());
            log.info("Успешно получено: {}", logMessage);
        } catch (Exception e) {
            log.error("Ошибка при получении {}: {}", logMessage, e.getMessage());
            response.put("success", false);
            response.put("error", "Ошибка при получении данных: " + e.getMessage());
        }
        return response;
    }

    private List<User> sortAndLimitUsers(List<User> users, String criteria, int limit) {
        Comparator<User> comparator = Comparator.comparing(
                User::getUsername,
                String.CASE_INSENSITIVE_ORDER
        );

        return users.stream()
                .sorted(comparator)
                .limit(limit)
                .collect(Collectors.toList());
    }

    private LocalDateTime calculateStartDate(String period, Integer days, LocalDateTime endDate) {
        if (days == null) {
            days = getDefaultDays(period);
        }

        switch (period.toLowerCase()) {
            case "weekly":
                return endDate.minusWeeks(days);
            case "monthly":
                return endDate.minusMonths(days);
            default: // daily
                return endDate.minusDays(days);
        }
    }

    private int getDefaultDays(String period) {
        switch (period.toLowerCase()) {
            case "weekly":
                return 1;
            case "monthly":
                return 1;
            default: // daily
                return 1;
        }
    }

    private Map<String, Long> calculateDataByDate(List<?> data, LocalDateTime start, LocalDateTime end) {
        return data.stream()
                .collect(Collectors.groupingBy(
                        this::extractDateString,
                        Collectors.counting()
                ));
    }

    private String extractDateString(Object obj) {
        LocalDateTime dateTime = null;

        if (obj instanceof User user) {
            dateTime = user.getCreatedAt();
        } else if (obj instanceof GameAction action) {
            dateTime = action.getActionTime();
        } else if (obj instanceof UserAchievement achievement) {
            dateTime = achievement.getUnlockedAt();
        } else if (obj instanceof GameSession session) {
            dateTime = session.getStartTime();
        }

        return dateTime != null ? dateTime.toLocalDate().toString() : "unknown";
    }

    private List<Map<String, Object>> getTopVisitedLocations(List<Location> locations, int limit) {
        return locations.stream()
                .filter(l -> l.getActive() != null && l.getActive()) // Use getActive()
                .sorted((a, b) -> {
                    // Use Long for comparison
                    Long countA = a.getVisitCount() != null ? a.getVisitCount() : 0L;
                    Long countB = b.getVisitCount() != null ? b.getVisitCount() : 0L;
                    return Long.compare(countB, countA); // Sort in descending order
                })
                .limit(limit)
                .map(this::createLocationMap)
                .collect(Collectors.toList());
    }

    private Map<String, Object> createLocationMap(Location location) {
        Map<String, Object> map = new HashMap<>();
        map.put("id", location.getId());
        map.put("title", location.getTitle());
        map.put("displayName", location.getDisplayName());
        map.put("description", location.getDescription());
        map.put("type", getLocationType(location));
        map.put("zoneType", location.getZoneType() != null ? location.getZoneType().name() : "UNKNOWN");
        map.put("status", location.getStatus() != null ? location.getStatus().name() : "UNKNOWN");
        map.put("requiredLevel", location.getRequiredLevel() != null ? location.getRequiredLevel() : 0);
        map.put("minLevel", location.getMinLevel() != null ? location.getMinLevel() : 1);
        map.put("maxLevel", location.getMaxLevel() != null ? location.getMaxLevel() : 100);
        map.put("recommendedLevel", location.getRecommendedLevel());

        // Use getDifficulty()
        map.put("difficulty", location.getDifficulty() != null ? location.getDifficulty().name() : "UNKNOWN");
        map.put("difficultyDisplayName", location.getDifficulty() != null ?
                location.getDifficulty().getDisplayName() : "Неизвестная");
        map.put("difficultyDescription", location.getDifficultyDescription());

        // Convert enum Difficulty to numeric level
        map.put("difficultyLevel", convertDifficultyToLevel(location.getDifficulty()));

        // Use Long, since visitCount has Long type in entity
        Long visitCount = location.getVisitCount() != null ? location.getVisitCount() : 0L;
        map.put("visitCount", visitCount);

        map.put("playerDeaths", location.getPlayerDeaths() != null ? location.getPlayerDeaths() : 0L);
        map.put("monsterKills", location.getMonsterKills() != null ? location.getMonsterKills() : 0L);

        // Geographic information
        map.put("coordinates", location.getCoordinatesString());
        map.put("worldId", location.getWorldId() != null ? location.getWorldId() : 1);
        map.put("regionId", location.getRegionId());

        // Flags and properties - FIXED: use correct method names
        map.put("isSafeZone", location.getIsSafeZone() != null ? location.getIsSafeZone() : false);
        map.put("isPvpEnabled", location.getIsPvpEnabled() != null ? location.getIsPvpEnabled() : false);
        map.put("isInstance", location.getIsInstance() != null ? location.getIsInstance() : false);
        map.put("isDungeon", location.getIsDungeon() != null ? location.getIsDungeon() : false);
        map.put("isRaid", location.getIsRaid() != null ? location.getIsRaid() : false);
        map.put("canFastTravel", location.getCanFastTravel() != null ? location.getCanFastTravel() : true);
        map.put("canTeleport", location.getCanTeleport() != null ? location.getCanTeleport() : true);
        map.put("canRespawn", location.getCanRespawn() != null ? location.getCanRespawn() : true);

        // Entry cost
        map.put("entryCost", location.getEntryCost() != null ? location.getEntryCost() : 0);
        map.put("accessCostGold", location.getAccessCostGold() != null ? location.getAccessCostGold() : 0);
        map.put("totalEntryCost", location.getTotalEntryCost());

        // Rewards
        map.put("firstVisitReward", location.getFirstVisitReward() != null ? location.getFirstVisitReward() : 0);
        map.put("baseExperienceReward", location.getBaseExperienceReward() != null ? location.getBaseExperienceReward() : 0);
        map.put("baseGoldReward", location.getBaseGoldReward() != null ? location.getBaseGoldReward() : 0);

        // Timers
        map.put("respawnTimeSeconds", location.getRespawnTimeSeconds() != null ? location.getRespawnTimeSeconds() : 60);
        map.put("cooldownMinutes", location.getCooldownMinutes() != null ? location.getCooldownMinutes() : 0);

        // Metadata - FIXED: use getActive() instead of getIsActive()
        map.put("isActive", location.getActive() != null ? location.getActive() : true);
        map.put("isSecret", location.getIsSecret() != null ? location.getIsSecret() : false);
        map.put("isHidden", location.getIsHidden() != null ? location.getIsHidden() : false);
        map.put("isFeatured", location.getIsFeatured() != null ? location.getIsFeatured() : false);
        map.put("isUnlocked", location.getIsUnlocked() != null ? location.getIsUnlocked() : true);

        // Additional calculated properties
        map.put("isTown", location.isTown());
        map.put("isDungeonArea", location.isDungeonArea());
        map.put("isCombatZone", location.isCombatZone());
        map.put("requiresEntryCost", location.requiresEntryCost());
        map.put("hasFirstVisitReward", location.hasFirstVisitReward());
        map.put("isPvpLocation", location.isPvpLocation());
        map.put("isSafeLocation", location.isSafeLocation());
        map.put("isInstanceLocation", location.isInstanceLocation());

        return map;
    }

    private int convertDifficultyToLevel(Location.Difficulty difficulty) {
        if (difficulty == null) return 0;
        switch (difficulty) {
            case VERY_EASY: return 1;
            case EASY: return 2;
            case NORMAL: return 3;
            case HARD: return 4;
            case VERY_HARD: return 5;
            case EPIC: return 6;
            case LEGENDARY: return 7;
            default: return 0;
        }
    }

    private String getLocationType(Location location) {
        if (location.getLocationType() != null) {
            return location.getLocationType().name();
        }
        return "UNKNOWN";
    }

    private <T> double calculateAverage(List<T> items, Function<T, Double> mapper) {
        if (items.isEmpty()) return 0;
        return items.stream()
                .map(mapper)
                .mapToDouble(Double::doubleValue)
                .average()
                .orElse(0);
    }

    // ==================== CALCULATION METHODS ====================

    private double calculateAverageLevel() {
        return userRepository.findAll().stream()
                .mapToInt(u -> 1) // Placeholder - assume all have level 1
                .average()
                .orElse(0);
    }

    private double calculateAverageGold() {
        return userRepository.findAll().stream()
                .mapToLong(u -> 0L) // Placeholder
                .average()
                .orElse(0);
    }

    private long calculateActiveUsers() {
        try {
            LocalDateTime yesterday = LocalDateTime.now().minusDays(1);
            return userRepository.findAll().stream()
                    .filter(u -> u.getLastLoginAt() != null && u.getLastLoginAt().isAfter(yesterday))
                    .count();
        } catch (Exception e) {
            log.warn("Ошибка расчета активных пользователей: {}", e.getMessage());
            return 0;
        }
    }

    private long calculateNewUsersToday() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        return userRepository.findAll().stream()
                .filter(user -> user.getCreatedAt() != null && user.getCreatedAt().isAfter(todayStart))
                .count();
    }

    private long calculateActionsToday() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        return gameActionRepository.findAll().stream()
                .filter(action -> action.getActionTime() != null && action.getActionTime().isAfter(todayStart))
                .count();
    }

    private long calculateAchievementsToday() {
        LocalDateTime todayStart = LocalDate.now().atStartOfDay();
        return userAchievementRepository.findAll().stream()
                .filter(ach -> ach.getUnlockedAt() != null && ach.getUnlockedAt().isAfter(todayStart))
                .count();
    }

    private double calculateAverageActionsPerUser() {
        long totalUsers = userRepository.count();
        if (totalUsers == 0) return 0;
        return (double) gameActionRepository.count() / totalUsers;
    }

    private long calculateTotalGold() {
        return 0L; // Placeholder
    }

    private int calculateMaxLevel() {
        return 1; // Placeholder
    }

    private long calculateVisitedLocations() {
        return gameActionRepository.findAll().stream()
                .filter(a -> a.getLocation() != null)
                .map(a -> a.getLocation().getId())
                .distinct()
                .count();
    }

    // ==================== SESSION METHODS ====================

    /**
     * Find user sessions via alternative method
     */
    private List<GameSession> findUserSessions(User user) {
        try {
            // Try to find via user.id
            return gameSessionRepository.findAll().stream()
                    .filter(session -> session.getUser() != null && session.getUser().getId().equals(user.getId()))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("Не удалось найти сессии пользователя напрямую: {}", e.getMessage());
            return Collections.emptyList();
        }
    }

    // ==================== ADDITIONAL METHODS FOR CONTROLLER ====================

    @Transactional(readOnly = true)
    public Map<String, Object> getSummaryStats() {
        return getOverallStats();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getComparativeStats(Long userId) {
        return getUserDetailedStats(userId);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getRetentionStats(Integer cohortDays) {
        Map<String, Object> stats = new HashMap<>();
        stats.put("message", "Статистика ретеншена в разработке");
        stats.put("cohortDays", cohortDays);
        stats.put("timestamp", LocalDateTime.now());
        return stats;
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getMonetizationStats() {
        Map<String, Object> stats = new HashMap<>();
        stats.put("message", "Статистика монетизации в разработке");
        stats.put("timestamp", LocalDateTime.now());
        return stats;
    }

    @Transactional(readOnly = true)
    public String exportStatsToCsv(String statType, Map<String, Object> params) {
        return "Статистика;Значение\nСервис;StatsService\nТип;" + statType + "\nВремя;" + LocalDateTime.now();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getDashboardStats() {
        return getOverallStats();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getUserGameStats(Long userId) {
        return getUserDetailedStats(userId);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getUserPlayTime(Long userId) {
        return getUserDetailedStats(userId);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getUserAchievements(Long userId) {
        return getPersonalStats(userId);
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getAverageStats() {
        return getOverallStats();
    }

    @Transactional(readOnly = true)
    public Map<String, Object> getTimeSeriesData(String period, Integer days) {
        return getTimeSeriesStats(period, days);
    }
}