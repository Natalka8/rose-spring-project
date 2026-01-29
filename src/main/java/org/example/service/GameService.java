package org.example.service;

import org.example.dto.*;
import java.util.List;

public interface GameService {

    // Character
    CharacterResponse getCharacterInfo();
    CharacterResponse travelToLocation(Long locationId);
    CharacterResponse rest();

    // Inventory and equipment
    InventoryResponse getInventory();
    List<EquipmentSlotResponse> getEquipment();
    InventoryResponse equipItem(Long itemId);
    InventoryResponse unequipItem(String slotId);

    // Locations
    List<LocationResponse> getAvailableLocations();

    // Skills
    List<SkillResponse> getSkills();
    GameActionDto useSkill(Long skillId);

    // Combat
    GameActionDto attackMonster(Long monsterId);
    GameActionDto fleeFromCombat();

    // Quests
    List<TaskResponse> getAvailableTasks();
    List<TaskResponse> getActiveTasks();
    TaskResponse acceptTask(Long taskId);
    GameActionDto completeTask(Long taskId);
    void abandonTask(Long taskId);

    // Game session
    GameSessionDto getGameSession();
    void saveGame();

    // General action
    GameActionDto performAction(GameActionDto action);
}