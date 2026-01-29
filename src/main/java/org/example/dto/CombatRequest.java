package org.example.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CombatRequest {
    @NotNull
    private Long monsterId;

    private Long skillId;
    private Integer positionX;
    private Integer positionY;
}