package org.example.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Table(name = "achievements", uniqueConstraints = {
        @UniqueConstraint(columnNames = {"achievement_code"})
})
public class Achievement {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "achievement_code", nullable = false, unique = true, length = 50)
    private String achievementCode;

    @Column(name = "achievement_name", nullable = false, length = 100)
    private String achievementName;

    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "lore", columnDefinition = "TEXT")
    private String lore;

    // Classification
    @Column(name = "achievement_type", length = 50)
    @Builder.Default
    private String achievementType = "ACHIEVEMENT";

    @Column(name = "category", length = 50)
    private String category;

    @Column(name = "subcategory", length = 50)
    private String subcategory;

    @Column(name = "difficulty", length = 20)
    @Builder.Default
    private String difficulty = "NORMAL";

    // Requirements
    @Column(name = "requirements", columnDefinition = "JSON")
    private String requirements;

    @Column(name = "points_required")
    @Builder.Default
    private Integer pointsRequired = 1;

    @Column(name = "prerequisite_achievement_id")
    private Long prerequisiteAchievementId;

    // Progress
    @Column(name = "progress_max")
    @Builder.Default
    private Integer progressMax = 100;

    @Column(name = "progress_text", length = 100)
    private String progressText;

    // Rewards
    @Column(name = "reward_points")
    @Builder.Default
    private Integer rewardPoints = 10;

    @Column(name = "reward_title", length = 100)
    private String rewardTitle;

    @Column(name = "reward_badge", length = 100)
    private String rewardBadge;

    @Column(name = "reward_items", columnDefinition = "JSON")
    private String rewardItems;

    @Column(name = "reward_currency", columnDefinition = "JSON")
    private String rewardCurrency;

    @Column(name = "reward_stats", columnDefinition = "JSON")
    private String rewardStats;

    @Column(name = "reward_experience")
    private Integer rewardExperience;

    @Column(name = "reward_gold")
    private Integer rewardGold;

    // Visual
    @Column(name = "icon_url", length = 255)
    private String iconUrl;

    @Column(name = "icon_unlocked_url", length = 255)
    private String iconUnlockedUrl;

    @Column(name = "background_image", length = 255)
    private String backgroundImage;

    // Properties
    @Column(name = "is_secret")
    @Builder.Default
    private Boolean isSecret = false;

    @Column(name = "is_hidden")
    @Builder.Default
    private Boolean isHidden = false;

    @Column(name = "is_account_wide")
    @Builder.Default
    private Boolean isAccountWide = false;

    @Column(name = "is_featured")
    @Builder.Default
    private Boolean isFeatured = false;

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

    public boolean isActive() {
        return isActive != null && isActive;
    }
}