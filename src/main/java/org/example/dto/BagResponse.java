package org.example.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class BagResponse {

    // Basic information
    private Long id;
    private String name;
    private String displayName;
    private String description;
    private String lore;

    // Type and characteristics
    private String bagType; // INVENTORY, BANK, GUILD_BANK, SPECIAL
    private String bagSubtype; // BACKPACK, QUIVER, HERB_POUCH, etc
    private String rarity;
    private String quality;

    // Dimensions and capacity
    private Integer totalSlots;
    private Integer usedSlots;
    private Integer baseSlots;
    private Integer expansionSlots;
    private Integer maxExpansionSlots;

    // Statistics
    private Integer itemCount;
    private Integer valuableItemCount;
    private Double totalWeight;
    private Double weightReduction; // % weight reduction

    // Visual
    private String iconUrl;
    private String modelUrl;
    private String colorHex;
    private Map<String, Object> appearance;

    // Requirements
    private Integer levelRequirement;
    private String classRequirement;
    private String skillRequirement;
    private Integer bagSlotRequirement; // Which bag slot it occupies

    // Properties
    private Boolean isSoulbound;
    private Boolean isTradeable;
    private Boolean isSellable;
    private Boolean isDroppable;
    private Boolean isExpandable;
    private Boolean isLocked;
    private Boolean isEquipped;

    // Filters and restrictions
    private List<String> allowedItemTypes; // Which item types can be placed
    private List<String> restrictedItemTypes; // Which item types cannot be placed
    private Integer maxItemLevel;

    // Upgrades
    private Map<String, Object> enchantments;
    private Map<String, Object> sockets;
    private Map<String, Object> modifications;

    // Timestamps
    private LocalDateTime acquiredAt;
    private LocalDateTime equippedAt;
    private LocalDateTime lastOpenedAt;

    // Additional data
    private Integer bagIndex; // Position in interface
    private Integer sortOrder;
    private Map<String, Object> metadata;

    // Contents
    private List<InventoryItemResponse> items;

    // Calculated fields
    public Integer getFreeSlots() {
        return totalSlots - usedSlots;
    }

    public boolean isFull() {
        return getFreeSlots() <= 0;
    }

    public boolean isEmpty() {
        return usedSlots == 0 || itemCount == 0;
    }

    public Double getFillPercentage() {
        if (totalSlots == null || totalSlots == 0) {
            return 0.0;
        }
        return (usedSlots.doubleValue() / totalSlots.doubleValue()) * 100;
    }

    public boolean canAcceptItemType(String itemType) {
        if (allowedItemTypes == null || allowedItemTypes.isEmpty()) {
            return true; // No restrictions
        }
        return allowedItemTypes.contains(itemType);
    }

    public boolean isRestrictedItemType(String itemType) {
        if (restrictedItemTypes == null || restrictedItemTypes.isEmpty()) {
            return false;
        }
        return restrictedItemTypes.contains(itemType);
    }

    public boolean canBeExpanded() {
        return isExpandable && expansionSlots < maxExpansionSlots;
    }
}