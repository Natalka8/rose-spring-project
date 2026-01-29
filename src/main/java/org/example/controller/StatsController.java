package org.example.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.service.StatsService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/stats")
@RequiredArgsConstructor
@Slf4j
public class StatsController {

    private final StatsService statsService;

    /**
     * Get overall statistics
     */
    @GetMapping("/overall")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<Map<String, Object>> getOverallStats() {
        log.info("Request for overall statistics");
        try {
            Map<String, Object> stats = statsService.getOverallStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error getting overall statistics: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get overall statistics"));
        }
    }

    /**
     * Get summary statistics (using overall statistics)
     */
    @GetMapping("/summary")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<Map<String, Object>> getSummaryStats() {
        log.info("Request for summary statistics");
        try {
            Map<String, Object> stats = statsService.getOverallStats(); // Use existing method
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error getting summary statistics: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get summary statistics"));
        }
    }

    /**
     * Get detailed user statistics
     */
    @GetMapping("/users/{userId}")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR') or @securityService.isCurrentUser(#userId)")
    public ResponseEntity<Map<String, Object>> getUserStats(@PathVariable Long userId) {
        log.info("Request for detailed statistics for user ID: {}", userId);
        try {
            Map<String, Object> stats = statsService.getUserDetailedStats(userId);
            return ResponseEntity.ok(stats);
        } catch (RuntimeException e) {
            log.warn("User not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        } catch (Exception e) {
            log.error("Error getting user statistics: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get user statistics"));
        }
    }

    /**
     * Get user comparative statistics
     */
    @GetMapping("/users/{userId}/comparison")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR') or @securityService.isCurrentUser(#userId)")
    public ResponseEntity<Map<String, Object>> getUserComparativeStats(@PathVariable Long userId) {
        log.info("Request for comparative statistics for user ID: {}", userId);
        try {
            Map<String, Object> stats = statsService.getUserStats(userId); // Use existing method
            return ResponseEntity.ok(stats);
        } catch (RuntimeException e) {
            log.warn("User not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        } catch (Exception e) {
            log.error("Error getting comparative statistics: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get comparative statistics"));
        }
    }

    /**
     * Get time series statistics
     */
    @GetMapping("/time-series")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<Map<String, Object>> getTimeSeriesStats(
            @RequestParam(defaultValue = "daily") String period,
            @RequestParam(required = false) Integer days) {
        log.info("Request for time series statistics for period: {}, days: {}", period, days);
        try {
            Map<String, Object> stats = statsService.getTimeSeriesStats(period, days);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error getting time series statistics: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get time series statistics"));
        }
    }

    /**
     * Get action type statistics
     */
    @GetMapping("/actions")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<Map<String, Object>> getActionStats(
            @RequestParam(required = false) String actionType,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("Request for action statistics for type: {}, from: {}, to: {}",
                actionType, startDate, endDate);

        try {
            LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
            LocalDateTime endDateTime = endDate != null ? endDate.atTime(23, 59, 59) : null;

            Map<String, Object> stats = statsService.getActionTypeStats(actionType, startDateTime, endDateTime);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error getting action statistics: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get action statistics"));
        }
    }

    /**
     * Get location statistics
     */
    @GetMapping("/locations")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<Map<String, Object>> getLocationStats() {
        log.info("Request for location statistics");
        try {
            Map<String, Object> stats = statsService.getLocationStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error getting location statistics: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get location statistics"));
        }
    }

    /**
     * Get achievement statistics
     */
    @GetMapping("/achievements")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<Map<String, Object>> getAchievementStats() {
        log.info("Request for achievement statistics");
        try {
            Map<String, Object> stats = statsService.getAchievementStats();
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error getting achievement statistics: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get achievement statistics"));
        }
    }

    /**
     * Get retention statistics (stub)
     */
    @GetMapping("/retention")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<Map<String, Object>> getRetentionStats(
            @RequestParam(required = false, defaultValue = "30") Integer cohortDays) {
        log.info("Request for retention statistics for cohort days: {}", cohortDays);
        try {
            Map<String, Object> stats = Map.of(
                    "message", "Retention statistics functionality under development",
                    "cohortDays", cohortDays,
                    "timestamp", LocalDateTime.now()
            );
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error getting retention statistics: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get retention statistics"));
        }
    }

    /**
     * Get monetization statistics (stub)
     */
    @GetMapping("/monetization")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<Map<String, Object>> getMonetizationStats() {
        log.info("Request for monetization statistics");
        try {
            Map<String, Object> stats = Map.of(
                    "message", "Monetization statistics functionality under development",
                    "timestamp", LocalDateTime.now()
            );
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error getting monetization statistics: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get monetization statistics"));
        }
    }

    /**
     * Export statistics to CSV (stub)
     */
    @GetMapping("/export/csv")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<byte[]> exportStatsToCsv(@RequestParam String statType) {
        log.info("Export statistics {} to CSV", statType);

        try {
            String csvContent = "Statistics;Value\n" +
                    "Service;StatsService\n" +
                    "Statistic type;" + statType + "\n" +
                    "Export time;" + LocalDateTime.now() + "\n" +
                    "Status;Under development";

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType("text/csv"));
            headers.setContentDispositionFormData("attachment",
                    String.format("%s_stats_%s.csv", statType, LocalDateTime.now().toString()));
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");

            return new ResponseEntity<>(csvContent.getBytes(), headers, HttpStatus.OK);

        } catch (Exception e) {
            log.error("Error exporting statistics to CSV: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .header("Content-Type", "application/json")
                    .body(("{\"error\": \"Failed to export statistics: " + e.getMessage() + "\"}").getBytes());
        }
    }

