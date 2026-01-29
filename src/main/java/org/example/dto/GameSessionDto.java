package org.example.dto;

import lombok.Data;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameSessionDto {
    private Long id;
    private Long userId;
    private String username;
    private Long currentLocationId;
    private String currentLocationName;
    private Integer currentHealth;
    private Integer currentGold;
    private Set<Long> visitedLocations;
    private Integer actionCount;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String status;
    private Long durationMinutes;
}