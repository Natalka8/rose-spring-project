package org.example.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "monsters")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Monster {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "monster_code", nullable = false, unique = true, length = 50)
    private String monsterCode;

    @Column(name = "monster_name", nullable = false, length = 100)
    private String monsterName;

    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "lore", columnDefinition = "TEXT")
    private String lore;

    // Classification
    @Column(name = "monster_type", length = 50)
    @Builder.Default
    private String monsterType = "NORMAL"; // NORMAL, ELITE, BOSS, MINIBOSS, WORLD_BOSS

    @Column(name = "monster_family", length = 50)
    private String monsterFamily; // BEAST, UNDEAD, DEMON, ELEMENTAL, HUMANOID

    @Column(name = "faction", length = 50)
    private String faction;

    @Column(name = "ai_type", length = 50)
    @Builder.Default
    private String aiType = "AGGRESSIVE"; // PASSIVE, AGGRESSIVE, NEUTRAL, FRIENDLY

    // Levels and health
    @Column(name = "level")
    @Builder.Default
    private Integer level = 1;

    @Column(name = "health_min")
    @Builder.Default
    private Integer healthMin = 100;

    @Column(name = "health_max")
    @Builder.Default
    private Integer healthMax = 100;

    @Column(name = "health_regen")
    @Builder.Default
    private Integer healthRegen = 1;

    // Combat characteristics
    @Column(name = "damage_min")
    @Builder.Default
    private Integer damageMin = 10;

    @Column(name = "damage_max")
    @Builder.Default
    private Integer damageMax = 20;

    // FIXED: removed scale for Double
    @Column(name = "attack_speed", precision = 5) // REMOVED scale = 2
    @Builder.Default
    private Double attackSpeed = 2.0;

    @Column(name = "attack_range")
    @Builder.Default
    private Integer attackRange = 2;

    @Column(name = "armor")
    @Builder.Default
    private Integer armor = 0;

    @Column(name = "magic_resist")
    @Builder.Default
    private Integer magicResist = 0;

    // Resistances
    @Column(name = "resistances", columnDefinition = "JSON")
    private String resistances; // {fire: 0.5, frost: 0.25}

    @Column(name = "immunities", columnDefinition = "JSON")
    private String immunities;

    @Column(name = "vulnerabilities", columnDefinition = "JSON")
    private String vulnerabilities;

    // Behavior
    @Column(name = "behavior_pattern", columnDefinition = "JSON")
    private String behaviorPattern;

    @Column(name = "patrol_route", columnDefinition = "JSON")
    private String patrolRoute;

    @Column(name = "spawn_conditions", columnDefinition = "JSON")
    private String spawnConditions;

    @Column(name = "despawn_conditions", columnDefinition = "JSON")
    private String despawnConditions;

    // Loot
    @Column(name = "loot_table_id")
    private Long lootTableId;

    // FIXED: removed scale for Double
    @Column(name = "drop_chance_multiplier", precision = 5) // REMOVED scale = 2
    @Builder.Default
    private Double dropChanceMultiplier = 1.0;

    @Column(name = "gold_min")
    @Builder.Default
    private Integer goldMin = 0;

    @Column(name = "gold_max")
    @Builder.Default
    private Integer goldMax = 10;

    @Column(name = "experience_reward")
    @Builder.Default
    private Integer experienceReward = 50;

    // Spawn
    @Column(name = "spawn_location_id")
    private Long spawnLocationId;

    @Column(name = "spawn_zone_id")
    private Long spawnZoneId;

    @Column(name = "respawn_time_seconds")
    @Builder.Default
    private Integer respawnTimeSeconds = 60;

    @Column(name = "max_spawn_count")
    @Builder.Default
    private Integer maxSpawnCount = 1;

    // Skills and abilities
    @Column(name = "skills", columnDefinition = "JSON")
    private String skills;

    @Column(name = "abilities", columnDefinition = "JSON")
    private String abilities;

    @Column(name = "special_attacks", columnDefinition = "JSON")
    private String specialAttacks;

    // Visual
    @Column(name = "model_url", length = 255)
    private String modelUrl;

    // FIXED: removed scale for Double
    @Column(name = "scale", precision = 5) // REMOVED scale = 2
    @Builder.Default
    private Double scale = 1.0;

    @Column(name = "particle_effects", columnDefinition = "JSON")
    private String particleEffects;

    @Column(name = "sound_effects", columnDefinition = "JSON")
    private String soundEffects;

    // Metadata
    @Column(name = "version")
    @Builder.Default
    private Integer version = 1;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}