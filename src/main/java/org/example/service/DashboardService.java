package org.example.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.entity.*;
import org.example.repository.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class DashboardService {

    private final UserRepository userRepository;
    private final UserAchievementRepository userAchievementRepository;
    private final GameActionRepository gameActionRepository;
    private final LocationRepository locationRepository;
    private final AchievementRepository achievementRepository;
    private final TagRepository tagRepository;
    private final TaskRepository taskRepository;

    // ==================== MAIN METHODS ====================

    /**
     * Get overall platform statistics
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getPlatformStats() {
        return createStatsResponse(() -> {
            Map<String, Object> stats = new HashMap<>();

            stats.put("totalUsers", getUserCount());
            stats.put("newUsersToday", countNewUsersToday());
            stats.put("totalAchievements", getUserAchievementCount());
            stats.put("achievementsToday", countAchievementsToday());
            stats.put("totalActions", getGameActionCount());
            stats.put("actionsToday", countActionsToday());
            stats.put("totalLocations", getLocationCount());
            stats.put("visitedLocations", countVisitedLocations());
            stats.put("activeUsers24h", countActiveUsersLast24h());
            stats.put("totalTags", tagRepository.count());
            stats.put("totalTasks", taskRepository.count());

            return stats;
        }, "platform stats");
    }

    /**
     * Get user statistics
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getUserStats(Long userId) {
        return createStatsResponse(() -> {
            User user = userRepository.findById(userId)
                    .orElseThrow(() -> new RuntimeException("Пользователь не найден"));

            List<UserAchievement> userAchievements = userAchievementRepository.findByUser(user);
            List<GameAction> userActions = gameActionRepository.findByUser(user);
            List<Task> userTasks = taskRepository.findByUserId(userId);

            Map<String, Object> stats = new HashMap<>();
            stats.put("userId", user.getId());
            stats.put("username", user.getUsername());
            stats.put("email", user.getEmail());
            stats.put("roles", user.getRoles());
            stats.put("createdAt", user.getCreatedAt());
            stats.put("lastLogin", user.getLastLoginAt());
            stats.put("accountStatus", user.getStatus() != null ? user.getStatus().name() : "UNKNOWN");

            // Additional statistics
            stats.put("totalAchievements", userAchievements.size());
            stats.put("achievementsToday", countAchievementsToday(userId));
            stats.put("completedAchievements", countCompletedAchievements(userAchievements));
            stats.put("totalActions", userActions.size());
            stats.put("actionsByType", groupActionsByType(userActions));
            stats.put("visitedLocationsCount", countVisitedLocations(userId));
            stats.put("favoriteLocation", getMostVisitedLocation(userId));
            stats.put("totalTasks", userTasks.size());
            stats.put("activeTasks", countActiveTasks(userTasks));
            stats.put("completedTasks", countCompletedTasks(userTasks));

            // Calculate level and progress based on achievements
            stats.put("achievementLevel", calculateAchievementLevel(userAchievements));
            stats.put("completionRate", calculateAchievementCompletionRate(userAchievements));

            return stats;
        }, "user stats for userId: " + userId);
    }

    /**
     * Get activity statistics for a period
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getActivityStats(LocalDate startDate, LocalDate endDate) {
        return createStatsResponse(() -> {
            LocalDateTime start = LocalDateTime.of(startDate, LocalTime.MIN);
            LocalDateTime end = LocalDateTime.of(endDate.plusDays(1), LocalTime.MIN).minusNanos(1);

            List<GameAction> actions = gameActionRepository.findByActionTimeBetween(start, end);
            List<UserAchievement> achievements = getAchievementsBetween(start, end);
            List<User> newUsers = getNewUsersBetween(start, end);

            // Using filtering instead of findByCreatedAtBetween
            List<Task> allTasks = taskRepository.findAll();
            List<Task> createdTasks = allTasks.stream()
                    .filter(task -> task.getCreatedAt() != null
                            && !task.getCreatedAt().isBefore(start)
                            && !task.getCreatedAt().isAfter(end))
                    .collect(Collectors.toList());

            Map<String, Object> stats = new HashMap<>();
            stats.put("periodStart", startDate);
            stats.put("periodEnd", endDate);
            stats.put("totalActions", actions.size());
            stats.put("activeUsersCount", actions.stream()
                    .map(action -> action.getUser().getId())
                    .distinct()
                    .count());
            stats.put("newUsersCount", newUsers.size());
            stats.put("activityByDay", groupActivityByDay(actions));
            stats.put("popularActions", getPopularActions(actions));
            stats.put("achievementsEarned", achievements.size());
            stats.put("tasksCreated", createdTasks.size());
            stats.put("mostActiveHour", getMostActiveHour(actions));

            return stats;
        }, "activity stats from " + startDate + " to " + endDate);
    }

    /**
     * Get user leaderboard
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getLeaderboard(String category, int limit) {
        return createStatsResponse(() -> {
            List<Map<String, Object>> leaderboard = getLeaderboardData(category, limit);

            Map<String, Object> stats = new HashMap<>();
            stats.put("leaderboard", leaderboard);
            stats.put("category", category);
            stats.put("limit", limit);
            stats.put("timestamp", LocalDateTime.now());

            return stats;
        }, "leaderboard for category: " + category);
    }

    /**
     * Get location analytics
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getLocationAnalytics() {
        return createStatsResponse(() -> {
            // FIXED: findByActiveTrue() instead of findByIsActiveTrue()
            List<Location> locations = locationRepository.findByActiveTrue();

            Map<String, Object> analytics = new HashMap<>();
            analytics.put("totalLocations", locations.size());
            analytics.put("activeLocations", locationRepository.countActiveLocations());
            analytics.put("topLocations", getTopLocations(locations, 10));
            analytics.put("locationsByType", groupLocationsByType(locations));
            analytics.put("locationsByZoneType", groupLocationsByZoneType(locations));
            analytics.put("locationsByDifficulty", groupLocationsByDifficulty(locations));
            analytics.put("visitStats", getVisitStats(locations));
            analytics.put("averageVisitsPerLocation", calculateAverageVisits(locations));
            analytics.put("mostPopularLocationType", getMostPopularLocationType(locations));

            return analytics;
        }, "location analytics");
    }

    /**
     * Get system metrics
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getSystemMetrics() {
        return createStatsResponse(() -> {
            LocalDateTime now = LocalDateTime.now();

            Map<String, Object> metrics = new HashMap<>();
            metrics.put("timestamp", now);

            // Users
            metrics.put("totalUsers", getUserCount());
            metrics.put("usersLast24h", countUsersAfter(now.minusDays(1)));
            metrics.put("usersLast7d", countUsersAfter(now.minusDays(7)));
            metrics.put("usersLast30d", countUsersAfter(now.minusDays(30)));
            metrics.put("activeUsers24h", countActiveUsersLast24h());

            // Achievements
            long totalAchievements = getUserAchievementCount();
            long totalUsers = getUserCount();
            metrics.put("totalAchievements", totalAchievements);
            metrics.put("achievementsPerUser", totalUsers > 0 ?
                    String.format("%.2f", (double) totalAchievements / totalUsers) : "0.00");

            // Actions
            long totalActions = getGameActionCount();
            metrics.put("totalActions", totalActions);
            metrics.put("actionsPerUser", totalUsers > 0 ?
                    String.format("%.2f", (double) totalActions / totalUsers) : "0.00");
            metrics.put("actionsPerDayLast7d", calculateAverageActionsPerDay(7));

            // Locations
            long totalLocations = getLocationCount();
            metrics.put("totalLocations", totalLocations);
            metrics.put("activeLocations", locationRepository.countActiveLocations());

            // Tasks
            long totalTasks = taskRepository.count();
            metrics.put("totalTasks", totalTasks);
            metrics.put("tasksPerUser", totalUsers > 0 ?
                    String.format("%.2f", (double) totalTasks / totalUsers) : "0.00");

            // Tags
            metrics.put("totalTags", tagRepository.count());

            return metrics;
        }, "system metrics");
    }

    /**
     * Get admin dashboard
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getAdminDashboard() {
        Map<String, Object> dashboard = new HashMap<>();

        try {
            dashboard.put("platformStats", getPlatformStats());
            dashboard.put("systemMetrics", getSystemMetrics());
            dashboard.put("locationAnalytics", getLocationAnalytics());
            dashboard.put("userGrowth", getUserGrowthStats());
            dashboard.put("recentActivity", getRecentActivity(24)); // Last 24 hours
            dashboard.put("achievementStats", getAchievementStats());
            dashboard.put("taskStats", getTaskStats());
            dashboard.put("tagStats", getTagStats());
            dashboard.put("success", true);
            dashboard.put("timestamp", LocalDateTime.now());

            log.info("Admin dashboard generated successfully");
        } catch (Exception e) {
            log.error("Error generating admin dashboard: {}", e.getMessage(), e);
            dashboard.put("success", false);
            dashboard.put("error", "Ошибка при генерации дашборда: " + e.getMessage());
        }

        return dashboard;
    }

    /**
     * Get real-time statistics
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getRealtimeStats() {
        return createStatsResponse(() -> {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime hourAgo = now.minusHours(1);
            LocalDateTime fifteenMinutesAgo = now.minusMinutes(15);

            List<GameAction> hourActions = gameActionRepository.findByActionTimeBetween(hourAgo, now);
            List<GameAction> recentActions = gameActionRepository.findByActionTimeBetween(fifteenMinutesAgo, now);
            List<UserAchievement> hourAchievements = getAchievementsBetween(hourAgo, now);

            // Using filtering instead of findByCreatedAtBetween
            List<Task> allTasks = taskRepository.findAll();
            List<Task> hourTasks = allTasks.stream()
                    .filter(task -> task.getCreatedAt() != null
                            && !task.getCreatedAt().isBefore(hourAgo)
                            && !task.getCreatedAt().isAfter(now))
                    .collect(Collectors.toList());

            Map<String, Object> stats = new HashMap<>();
            stats.put("timestamp", now);
            stats.put("actionsLastHour", hourActions.size());
            stats.put("usersActiveLastHour", hourActions.stream()
                    .map(action -> action.getUser().getId())
                    .distinct()
                    .count());
            stats.put("achievementsLastHour", hourAchievements.size());
            stats.put("tasksCreatedLastHour", hourTasks.size());
            stats.put("currentlyOnline", recentActions.stream()
                    .map(action -> action.getUser().getId())
                    .distinct()
                    .count());
            stats.put("activeLocations", countActiveLocationsLastHour(hourActions));
            stats.put("popularActionTypes", getPopularActionTypesLastHour(hourActions));

            return stats;
        }, "realtime stats");
    }

    /**
     * Get hourly activity statistics
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getHourlyActivityStats(LocalDate date) {
        return createStatsResponse(() -> {
            LocalDate targetDate = date != null ? date : LocalDate.now();
            LocalDateTime startOfDay = LocalDateTime.of(targetDate, LocalTime.MIN);
            LocalDateTime endOfDay = LocalDateTime.of(targetDate, LocalTime.MAX);

            List<GameAction> actions = gameActionRepository.findByActionTimeBetween(startOfDay, endOfDay);

            Map<Integer, Long> hourlyStats = new TreeMap<>();
            // Initialize all hours from 0 to 23
            for (int hour = 0; hour < 24; hour++) {
                hourlyStats.put(hour, 0L);
            }

            // Count actions by hour
            for (GameAction action : actions) {
                if (action.getActionTime() != null) {
                    int hour = action.getActionTime().getHour();
                    hourlyStats.put(hour, hourlyStats.get(hour) + 1);
                }
            }

            Map<String, Object> response = new HashMap<>();
            response.put("date", targetDate);
            response.put("hourlyStats", hourlyStats);

            long totalActions = hourlyStats.values().stream().mapToLong(Long::longValue).sum();
            Optional<Map.Entry<Integer, Long>> peakHour = hourlyStats.entrySet().stream()
                    .max(Map.Entry.comparingByValue());

            response.put("totalActions", totalActions);
            response.put("peakHour", peakHour.map(e -> String.format("%02d:00", e.getKey())).orElse("N/A"));
            response.put("peakHourCount", peakHour.map(Map.Entry::getValue).orElse(0L));
            response.put("averagePerHour", totalActions / 24.0);

            return response;
        }, "hourly activity stats for date: " + date);
    }

    /**
     * Get task statistics
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getTaskStats() {
        return createStatsResponse(() -> {
            List<Task> allTasks = taskRepository.findAll();

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalTasks", allTasks.size());
            stats.put("tasksByStatus", groupTasksByStatus(allTasks));
            stats.put("tasksByPriority", groupTasksByPriority(allTasks));
            stats.put("averageCompletionTime", calculateAverageCompletionTime(allTasks));
            stats.put("overdueTasks", countOverdueTasks(allTasks));
            stats.put("tasksCreatedToday", countTasksCreatedToday());
            stats.put("topUsersByTasks", getTopUsersByTaskCount(10));

            return stats;
        }, "task statistics");
    }

    /**
     * Get tag statistics
     */
    @Transactional(readOnly = true)
    public Map<String, Object> getTagStats() {
        return createStatsResponse(() -> {
            List<Tag> allTags = tagRepository.findAll();

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalTags", allTags.size());
            stats.put("mostUsedTags", getMostUsedTags(allTags, 10));
            stats.put("tagsByTaskCount", groupTagsByTaskCount(allTags));
            stats.put("averageTasksPerTag", calculateAverageTasksPerTag(allTags));

            return stats;
        }, "tag statistics");
    }

    // ==================== HELPER METHODS ====================

    private Map<String, Object> createStatsResponse(StatsSupplier supplier, String logMessage) {
        Map<String, Object> response = new HashMap<>();
        try {
            response.putAll(supplier.get());
            response.put("success", true);
            response.put("timestamp", LocalDateTime.now());
            log.debug("Successfully retrieved: {}", logMessage);
        } catch (Exception e) {
            log.error("Error retrieving {}: {}", logMessage, e.getMessage(), e);
            response.put("success", false);
            response.put("error", "Ошибка при получении данных: " + e.getMessage());
        }
        return response;
    }

    // Statistics counting methods
    private long getUserCount() {
        try {
            return userRepository.count();
        } catch (Exception e) {
            log.warn("Error getting user count: {}", e.getMessage());
            return 0;
        }
    }

    private long getUserAchievementCount() {
        try {
            return userAchievementRepository.count();
        } catch (Exception e) {
            log.warn("Error getting achievement count: {}", e.getMessage());
            return 0;
        }
    }

    private long getGameActionCount() {
        try {
            return gameActionRepository.count();
        } catch (Exception e) {
            log.warn("Error getting action count: {}", e.getMessage());
            return 0;
        }
    }

    private long getLocationCount() {
        try {
            return locationRepository.count();
        } catch (Exception e) {
            log.warn("Error getting location count: {}", e.getMessage());
            return 0;
        }
    }

    private long countUsersAfter(LocalDateTime date) {
        try {
            return userRepository.findAll().stream()
                    .filter(user -> user.getCreatedAt() != null && user.getCreatedAt().isAfter(date))
                    .count();
        } catch (Exception e) {
            log.warn("Error counting users after {}: {}", date, e.getMessage());
            return 0;
        }
    }

    private long countUsersBefore(LocalDateTime date) {
        try {
            return userRepository.findAll().stream()
                    .filter(user -> user.getCreatedAt() != null && user.getCreatedAt().isBefore(date))
                    .count();
        } catch (Exception e) {
            log.warn("Error counting users before {}: {}", date, e.getMessage());
            return 0;
        }
    }

    private long countActiveUsersLast24h() {
        try {
            LocalDateTime dayAgo = LocalDateTime.now().minusDays(1);
            return gameActionRepository.findAll().stream()
                    .filter(action -> action.getActionTime() != null && action.getActionTime().isAfter(dayAgo))
                    .map(action -> action.getUser().getId())
                    .distinct()
                    .count();
        } catch (Exception e) {
            log.warn("Error counting active users last 24h: {}", e.getMessage());
            return 0;
        }
    }

    private List<UserAchievement> getAchievementsBetween(LocalDateTime start, LocalDateTime end) {
        try {
            return userAchievementRepository.findAll().stream()
                    .filter(ach -> {
                        LocalDateTime achievementDate = ach.getCompletedAt() != null
                                ? ach.getCompletedAt()
                                : ach.getUnlockedAt();
                        return achievementDate != null &&
                                !achievementDate.isBefore(start) &&
                                !achievementDate.isAfter(end);
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("Error getting achievements between {} and {}: {}", start, end, e.getMessage());
            return Collections.emptyList();
        }
    }

    private List<User> getNewUsersBetween(LocalDateTime start, LocalDateTime end) {
        try {
            return userRepository.findAll().stream()
                    .filter(user -> user.getCreatedAt() != null &&
                            !user.getCreatedAt().isBefore(start) &&
                            !user.getCreatedAt().isAfter(end))
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("Error getting new users between {} and {}: {}", start, end, e.getMessage());
            return Collections.emptyList();
        }
    }

    private long countNewUsersToday() {
        try {
            LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
            return countUsersAfter(todayStart);
        } catch (Exception e) {
            log.warn("Error counting new users today: {}", e.getMessage());
            return 0;
        }
    }

    private long countAchievementsToday() {
        try {
            LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
            LocalDateTime now = LocalDateTime.now();
            return getAchievementsBetween(todayStart, now).size();
        } catch (Exception e) {
            log.warn("Error counting achievements today: {}", e.getMessage());
            return 0;
        }
    }

    private long countAchievementsToday(Long userId) {
        try {
            LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
            LocalDateTime now = LocalDateTime.now();

            User user = userRepository.findById(userId).orElse(null);
            if (user == null) return 0;

            List<UserAchievement> userAchievements = userAchievementRepository.findByUser(user);
            return userAchievements.stream()
                    .filter(ach -> {
                        LocalDateTime achievementDate = ach.getCompletedAt() != null
                                ? ach.getCompletedAt()
                                : ach.getUnlockedAt();
                        return achievementDate != null &&
                                !achievementDate.isBefore(todayStart) &&
                                !achievementDate.isAfter(now);
                    })
                    .count();
        } catch (Exception e) {
            log.warn("Error counting achievements today for user {}: {}", userId, e.getMessage());
            return 0;
        }
    }

    private long countCompletedAchievements(List<UserAchievement> achievements) {
        return achievements.stream()
                .filter(ach -> Boolean.TRUE.equals(ach.getCompleted()))
                .count();
    }

    private long countActionsToday() {
        try {
            LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
            LocalDateTime now = LocalDateTime.now();
            return gameActionRepository.findByActionTimeBetween(todayStart, now).size();
        } catch (Exception e) {
            log.warn("Error counting actions today: {}", e.getMessage());
            return 0;
        }
    }

    private long countVisitedLocations() {
        try {
            return locationRepository.findAll().stream()
                    .filter(l -> l.getVisitCount() != null && l.getVisitCount() > 0)
                    .count();
        } catch (Exception e) {
            log.warn("Error counting visited locations: {}", e.getMessage());
            return 0;
        }
    }

    private long countVisitedLocations(Long userId) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) return 0;

            return gameActionRepository.findByUser(user).stream()
                    .map(GameAction::getLocation)
                    .filter(Objects::nonNull)
                    .map(Location::getId)
                    .distinct()
                    .count();
        } catch (Exception e) {
            log.warn("Error counting visited locations for user {}: {}", userId, e.getMessage());
            return 0;
        }
    }

    private Map<String, Object> getMostVisitedLocation(Long userId) {
        try {
            User user = userRepository.findById(userId).orElse(null);
            if (user == null) return null;

            Map<Location, Long> locationCounts = gameActionRepository.findByUser(user).stream()
                    .filter(action -> action.getLocation() != null)
                    .collect(Collectors.groupingBy(
                            GameAction::getLocation,
                            Collectors.counting()
                    ));

            return locationCounts.entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(entry -> {
                        Location loc = entry.getKey();
                        Map<String, Object> result = new HashMap<>();
                        result.put("id", loc.getId());
                        result.put("title", loc.getTitle());
                        result.put("displayName", loc.getDisplayName());
                        result.put("type", loc.getLocationType() != null ?
                                loc.getLocationType().name() : "UNKNOWN");
                        result.put("visitCount", entry.getValue());
                        return result;
                    })
                    .orElse(null);
        } catch (Exception e) {
            log.warn("Error getting most visited location for user {}: {}", userId, e.getMessage());
            return null;
        }
    }

    private int calculateAchievementLevel(List<UserAchievement> achievements) {
        long completedCount = countCompletedAchievements(achievements);
        return (int) (completedCount / 10) + 1;
    }

    private double calculateAchievementCompletionRate(List<UserAchievement> achievements) {
        if (achievements.isEmpty()) return 0.0;
        long totalAchievements = achievementRepository.count();
        if (totalAchievements == 0) return 0.0;
        return (double) achievements.size() / totalAchievements * 100;
    }

    private Map<String, Long> groupActionsByType(List<GameAction> actions) {
        try {
            return actions.stream()
                    .collect(Collectors.groupingBy(
                            GameAction::getActionType,
                            Collectors.counting()
                    ));
        } catch (Exception e) {
            log.warn("Error grouping actions by type: {}", e.getMessage());
            return new HashMap<>();
        }
    }

    private Map<LocalDate, Long> groupActivityByDay(List<GameAction> actions) {
        try {
            return actions.stream()
                    .filter(a -> a.getActionTime() != null)
                    .collect(Collectors.groupingBy(
                            action -> action.getActionTime().toLocalDate(),
                            Collectors.counting()
                    ));
        } catch (Exception e) {
            log.warn("Error grouping activity by day: {}", e.getMessage());
            return new HashMap<>();
        }
    }

    private Map<String, Long> getPopularActions(List<GameAction> actions) {
        try {
            return actions.stream()
                    .collect(Collectors.groupingBy(GameAction::getActionType, Collectors.counting()))
                    .entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .limit(10)
                    .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
        } catch (Exception e) {
            log.warn("Error getting popular actions: {}", e.getMessage());
            return new HashMap<>();
        }
    }

    private String getMostActiveHour(List<GameAction> actions) {
        try {
            return actions.stream()
                    .filter(a -> a.getActionTime() != null)
                    .collect(Collectors.groupingBy(
                            a -> a.getActionTime().getHour(),
                            Collectors.counting()
                    ))
                    .entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(entry -> String.format("%02d:00", entry.getKey()))
                    .orElse("N/A");
        } catch (Exception e) {
            log.warn("Error getting most active hour: {}", e.getMessage());
            return "N/A";
        }
    }

    private List<Map<String, Object>> getTopLocations(List<Location> locations, int limit) {
        try {
            return locations.stream()
                    .filter(l -> l.getVisitCount() != null)
                    .sorted((a, b) -> Long.compare(b.getVisitCount(), a.getVisitCount()))
                    .limit(limit)
                    .map(this::createLocationMap)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("Error getting top locations: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    private Map<String, Object> createLocationMap(Location location) {
        Map<String, Object> map = new HashMap<>();
        try {
            map.put("id", location.getId());
            map.put("title", location.getTitle());
            map.put("displayName", location.getDisplayName());
            map.put("locationType", location.getLocationType() != null ?
                    location.getLocationType().name() : "UNKNOWN");
            map.put("zoneType", location.getZoneType() != null ?
                    location.getZoneType().name() : "UNKNOWN");
            map.put("visitCount", location.getVisitCount() != null ? location.getVisitCount() : 0L);
            map.put("requiredLevel", location.getRequiredLevel() != null ? location.getRequiredLevel() : 0);
            map.put("difficulty", location.getDifficulty() != null ?
                    location.getDifficulty().getDisplayName() : "Неизвестная");
            map.put("isActive", location.isLocationActive());
            map.put("createdAt", location.getCreatedAt());
        } catch (Exception e) {
            log.warn("Error creating location map: {}", e.getMessage());
        }
        return map;
    }

    private Map<String, Long> groupLocationsByType(List<Location> locations) {
        try {
            return locations.stream()
                    .collect(Collectors.groupingBy(
                            location -> location.getLocationType() != null ?
                                    location.getLocationType().name() : "UNKNOWN",
                            Collectors.counting()
                    ));
        } catch (Exception e) {
            log.warn("Error grouping locations by type: {}", e.getMessage());
            return new HashMap<>();
        }
    }

    private Map<String, Long> groupLocationsByZoneType(List<Location> locations) {
        try {
            return locations.stream()
                    .collect(Collectors.groupingBy(
                            location -> location.getZoneType() != null ?
                                    location.getZoneType().name() : "UNKNOWN",
                            Collectors.counting()
                    ));
        } catch (Exception e) {
            log.warn("Error grouping locations by zone type: {}", e.getMessage());
            return new HashMap<>();
        }
    }

    private Map<String, Long> groupLocationsByDifficulty(List<Location> locations) {
        try {
            return locations.stream()
                    .collect(Collectors.groupingBy(
                            location -> location.getDifficulty() != null ?
                                    location.getDifficulty().name() : "UNKNOWN",
                            Collectors.counting()
                    ));
        } catch (Exception e) {
            log.warn("Error grouping locations by difficulty: {}", e.getMessage());
            return new HashMap<>();
        }
    }

    private List<Map<String, Object>> getVisitStats(List<Location> locations) {
        try {
            return locations.stream()
                    .filter(l -> l.getVisitCount() != null && l.getVisitCount() > 0)
                    .sorted((a, b) -> Long.compare(b.getVisitCount(), a.getVisitCount()))
                    .limit(20)
                    .map(this::createLocationMap)
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("Error getting visit stats: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    private double calculateAverageVisits(List<Location> locations) {
        if (locations.isEmpty()) return 0.0;
        long totalVisits = locations.stream()
                .filter(l -> l.getVisitCount() != null)
                .mapToLong(Location::getVisitCount)
                .sum();
        long locationsWithVisits = locations.stream()
                .filter(l -> l.getVisitCount() != null && l.getVisitCount() > 0)
                .count();
        return locationsWithVisits > 0 ? (double) totalVisits / locationsWithVisits : 0.0;
    }

    private String getMostPopularLocationType(List<Location> locations) {
        try {
            return locations.stream()
                    .collect(Collectors.groupingBy(
                            location -> location.getLocationType() != null ?
                                    location.getLocationType().name() : "UNKNOWN",
                            Collectors.counting()
                    ))
                    .entrySet().stream()
                    .max(Map.Entry.comparingByValue())
                    .map(Map.Entry::getKey)
                    .orElse("N/A");
        } catch (Exception e) {
            log.warn("Error getting most popular location type: {}", e.getMessage());
            return "N/A";
        }
    }

    private double calculateAverageActionsPerDay(int days) {
        try {
            LocalDateTime end = LocalDateTime.now();
            LocalDateTime start = end.minusDays(days);

            long totalActions = gameActionRepository.findByActionTimeBetween(start, end).size();
            return (double) totalActions / days;
        } catch (Exception e) {
            log.warn("Error calculating average actions per day: {}", e.getMessage());
            return 0.0;
        }
    }

    private List<Map<String, Object>> getLeaderboardData(String category, int limit) {
        switch (category.toLowerCase()) {
            case "achievements":
                return getAchievementsLeaderboard(limit);
            case "actions":
                return getActionsLeaderboard(limit);
            case "locations":
                return getLocationsLeaderboard(limit);
            case "tasks":
                return getTasksLeaderboard(limit);
            default:
                throw new IllegalArgumentException("Неизвестная категория: " + category);
        }
    }

    private List<Map<String, Object>> getAchievementsLeaderboard(int limit) {
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(user -> {
                    long achievementCount = userAchievementRepository.findByUser(user).size();

                    Map<String, Object> entry = new HashMap<>();
                    entry.put("username", user.getUsername());
                    entry.put("userId", user.getId());
                    entry.put("category", "achievements");
                    entry.put("value", achievementCount);
                    entry.put("completed", userAchievementRepository.findByUser(user).stream()
                            .filter(ach -> Boolean.TRUE.equals(ach.getCompleted()))
                            .count());
                    return entry;
                })
                .sorted((a, b) -> Long.compare((Long) b.get("value"), (Long) a.get("value")))
                .limit(limit)
                .collect(Collectors.toList());
    }

    private List<Map<String, Object>> getActionsLeaderboard(int limit) {
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(user -> {
                    long actionCount = gameActionRepository.findByUser(user).size();

                    Map<String, Object> entry = new HashMap<>();
                    entry.put("username", user.getUsername());
                    entry.put("userId", user.getId());
                    entry.put("category", "actions");
                    entry.put("value", actionCount);
                    entry.put("lastAction", gameActionRepository.findByUser(user).stream()
                            .map(GameAction::getActionTime)
                            .filter(Objects::nonNull)
                            .max(LocalDateTime::compareTo)
                            .orElse(null));
                    return entry;
                })
                .sorted((a, b) -> Long.compare((Long) b.get("value"), (Long) a.get("value")))
                .limit(limit)
                .collect(Collectors.toList());
    }

    private List<Map<String, Object>> getLocationsLeaderboard(int limit) {
        return locationRepository.findAll().stream()
                .filter(l -> l.getVisitCount() != null && l.getVisitCount() > 0)
                .sorted((a, b) -> Long.compare(b.getVisitCount(), a.getVisitCount()))
                .limit(limit)
                .map(location -> {
                    Map<String, Object> entry = new HashMap<>();
                    entry.put("locationId", location.getId());
                    entry.put("title", location.getTitle());
                    entry.put("displayName", location.getDisplayName());
                    entry.put("category", "locations");
                    entry.put("value", location.getVisitCount());
                    entry.put("type", location.getLocationType() != null ?
                            location.getLocationType().name() : "UNKNOWN");
                    entry.put("difficulty", location.getDifficulty() != null ?
                            location.getDifficulty().name() : "UNKNOWN");
                    return entry;
                })
                .collect(Collectors.toList());
    }

    private List<Map<String, Object>> getTasksLeaderboard(int limit) {
        List<User> users = userRepository.findAll();

        return users.stream()
                .map(user -> {
                    long taskCount = taskRepository.findByUserId(user.getId()).size();

                    Map<String, Object> entry = new HashMap<>();
                    entry.put("username", user.getUsername());
                    entry.put("userId", user.getId());
                    entry.put("category", "tasks");
                    entry.put("value", taskCount);
                    entry.put("completed", taskRepository.findByUserId(user.getId()).stream()
                            .filter(task -> task.getStatus() == Task.Status.COMPLETED)
                            .count());
                    return entry;
                })
                .sorted((a, b) -> Long.compare((Long) b.get("value"), (Long) a.get("value")))
                .limit(limit)
                .collect(Collectors.toList());
    }

    private Map<String, Object> getUserGrowthStats() {
        try {
            LocalDateTime now = LocalDateTime.now();
            long usersNow = getUserCount();

            long usersWeekAgo = countUsersBefore(now.minusWeeks(1));
            long usersMonthAgo = countUsersBefore(now.minusMonths(1));

            Map<String, Object> growth = new HashMap<>();
            growth.put("totalUsers", usersNow);
            growth.put("weeklyGrowth", Math.max(0, usersNow - usersWeekAgo));
            growth.put("monthlyGrowth", Math.max(0, usersNow - usersMonthAgo));
            growth.put("weeklyGrowthRate", usersWeekAgo > 0 ?
                    (double) Math.max(0, usersNow - usersWeekAgo) / usersWeekAgo * 100 : 0);
            growth.put("monthlyGrowthRate", usersMonthAgo > 0 ?
                    (double) Math.max(0, usersNow - usersMonthAgo) / usersMonthAgo * 100 : 0);

            return growth;
        } catch (Exception e) {
            log.warn("Error getting user growth stats: {}", e.getMessage());
            Map<String, Object> errorStats = new HashMap<>();
            errorStats.put("totalUsers", 0L);
            errorStats.put("weeklyGrowth", 0L);
            errorStats.put("monthlyGrowth", 0L);
            errorStats.put("weeklyGrowthRate", 0.0);
            errorStats.put("monthlyGrowthRate", 0.0);
            return errorStats;
        }
    }

    private Map<String, Object> getRecentActivity(int hours) {
        try {
            LocalDateTime start = LocalDateTime.now().minusHours(hours);
            LocalDateTime end = LocalDateTime.now();

            List<GameAction> actions = gameActionRepository.findByActionTimeBetween(start, end);
            List<UserAchievement> achievements = getAchievementsBetween(start, end);

            // Using filtering instead of findByCreatedAtBetween
            List<Task> allTasks = taskRepository.findAll();
            List<Task> tasks = allTasks.stream()
                    .filter(task -> task.getCreatedAt() != null
                            && !task.getCreatedAt().isBefore(start)
                            && !task.getCreatedAt().isAfter(end))
                    .collect(Collectors.toList());

            List<Map<String, Object>> recentActions = actions.stream()
                    .sorted((a, b) -> b.getActionTime().compareTo(a.getActionTime()))
                    .limit(20)
                    .map(this::createActionMap)
                    .collect(Collectors.toList());

            Map<String, Object> activity = new HashMap<>();
            activity.put("periodHours", hours);
            activity.put("totalActions", actions.size());
            activity.put("totalAchievements", achievements.size());
            activity.put("totalTasks", tasks.size());
            activity.put("activeUsers", actions.stream()
                    .map(action -> action.getUser().getId())
                    .distinct()
                    .count());
            activity.put("recentActions", recentActions);

            return activity;
        } catch (Exception e) {
            log.warn("Error getting recent activity: {}", e.getMessage());
            return new HashMap<>();
        }
    }

    private Map<String, Object> createActionMap(GameAction action) {
        Map<String, Object> map = new HashMap<>();
        try {
            map.put("id", action.getId());
            map.put("actionType", action.getActionType());
            map.put("actionTime", action.getActionTime());
            map.put("userId", action.getUser() != null ? action.getUser().getId() : null);
            map.put("username", action.getUser() != null ? action.getUser().getUsername() : null);
            map.put("locationId", action.getLocation() != null ? action.getLocation().getId() : null);
            map.put("locationName", action.getLocation() != null ? action.getLocation().getTitle() : null);
            map.put("status", action.getStatus());
        } catch (Exception e) {
            log.warn("Error creating action map: {}", e.getMessage());
        }
        return map;
    }

    private Map<String, Object> getAchievementStats() {
        try {
            long totalAchievements = achievementRepository.count();
            long userAchievements = userAchievementRepository.count();
            long completedAchievements = userAchievementRepository.findAll().stream()
                    .filter(ach -> Boolean.TRUE.equals(ach.getCompleted()))
                    .count();

            Map<String, Object> stats = new HashMap<>();
            stats.put("totalAvailable", totalAchievements);
            stats.put("totalEarned", userAchievements);
            stats.put("totalCompleted", completedAchievements);
            stats.put("completionRate", totalAchievements > 0 ?
                    (double) completedAchievements / totalAchievements * 100 : 0);
            stats.put("averagePerUser", getUserCount() > 0 ?
                    (double) userAchievements / getUserCount() : 0);

            // Most popular achievements
            List<Map<String, Object>> mostPopular = achievementRepository.findAll().stream()
                    .sorted((a, b) -> Long.compare(
                            userAchievementRepository.findByAchievement(b).size(),
                            userAchievementRepository.findByAchievement(a).size()))
                    .limit(5)
                    .map(ach -> {
                        Map<String, Object> achievementMap = new HashMap<>();
                        achievementMap.put("id", ach.getId());
                        achievementMap.put("name", ach.getAchievementName());
                        achievementMap.put("displayName", ach.getDisplayName());
                        achievementMap.put("earnedBy", userAchievementRepository.findByAchievement(ach).size());
                        return achievementMap;
                    })
                    .collect(Collectors.toList());

            stats.put("mostPopular", mostPopular);

            return stats;
        } catch (Exception e) {
            log.warn("Error getting achievement stats: {}", e.getMessage());
            return new HashMap<>();
        }
    }

    // Task statistics methods
    private long countActiveTasks(List<Task> tasks) {
        return tasks.stream()
                .filter(task -> task.getStatus() == Task.Status.PENDING ||
                        task.getStatus() == Task.Status.IN_PROGRESS)
                .count();
    }

    private long countCompletedTasks(List<Task> tasks) {
        return tasks.stream()
                .filter(task -> task.getStatus() == Task.Status.COMPLETED)
                .count();
    }

    private long countTasksCreatedToday() {
        try {
            LocalDateTime todayStart = LocalDateTime.of(LocalDate.now(), LocalTime.MIN);
            LocalDateTime now = LocalDateTime.now();

            // Using filtering instead of findByCreatedAtBetween
            List<Task> allTasks = taskRepository.findAll();
            return allTasks.stream()
                    .filter(task -> task.getCreatedAt() != null
                            && !task.getCreatedAt().isBefore(todayStart)
                            && !task.getCreatedAt().isAfter(now))
                    .count();
        } catch (Exception e) {
            log.warn("Error counting tasks created today: {}", e.getMessage());
            return 0;
        }
    }

    private long countOverdueTasks(List<Task> tasks) {
        return tasks.stream()
                .filter(Task::isOverdue)
                .count();
    }

    private Map<String, Long> groupTasksByStatus(List<Task> tasks) {
        try {
            return tasks.stream()
                    .collect(Collectors.groupingBy(
                            task -> task.getStatus() != null ? task.getStatus().name() : "UNKNOWN",
                            Collectors.counting()
                    ));
        } catch (Exception e) {
            log.warn("Error grouping tasks by status: {}", e.getMessage());
            return new HashMap<>();
        }
    }

    private Map<String, Long> groupTasksByPriority(List<Task> tasks) {
        try {
            return tasks.stream()
                    .collect(Collectors.groupingBy(
                            task -> task.getPriority() != null ? task.getPriority().name() : "UNKNOWN",
                            Collectors.counting()
                    ));
        } catch (Exception e) {
            log.warn("Error grouping tasks by priority: {}", e.getMessage());
            return new HashMap<>();
        }
    }

    private double calculateAverageCompletionTime(List<Task> tasks) {
        List<Task> completedTasks = tasks.stream()
                .filter(task -> task.getStatus() == Task.Status.COMPLETED &&
                        task.getCreatedAt() != null &&
                        task.getCompletedAt() != null)
                .collect(Collectors.toList());

        if (completedTasks.isEmpty()) return 0.0;

        long totalSeconds = completedTasks.stream()
                .mapToLong(task -> java.time.Duration.between(
                        task.getCreatedAt(), task.getCompletedAt()).getSeconds())
                .sum();

        return (double) totalSeconds / completedTasks.size() / 3600.0; // In hours
    }

    private List<Map<String, Object>> getTopUsersByTaskCount(int limit) {
        return userRepository.findAll().stream()
                .map(user -> {
                    long taskCount = taskRepository.findByUserId(user.getId()).size();

                    Map<String, Object> entry = new HashMap<>();
                    entry.put("userId", user.getId());
                    entry.put("username", user.getUsername());
                    entry.put("taskCount", taskCount);
                    entry.put("completedTasks", taskRepository.findByUserId(user.getId()).stream()
                            .filter(task -> task.getStatus() == Task.Status.COMPLETED)
                            .count());
                    return entry;
                })
                .sorted((a, b) -> Long.compare((Long) b.get("taskCount"), (Long) a.get("taskCount")))
                .limit(limit)
                .collect(Collectors.toList());
    }

    // Tag statistics methods
    private List<Map<String, Object>> getMostUsedTags(List<Tag> tags, int limit) {
        return tags.stream()
                .sorted((a, b) -> Integer.compare(b.getTasks().size(), a.getTasks().size()))
                .limit(limit)
                .map(tag -> {
                    Map<String, Object> entry = new HashMap<>();
                    entry.put("id", tag.getId());
                    entry.put("name", tag.getName());
                    entry.put("color", tag.getColor());
                    entry.put("taskCount", tag.getTasks().size());
                    return entry;
                })
                .collect(Collectors.toList());
    }

    private Map<String, Integer> groupTagsByTaskCount(List<Tag> tags) {
        return tags.stream()
                .collect(Collectors.groupingBy(
                        tag -> {
                            int count = tag.getTasks().size();
                            if (count == 0) return "0";
                            if (count <= 5) return "1-5";
                            if (count <= 10) return "6-10";
                            if (count <= 20) return "11-20";
                            return "21+";
                        },
                        Collectors.summingInt(tag -> 1)
                ));
    }

    private double calculateAverageTasksPerTag(List<Tag> tags) {
        if (tags.isEmpty()) return 0.0;
        int totalTasks = tags.stream()
                .mapToInt(tag -> tag.getTasks().size())
                .sum();
        return (double) totalTasks / tags.size();
    }

    private long countActiveLocationsLastHour(List<GameAction> actions) {
        try {
            return actions.stream()
                    .filter(action -> action.getLocation() != null)
                    .map(action -> action.getLocation().getId())
                    .distinct()
                    .count();
        } catch (Exception e) {
            log.warn("Error counting active locations last hour: {}", e.getMessage());
            return 0;
        }
    }

    private List<Map<String, Object>> getPopularActionTypesLastHour(List<GameAction> actions) {
        try {
            return actions.stream()
                    .collect(Collectors.groupingBy(GameAction::getActionType, Collectors.counting()))
                    .entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .limit(5)
                    .map(entry -> {
                        Map<String, Object> map = new HashMap<>();
                        map.put("actionType", entry.getKey());
                        map.put("count", entry.getValue());
                        return map;
                    })
                    .collect(Collectors.toList());
        } catch (Exception e) {
            log.warn("Error getting popular action types: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    // ==================== FUNCTIONAL INTERFACE ====================

    @FunctionalInterface
    private interface StatsSupplier {
        Map<String, Object> get() throws Exception;
    }
}