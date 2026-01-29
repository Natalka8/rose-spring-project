package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SkillResponse {
    private Long id;
    private String name;
    private String description;
    private String type;
    private Integer level;
    private Integer maxLevel;
    private Integer manaCost;
    private Integer cooldown;
    private Integer currentCooldown;
    private Integer damage;
    private Integer healing;
    private String iconUrl;
    private Boolean isUnlocked;
    private Boolean canUpgrade;
}