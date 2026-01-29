package org.example.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.actuate.health.Health;
import org.springframework.boot.actuate.health.HealthIndicator;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class DatabaseHealthIndicator implements HealthIndicator {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public Health health() {
        try {
            Integer result = jdbcTemplate.queryForObject("SELECT 1", Integer.class);
            if (result != null && result == 1) {
                return Health.up()
                        .withDetail("database", "MySQL")
                        .withDetail("status", "available")
                        .withDetail("message", "Database connection is healthy")
                        .build();
            } else {
                return Health.down()
                        .withDetail("database", "MySQL")
                        .withDetail("error", "Unexpected query result")
                        .build();
            }
        } catch (Exception e) {
            return Health.down()
                    .withDetail("database", "MySQL")
                    .withDetail("error", e.getMessage())
                    .withDetail("exception", e.getClass().getName())
                    .build();
        }
    }

    // Additional method for checking specific tables
    public Health checkTable(String tableName) {
        try {
            Integer count = jdbcTemplate.queryForObject(
                    "SELECT COUNT(*) FROM " + tableName,
                    Integer.class
            );
            return Health.up()
                    .withDetail("table", tableName)
                    .withDetail("rowCount", count)
                    .build();
        } catch (Exception e) {
            return Health.down()
                    .withDetail("table", tableName)
                    .withDetail("error", e.getMessage())
                    .build();
        }
    }
}