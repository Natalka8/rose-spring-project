package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EquipmentSlotResponse {
    private String slotName;
    private String slotType;
    private Boolean isLocked;
    private InventoryItemResponse equippedItem;
    private String requiredClass;
    private Integer requiredLevel;
    private String iconUrl;
}