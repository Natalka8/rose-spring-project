package org.example.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "achievement_templates")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AchievementTemplate {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "achievement_id", unique = true, nullable = false, length = 100)
    private String achievementId;

    @Column(nullable = false, length = 100)
    private String name;

    @Column(length = 500)
    private String description;

    @Column(name = "icon_url", length = 255)
    private String iconUrl;

    @Column(name = "points")
    @Builder.Default
    private Integer points = 10;

    @Column(name = "category", length = 50)
    private String category;

    @Column(name = "is_secret")
    @Builder.Default
    private Boolean isSecret = false;

    @Column(name = "goal")
    @Builder.Default
    private Integer goal = 100;

    @Column(name = "type", length = 50)
    private String type;

    @Column(name = "criteria", columnDefinition = "TEXT")
    private String criteria;

    @Column(name = "reward_type", length = 50)
    private String rewardType;

    @Column(name = "reward_value")
    private Integer rewardValue;

    @Column(name = "prerequisite_id", length = 100)
    private String prerequisiteId;

    @Column(name = "is_repeatable")
    @Builder.Default
    private Boolean isRepeatable = false;

    @Column(name = "max_completions")
    @Builder.Default
    private Integer maxCompletions = 1;

    @Column(name = "cooldown_minutes")
    @Builder.Default
    private Integer cooldownMinutes = 0;

    @Column(name = "required_level")
    @Builder.Default
    private Integer requiredLevel = 1;

    @Column(name = "class_restriction", length = 50)
    private String classRestriction;

    @Column(name = "faction_restriction", length = 50)
    private String factionRestriction;

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    @Column(name = "version")
    @Builder.Default
    private Integer version = 1;

    public boolean isActive() {
        return isActive != null && isActive;
    }
}