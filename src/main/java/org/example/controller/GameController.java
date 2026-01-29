package org.example.controller;

import org.example.dto.*;
import org.example.service.GameService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/v1/game")
@RequiredArgsConstructor
@Tag(name = "Game", description = "Main game endpoints")
@SecurityRequirement(name = "bearerAuth")
public class GameController {

    private final GameService gameService;

    // ========== CHARACTER ==========

    @GetMapping("/character")
    @Operation(summary = "Get character information")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<CharacterResponse>> getCharacterInfo() {
        CharacterResponse character = gameService.getCharacterInfo();
        return ResponseEntity.ok(ApiResponse.success("Character information retrieved", character));
    }

    // ========== INVENTORY AND EQUIPMENT ==========

    @GetMapping("/inventory")
    @Operation(summary = "Get inventory")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<InventoryResponse>> getInventory() {
        InventoryResponse inventory = gameService.getInventory();
        return ResponseEntity.ok(ApiResponse.success("Inventory retrieved", inventory));
    }

    @GetMapping("/equipment")
    @Operation(summary = "Get equipment")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<EquipmentSlotResponse>>> getEquipment() {
        List<EquipmentSlotResponse> equipment = gameService.getEquipment();
        return ResponseEntity.ok(ApiResponse.success("Equipment retrieved", equipment));
    }

    @PostMapping("/equip/{itemId}")
    @Operation(summary = "Equip item")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<InventoryResponse>> equipItem(@PathVariable Long itemId) {
        InventoryResponse response = gameService.equipItem(itemId);
        return ResponseEntity.ok(ApiResponse.success("Item equipped", response));
    }

    @PostMapping("/unequip/{slotId}")
    @Operation(summary = "Unequip item")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<InventoryResponse>> unequipItem(@PathVariable String slotId) {
        InventoryResponse response = gameService.unequipItem(slotId);
        return ResponseEntity.ok(ApiResponse.success("Item unequipped", response));
    }

    // ========== LOCATIONS ==========

    @GetMapping("/locations")
    @Operation(summary = "Get available locations")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<LocationResponse>>> getAvailableLocations() {
        List<LocationResponse> locations = gameService.getAvailableLocations();
        return ResponseEntity.ok(ApiResponse.success("Available locations retrieved", locations));
    }

    @PostMapping("/travel/{locationId}")
    @Operation(summary = "Travel to location")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<CharacterResponse>> travelToLocation(@PathVariable Long locationId) {
        CharacterResponse response = gameService.travelToLocation(locationId);
        return ResponseEntity.ok(ApiResponse.success("Travel completed", response));
    }

    // ========== SKILLS ==========

    @GetMapping("/skills")
    @Operation(summary = "Get character skills")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<SkillResponse>>> getSkills() {
        List<SkillResponse> skills = gameService.getSkills();
        return ResponseEntity.ok(ApiResponse.success("Skills retrieved", skills));
    }

    @PostMapping("/skills/{skillId}/use")
    @Operation(summary = "Use skill")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<GameActionDto>> useSkill(@PathVariable Long skillId) {
        GameActionDto response = gameService.useSkill(skillId);
        return ResponseEntity.ok(ApiResponse.success("Skill used", response));
    }

    // ========== COMBAT ==========

    @PostMapping("/combat/attack/{monsterId}")
    @Operation(summary = "Attack monster")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<GameActionDto>> attackMonster(@PathVariable Long monsterId) {
        GameActionDto response = gameService.attackMonster(monsterId);
        return ResponseEntity.ok(ApiResponse.success("Attack executed", response));
    }

    @PostMapping("/combat/flee")
    @Operation(summary = "Attempt to flee")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<GameActionDto>> fleeFromCombat() {
        GameActionDto response = gameService.fleeFromCombat();
        return ResponseEntity.ok(ApiResponse.success("Escape attempt", response));
    }

    // ========== TASKS/QUESTS ==========

    @GetMapping("/tasks/available")
    @Operation(summary = "Get available tasks")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getAvailableTasks() {
        List<TaskResponse> tasks = gameService.getAvailableTasks();
        return ResponseEntity.ok(ApiResponse.success("Available tasks retrieved", tasks));
    }

    @GetMapping("/tasks/active")
    @Operation(summary = "Get active tasks")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<List<TaskResponse>>> getActiveTasks() {
        List<TaskResponse> tasks = gameService.getActiveTasks();
        return ResponseEntity.ok(ApiResponse.success("Active tasks retrieved", tasks));
    }

    @PostMapping("/tasks/{taskId}/accept")
    @Operation(summary = "Accept task")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<TaskResponse>> acceptTask(@PathVariable Long taskId) {
        TaskResponse response = gameService.acceptTask(taskId);
        return ResponseEntity.ok(ApiResponse.success("Task accepted", response));
    }

    @PostMapping("/tasks/{taskId}/complete")
    @Operation(summary = "Complete task")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<GameActionDto>> completeTask(@PathVariable Long taskId) {
        GameActionDto response = gameService.completeTask(taskId);
        return ResponseEntity.ok(ApiResponse.success("Task completed", response));
    }

    @PostMapping("/tasks/{taskId}/abandon")
    @Operation(summary = "Abandon task")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<String>> abandonTask(@PathVariable Long taskId) { // Changed to String
        gameService.abandonTask(taskId);
        return ResponseEntity.ok(ApiResponse.success("Task abandoned", "Task successfully abandoned"));
    }

    // ========== REST ==========

    @PostMapping("/rest")
    @Operation(summary = "Rest")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<CharacterResponse>> rest() {
        CharacterResponse response = gameService.rest();
        return ResponseEntity.ok(ApiResponse.success("Rest completed", response));
    }

    // ========== GAME SESSION ==========

    @GetMapping("/session")
    @Operation(summary = "Get game session")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<GameSessionDto>> getGameSession() {
        GameSessionDto session = gameService.getGameSession();
        return ResponseEntity.ok(ApiResponse.success("Game session retrieved", session));
    }

    @PostMapping("/session/save")
    @Operation(summary = "Save game")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<String>> saveGame() { // Changed to String
        gameService.saveGame();
        return ResponseEntity.ok(ApiResponse.success("Game saved", "Game successfully saved"));
    }

    // ========== SYSTEM ==========

    @GetMapping("/health")
    @Operation(summary = "Service health check")
    public ResponseEntity<ApiResponse<String>> healthCheck() {
        return ResponseEntity.ok(ApiResponse.success("Service active", "Game service is running"));
    }

    @PostMapping("/action")
    @Operation(summary = "Perform game action")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ApiResponse<GameActionDto>> performAction(@Valid @RequestBody GameActionDto action) {
        GameActionDto response = gameService.performAction(action);
        return ResponseEntity.ok(ApiResponse.success("Action performed", response));
    }
}