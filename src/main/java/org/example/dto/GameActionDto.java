package org.example.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;
import jakarta.validation.constraints.NotBlank; // Changed
import jakarta.validation.constraints.NotNull;  // Changed
import jakarta.validation.constraints.Size;     // Changed
import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GameActionDto {

    @NotNull(message = "ID пользователя не может быть пустым")
    private Long userId;

    @NotNull(message = "ID сессии не может быть пустым")
    private Long sessionId;

    @NotBlank(message = "Тип действия не может быть пустым")
    @Size(max = 50, message = "Тип действия не может превышать 50 символов")
    private String actionType; // MOVE, USE_ITEM, BUY, SELL, ATTACK, DEFEND, etc.

    @NotBlank(message = "Код действия не может быть пустым")
    @Size(max = 100, message = "Код действия не может превышать 100 символов")
    private String actionCode; // Specific action, for example: "go_north", "use_potion"

    @Size(max = 500, message = "Описание не может превышать 500 символов")
    private String description;

    // Action targets
    private Long targetLocationId;
    private Long targetUserId;
    private String targetItemId;
    private Long targetNpcId;

    // Action parameters
    private Map<String, Object> parameters;

    // Action coordinates (if applicable)
    private Integer coordinateX;
    private Integer coordinateY;

    // Timestamps
    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    private LocalDateTime serverTimestamp;

    // Action results (populated by server)
    private Boolean success;
    private String resultMessage;
    private Integer experienceGained;
    private Integer goldGained;
    private Integer healthChange;
    private Map<String, Integer> itemsGained;
    private Map<String, Integer> itemsLost;

    // Technical fields
    private String deviceId;
    private String ipAddress;
    private Long latency; // Response time in ms
    private String version; // Client version

    /**
     * Validates basic fields
     */
    public boolean isValid() {
        return userId != null && userId > 0 &&
                sessionId != null && sessionId > 0 &&
                actionType != null && !actionType.trim().isEmpty() &&
                actionCode != null && !actionCode.trim().isEmpty();
    }

    /**
     * Checks if action is a movement action
     */
    public boolean isMovementAction() {
        return "MOVE".equalsIgnoreCase(actionType) ||
                actionCode.startsWith("go_") ||
                actionCode.startsWith("move_");
    }

    /**
     * Checks if action is an item use action
     */
    public boolean isItemUseAction() {
        return "USE_ITEM".equalsIgnoreCase(actionType) ||
                actionCode.startsWith("use_");
    }

    /**
     * Checks if action is a purchase action
     */
    public boolean isPurchaseAction() {
        return "BUY".equalsIgnoreCase(actionType) ||
                "PURCHASE".equalsIgnoreCase(actionType);
    }

    /**
     * Gets parameter as string
     */
    public String getParameterAsString(String key) {
        if (parameters == null) return null;
        Object value = parameters.get(key);
        return value != null ? value.toString() : null;
    }

    /**
     * Gets parameter as number
     */
    public Integer getParameterAsInteger(String key) {
        if (parameters == null) return null;
        Object value = parameters.get(key);
        if (value instanceof Number) {
            return ((Number) value).intValue();
        } else if (value instanceof String) {
            try {
                return Integer.parseInt((String) value);
            } catch (NumberFormatException e) {
                return null;
            }
        }
        return null;
    }

    /**
     * Gets parameter as Boolean
     */
    public Boolean getParameterAsBoolean(String key) {
        if (parameters == null) return null;
        Object value = parameters.get(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else if (value instanceof String) {
            return Boolean.parseBoolean((String) value);
        }
        return null;
    }

    /**
     * Creates DTO for successful action
     */
    public static GameActionDto createSuccessResult(GameActionDto request,
                                                    String message,
                                                    Integer exp,
                                                    Integer gold) {
        return GameActionDto.builder()
                .userId(request.getUserId())
                .sessionId(request.getSessionId())
                .actionType(request.getActionType())
                .actionCode(request.getActionCode())
                .description(request.getDescription())
                .success(true)
                .resultMessage(message)
                .experienceGained(exp)
                .goldGained(gold)
                .timestamp(request.getTimestamp())
                .serverTimestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Creates DTO for unsuccessful action
     */
    public static GameActionDto createFailureResult(GameActionDto request, String errorMessage) {
        return GameActionDto.builder()
                .userId(request.getUserId())
                .sessionId(request.getSessionId())
                .actionType(request.getActionType())
                .actionCode(request.getActionCode())
                .description(request.getDescription())
                .success(false)
                .resultMessage(errorMessage)
                .timestamp(request.getTimestamp())
                .serverTimestamp(LocalDateTime.now())
                .build();
    }

    /**
     * Masks confidential data for logs
     */
    public GameActionDto getMaskedCopy() {
        return GameActionDto.builder()
                .userId(userId)
                .sessionId(sessionId)
                .actionType(actionType)
                .actionCode(actionCode)
                .description(description)
                .targetLocationId(targetLocationId)
                .targetUserId(targetUserId)
                .targetItemId(targetItemId)
                .targetNpcId(targetNpcId)
                .parameters(parameters) // Parameters may contain important game information
                .coordinateX(coordinateX)
                .coordinateY(coordinateY)
                .timestamp(timestamp)
                .serverTimestamp(serverTimestamp)
                .success(success)
                .resultMessage(resultMessage)
                .experienceGained(experienceGained)
                .goldGained(goldGained)
                .healthChange(healthChange)
                .itemsGained(itemsGained)
                .itemsLost(itemsLost)
                .deviceId(deviceId != null ? "***" + deviceId.substring(Math.max(0, deviceId.length() - 4)) : null)
                .ipAddress(maskIpAddress(ipAddress))
                .latency(latency)
                .version(version)
                .build();
    }

    private String maskIpAddress(String ip) {
        if (ip == null) return null;
        String[] parts = ip.split("\\.");
        if (parts.length == 4) {
            return parts[0] + "." + parts[1] + ".***.***";
        }
        return "***";
    }
}