    /**
     * Quick statistics for dashboard
     */
    @GetMapping("/dashboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'USER')")
    public ResponseEntity<Map<String, Object>> getDashboardStats() {
        log.info("Request for dashboard statistics");
        try {
            Map<String, Object> stats = statsService.getOverallStats(); // Use overall statistics
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error getting dashboard statistics: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get dashboard statistics"));
        }
    }

    /**
     * Personal user statistics
     */
    @GetMapping("/personal")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<Map<String, Object>> getPersonalStats(
            @RequestParam(required = false) Long userId) {

        log.info("Request for personal statistics for user ID: {}", userId);

        try {
            if (userId == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of(
                                "success", false,
                                "error", "User ID is required"
                        ));
            }

            Map<String, Object> stats = statsService.getPersonalStats(userId);
            return ResponseEntity.ok(stats);
        } catch (RuntimeException e) {
            log.warn("User not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        } catch (Exception e) {
            log.error("Error getting personal statistics: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get personal statistics"));
        }
    }

    /**
     * Game session statistics
     */
    @GetMapping("/sessions")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<Map<String, Object>> getSessionStats(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {

        log.info("Request for session statistics from: {} to: {}", startDate, endDate);

        try {
            LocalDateTime startDateTime = startDate != null ? startDate.atStartOfDay() : null;
            LocalDateTime endDateTime = endDate != null ? endDate.atTime(23, 59, 59) : null;

            Map<String, Object> stats = statsService.getSessionStats(startDateTime, endDateTime);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error getting session statistics: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get session statistics"));
        }
    }

    /**
     * Get leaderboard
     */
    @GetMapping("/leaderboard")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR', 'USER')")
    public ResponseEntity<Map<String, Object>> getLeaderboard(
            @RequestParam(defaultValue = "level") String criteria,
            @RequestParam(defaultValue = "10") Integer limit) {

        log.info("Request for leaderboard by criteria: {}, limit: {}", criteria, limit);

        try {
            Map<String, Object> stats = statsService.getLeaderboard(criteria, limit);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error getting leaderboard: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get leaderboard"));
        }
    }

    /**
     * Get user game statistics
     */
    @GetMapping("/users/{userId}/game")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR') or @securityService.isCurrentUser(#userId)")
    public ResponseEntity<Map<String, Object>> getUserGameStats(@PathVariable Long userId) {
        log.info("Request for game statistics for user ID: {}", userId);
        try {
            Map<String, Object> stats = statsService.getUserStats(userId); // Use existing method
            return ResponseEntity.ok(stats);
        } catch (RuntimeException e) {
            log.warn("User not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        } catch (Exception e) {
            log.error("Error getting game statistics: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get game statistics"));
        }
    }

    /**
     * Get user playtime statistics
     */
    @GetMapping("/users/{userId}/playtime")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR') or @securityService.isCurrentUser(#userId)")
    public ResponseEntity<Map<String, Object>> getUserPlayTime(@PathVariable Long userId) {
        log.info("Request for playtime statistics for user ID: {}", userId);
        try {
            Map<String, Object> stats = statsService.getUserStats(userId); // Use existing method
            return ResponseEntity.ok(stats);
        } catch (RuntimeException e) {
            log.warn("User not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        } catch (Exception e) {
            log.error("Error getting playtime statistics: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get playtime statistics"));
        }
    }

    /**
     * Get user achievements
     */
    @GetMapping("/users/{userId}/achievements")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR') or @securityService.isCurrentUser(#userId)")
    public ResponseEntity<Map<String, Object>> getUserAchievements(@PathVariable Long userId) {
        log.info("Request for achievements for user ID: {}", userId);
        try {
            Map<String, Object> stats = statsService.getPersonalStats(userId); // Use personal statistics
            return ResponseEntity.ok(stats);
        } catch (RuntimeException e) {
            log.warn("User not found: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "User not found"));
        } catch (Exception e) {
            log.error("Error getting achievements: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get achievements"));
        }
    }

    /**
     * Get average statistics across all users
     */
    @GetMapping("/average")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<Map<String, Object>> getAverageStats() {
        log.info("Request for average statistics");
        try {
            Map<String, Object> stats = statsService.getOverallStats(); // Use overall statistics
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error getting average statistics: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get average statistics"));
        }
    }

    /**
     * Get hourly activity statistics
     */
    @GetMapping("/activity/hourly")
    @PreAuthorize("hasAnyRole('ADMIN', 'MODERATOR')")
    public ResponseEntity<Map<String, Object>> getHourlyActivity(
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate date) {

        LocalDate targetDate = date != null ? date : LocalDate.now();
        log.info("Request for hourly activity for date: {}", targetDate);

        try {
            Map<String, Object> stats = statsService.getTimeSeriesStats("daily", 1); // Use time series
            stats.put("date", targetDate);
            return ResponseEntity.ok(stats);
        } catch (Exception e) {
            log.error("Error getting hourly activity: {}", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Failed to get hourly activity"));
        }
    }

    /**
     * Service health check
     */
    @GetMapping("/health")
    public ResponseEntity<Map<String, Object>> healthCheck() {
        Map<String, Object> response = new HashMap<>();
        response.put("status", "UP");
        response.put("service", "stats-service");
        response.put("timestamp", LocalDateTime.now());
        return ResponseEntity.ok(response);
    }
}