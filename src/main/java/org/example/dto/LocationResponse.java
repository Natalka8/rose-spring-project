package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LocationResponse {
    private Long id;
    private String name;
    private String displayName;
    private String description;
    private String type;
    private Integer minLevel;
    private Integer maxLevel;
    private Boolean isSafeZone;
    private Boolean isPvpEnabled;
    private Integer playerCount;
    private Boolean isUnlocked;
}