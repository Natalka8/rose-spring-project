package org.example.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
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
@JsonInclude(JsonInclude.Include.NON_NULL)
public class InventoryItemResponse {

    private Long id;
    private Long itemId;
    private String itemCode;
    private String name;
    private String displayName;
    private String description;
    private String itemType;
    private String itemSubtype;
    private String rarity;
    private String quality;

    // Quantity and condition
    private Integer quantity;
    private Integer maxStackSize;
    private Integer durabilityCurrent;
    private Integer durabilityMax;
    private Integer chargesCurrent;
    private Integer chargesMax;

    // Item statistics
    private Map<String, Integer> stats;
    private Map<String, Object> randomStats;
    private Map<String, Object> customStats;

    // Modifiers
    private Boolean isEquipped;
    private Boolean isBound;
    private Boolean isSoulbound;
    private String boundTo;
    private Boolean isTradeable;
    private Boolean isSellable;

    // Slots and position
    private String slotType; // INVENTORY, EQUIPMENT, BANK
    private Integer slotNumber;
    private Integer bagIndex;
    private String equipmentSlot; // HEAD, CHEST, LEGS, WEAPON, etc

    // Upgrades
    private Map<String, Object> enchantments;
    private Map<String, Object> socketedGems;
    private Map<String, Object> reforges;

    // Timestamps
    private LocalDateTime acquiredAt;
    private LocalDateTime equippedAt;
    private LocalDateTime lastUsedAt;
    private LocalDateTime expiresAt;

    // Visual
    private String iconUrl;
    private String modelUrl;
    private String colorHex;
    private Map<String, Object> particleEffects;

    // Flags
    private Boolean isQuestItem;
    private Boolean isConsumable;
    private Boolean isUsable;
    private Boolean isUnique;

    // Commercial
    private Integer vendorPrice;
    private Integer auctionPrice;
    private Integer premiumPrice;

    // Weight and size
    private Double weight;
    private Integer sizeX;
    private Integer sizeY;

    // Additional data
    private Map<String, Object> metadata;
    private String uniqueInstanceId;

    // Calculated fields
    public boolean isDamaged() {
        return durabilityMax != null && durabilityCurrent != null &&
                durabilityCurrent < durabilityMax;
    }

    public boolean isBroken() {
        return durabilityCurrent != null && durabilityCurrent <= 0;
    }

    public Integer getDurabilityPercentage() {
        if (durabilityMax == null || durabilityCurrent == null || durabilityMax == 0) {
            return 100;
        }
        return (durabilityCurrent * 100) / durabilityMax;
    }

    public boolean isStackable() {
        return maxStackSize != null && maxStackSize > 1;
    }

    public boolean canBeEquipped() {
        return equipmentSlot != null && !equipmentSlot.isEmpty();
    }
}