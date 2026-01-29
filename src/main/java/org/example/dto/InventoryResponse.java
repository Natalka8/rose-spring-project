package org.example.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class InventoryResponse {
    private Integer totalSlots;
    private Integer usedSlots;
    private Integer gold;
    private Integer diamonds;
    private List<InventoryItemResponse> items;
    private Map<String, EquipmentSlotResponse> equipment;
    private List<BagResponse> bags;
}