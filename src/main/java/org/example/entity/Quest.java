package org.example.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "quests")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Quest {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "quest_code", nullable = false, unique = true, length = 50)
    private String questCode;

    @Column(name = "quest_name", nullable = false, length = 100)
    private String questName;

    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "lore", columnDefinition = "TEXT")
    private String lore;

    @Column(name = "story_text", columnDefinition = "TEXT")
    private String storyText;

    // Classification
    @Column(name = "quest_type", length = 50)
    @Builder.Default
    private String questType = "MAIN"; // MAIN, SIDE, DAILY, WEEKLY, EVENT, CHAIN

    @Column(name = "quest_category", length = 50)
    private String questCategory; // STORY, HUNTING, GATHERING, DELIVERY, ESCORT, DUNGEON

    @Column(name = "difficulty", length = 20)
    @Builder.Default
    private String difficulty = "NORMAL";

    @Column(name = "zone_id")
    private Long zoneId;

    @Column(name = "location_id")
    private Long locationId;

    // Requirements
    @Column(name = "level_requirement")
    @Builder.Default
    private Integer levelRequirement = 1;

    @Column(name = "level_recommended")
    private Integer levelRecommended;

    @Column(name = "class_requirement", length = 50)
    private String classRequirement;

    @Column(name = "race_requirement", length = 50)
    private String raceRequirement;

    @Column(name = "faction_requirement", length = 50)
    private String factionRequirement;

    @Column(name = "prerequisite_quest_id")
    private Long prerequisiteQuestId;

    @Column(name = "prerequisite_achievement_id")
    private Long prerequisiteAchievementId;

    // Objectives
    @Column(name = "objectives", columnDefinition = "JSON")
    private String objectives;

    @Column(name = "objective_types", columnDefinition = "JSON")
    private String objectiveTypes;

    @Column(name = "progress_requirements", columnDefinition = "JSON")
    private String progressRequirements;

    // Rewards
    @Column(name = "rewards", columnDefinition = "JSON")
    private String rewards;

    @Column(name = "choice_rewards", columnDefinition = "JSON")
    private String choiceRewards;

    @Column(name = "bonus_rewards", columnDefinition = "JSON")
    private String bonusRewards;

    @Column(name = "reputation_rewards", columnDefinition = "JSON")
    private String reputationRewards;

    // Time restrictions
    @Column(name = "time_limit_hours")
    private Integer timeLimitHours;

    @Column(name = "daily_reset")
    @Builder.Default
    private Boolean dailyReset = false;

    @Column(name = "weekly_reset")
    @Builder.Default
    private Boolean weeklyReset = false;

    @Column(name = "event_start")
    private LocalDateTime eventStart;

    @Column(name = "event_end")
    private LocalDateTime eventEnd;

    // NPC and dialogues
    @Column(name = "start_npc_id")
    private Long startNpcId;

    @Column(name = "end_npc_id")
    private Long endNpcId;

    @Column(name = "dialogue_start", columnDefinition = "TEXT")
    private String dialogueStart;

    @Column(name = "dialogue_end", columnDefinition = "TEXT")
    private String dialogueEnd;

    @Column(name = "dialogue_progress", columnDefinition = "TEXT")
    private String dialogueProgress;

    // Properties
    @Column(name = "is_repeatable")
    @Builder.Default
    private Boolean isRepeatable = false;

    @Column(name = "repeat_delay_hours")
    private Integer repeatDelayHours;

    @Column(name = "max_completions")
    private Integer maxCompletions;

    @Column(name = "is_shared")
    @Builder.Default
    private Boolean isShared = false;

    @Column(name = "share_range")
    @Builder.Default
    private Integer shareRange = 100;

    // Flags
    @Column(name = "is_epic")
    @Builder.Default
    private Boolean isEpic = false;

    @Column(name = "is_legendary")
    @Builder.Default
    private Boolean isLegendary = false;

    @Column(name = "is_hidden")
    @Builder.Default
    private Boolean isHidden = false;

    @Column(name = "is_auto_accept")
    @Builder.Default
    private Boolean isAutoAccept = false;

    @Column(name = "is_auto_complete")
    @Builder.Default
    private Boolean isAutoComplete = false;

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