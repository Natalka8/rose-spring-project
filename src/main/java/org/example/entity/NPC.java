package org.example.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Table(name = "npcs")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class NPC {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "npc_code", nullable = false, unique = true, length = 50)
    private String npcCode;

    @Column(name = "npc_name", nullable = false, length = 100)
    private String npcName;

    @Column(name = "display_name", nullable = false, length = 100)
    private String displayName;

    @Column(name = "description", columnDefinition = "TEXT")
    private String description;

    @Column(name = "lore", columnDefinition = "TEXT")
    private String lore;

    @Column(name = "dialogue", columnDefinition = "TEXT")
    private String dialogue;

    // Classification
    @Column(name = "npc_type", length = 50)
    @Builder.Default
    private String npcType = "VENDOR"; // VENDOR, QUEST_GIVER, TRAINER, GUARD, BANKER

    @Column(name = "npc_subtype", length = 50)
    private String npcSubtype;

    @Column(name = "faction", length = 50)
    private String faction;

    @Column(name = "ai_type", length = 50)
    @Builder.Default
    private String aiType = "STATIC";

    // Services
    @Column(name = "services", columnDefinition = "JSON")
    private String services; // {vendor: true, repair: true, bank: true}

    @Column(name = "vendor_items", columnDefinition = "JSON")
    private String vendorItems;

    @Column(name = "repair_items", columnDefinition = "JSON")
    private String repairItems;

    @Column(name = "trainer_skills", columnDefinition = "JSON")
    private String trainerSkills;

    @Column(name = "quests_offered", columnDefinition = "JSON")
    private String questsOffered;

    // Dialogue
    @Column(name = "dialogue_tree", columnDefinition = "JSON")
    private String dialogueTree;

    @Column(name = "dialogue_options", columnDefinition = "JSON")
    private String dialogueOptions;

    @Column(name = "greeting_text", columnDefinition = "TEXT")
    private String greetingText;

    @Column(name = "farewell_text", columnDefinition = "TEXT")
    private String farewellText;

    // Location
    @Column(name = "spawn_location_id")
    private Long spawnLocationId;

    @Column(name = "spawn_zone_id")
    private Long spawnZoneId;

    @Column(name = "spawn_coordinates", columnDefinition = "JSON")
    private String spawnCoordinates;

    @Column(name = "patrol_route", columnDefinition = "JSON")
    private String patrolRoute;

    // Visual
    @Column(name = "model_url", length = 255)
    private String modelUrl;

    @Column(name = "appearance", columnDefinition = "JSON")
    private String appearance;

    @Column(name = "animation_set", columnDefinition = "JSON")
    private String animationSet;

    // Interaction
    @Column(name = "interaction_range")
    @Builder.Default
    private Integer interactionRange = 5;

    @Column(name = "interaction_time_ms")
    @Builder.Default
    private Integer interactionTimeMs = 0;

    @Column(name = "is_tradable")
    @Builder.Default
    private Boolean isTradable = true;

    @Column(name = "is_attackable")
    @Builder.Default
    private Boolean isAttackable = false;

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