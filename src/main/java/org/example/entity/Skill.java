package org.example.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "skills")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Skill {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "skill_code", nullable = false, unique = true, length = 50)
    private String skillCode;

    @Column(name = "skill_name", nullable = false, length = 100)
    private String skillName;

    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    // Classification
    @Column(name = "skill_type", nullable = false, length = 50)
    private String skillType; // ACTIVE, PASSIVE, AURA, TARGETED, AREA

    @Column(name = "skill_category", length = 50)
    private String skillCategory; // COMBAT, MAGIC, SUPPORT, CRAFTING, GATHERING

    @Column(name = "school", length = 50)
    private String school; // FIRE, FROST, ARCANE, HOLY, SHADOW

    @Column(name = "class_restriction", length = 50)
    private String classRestriction;

    @Column(name = "race_restriction", length = 50)
    private String raceRestriction;

    // Requirements
    @Column(name = "level_requirement")
    @Builder.Default
    private Integer levelRequirement = 1;

    @Column(name = "skill_points_required")
    @Builder.Default
    private Integer skillPointsRequired = 1;

    @Column(name = "prerequisite_skill_id")
    private Long prerequisiteSkillId;

    @Column(name = "prerequisite_skill_level")
    private Integer prerequisiteSkillLevel;

    // Resources
    @Column(name = "mana_cost")
    @Builder.Default
    private Integer manaCost = 0;

    @Column(name = "health_cost")
    @Builder.Default
    private Integer healthCost = 0;

    @Column(name = "stamina_cost")
    @Builder.Default
    private Integer staminaCost = 0;

    @Column(name = "energy_cost")
    @Builder.Default
    private Integer energyCost = 0;

    @Column(name = "item_cost", columnDefinition = "JSON")
    private String itemCost;

    @Column(name = "gold_cost")
    @Builder.Default
    private Integer goldCost = 0;

    // Time and cooldowns
    @Column(name = "cast_time_ms")
    @Builder.Default
    private Integer castTimeMs = 0;

    @Column(name = "channel_time_ms")
    @Builder.Default
    private Integer channelTimeMs = 0;

    @Column(name = "cooldown_ms")
    @Builder.Default
    private Integer cooldownMs = 0;

    @Column(name = "global_cooldown_ms")
    @Builder.Default
    private Integer globalCooldownMs = 1500;

    @Column(name = "duration_ms")
    private Integer durationMs;

    // Range and area
    @Column(name = "range_min")
    @Builder.Default
    private Integer rangeMin = 0;

    @Column(name = "range_max")
    @Builder.Default
    private Integer rangeMax = 30;

    @Column(name = "area_radius")
    private Integer areaRadius;

    @Column(name = "area_shape", length = 20)
    private String areaShape; // CIRCLE, CONE, LINE, RECTANGLE

    @Column(name = "max_targets")
    @Builder.Default
    private Integer maxTargets = 1;

    // Effects
    @Column(name = "effects", columnDefinition = "JSON")
    private String effects;

    @Column(name = "scaling", columnDefinition = "JSON")
    private String scaling; // {strength: 0.5, intelligence: 1.0}

    @Column(name = "modifiers", columnDefinition = "JSON")
    private String modifiers;

    @Column(name = "proc_chance", precision = 5)
    private Double procChance;

    @Column(name = "proc_effects", columnDefinition = "JSON")
    private String procEffects;

    // Visual effects
    @Column(name = "icon_url", length = 255)
    private String iconUrl;

    @Column(name = "cast_effect", length = 100)
    private String castEffect;

    @Column(name = "hit_effect", length = 100)
    private String hitEffect;

    @Column(name = "sound_effect", length = 100)
    private String soundEffect;

    @Column(name = "animation_name", length = 100)
    private String animationName;

    // Statistics
    @Column(name = "base_damage")
    private Integer baseDamage;

    @Column(name = "base_healing")
    private Integer baseHealing;

    @Column(name = "damage_multiplier", precision = 5)
    @Builder.Default
    private Double damageMultiplier = 1.0;

    @Column(name = "healing_multiplier", precision = 5)
    @Builder.Default
    private Double healingMultiplier = 1.0;

    @Column(name = "critical_chance_bonus", precision = 5)
    private Double criticalChanceBonus;

    @Column(name = "critical_damage_bonus", precision = 5)
    private Double criticalDamageBonus;

    // Upgrades
    @Column(name = "max_level")
    @Builder.Default
    private Integer maxLevel = 1;

    @Column(name = "level_increment", columnDefinition = "JSON")
    private String levelIncrement;

    @Column(name = "rank_increment", columnDefinition = "JSON")
    private String rankIncrement;

    // PVP
    @Column(name = "pvp_enabled")
    @Builder.Default
    private Boolean pvpEnabled = true;

    @Column(name = "pvp_damage_multiplier", precision = 5)
    @Builder.Default
    private Double pvpDamageMultiplier = 1.0;

    @Column(name = "pvp_cooldown_multiplier", precision = 5)
    @Builder.Default
    private Double pvpCooldownMultiplier = 1.0;

    // Metadata
    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "is_hidden")
    @Builder.Default
    private Boolean isHidden = false;

    @Column(name = "version")
    @Builder.Default
    private Integer version = 1;

    @Column(name = "created_by", length = 50)
    private String createdBy;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}