package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CharacterResponse {
    private Long id;
    private String name;
    private String characterClass;
    private String race;
    private Integer level;
    private Long experience;
    private Long experienceToNextLevel;
    private Integer health;
    private Integer maxHealth;
    private Integer mana;
    private Integer maxMana;
    private Map<String, Integer> attributes;
    private Map<String, Integer> combatStats;
    private Map<String, Object> appearance;
    private LocalDateTime createdDate;
    private Integer playTimeHours;
}