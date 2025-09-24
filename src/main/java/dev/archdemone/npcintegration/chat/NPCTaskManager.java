package dev.archdemone.npcintegration.chat;

import dev.archdemone.npcintegration.NPCIntegrationPlugin;
import dev.archdemone.npcintegration.managers.NPCManager;
import dev.archdemone.npcintegration.integrations.CitizensIntegration;
import dev.archdemone.npcintegration.integrations.MythicMobsIntegration;
import dev.archdemone.npcintegration.integrations.ModelEngineIntegration;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;

/**
 * Manages task execution for NPCs based on chat commands
 */
public class NPCTaskManager {
    
    private final NPCIntegrationPlugin plugin;
    private final NPCManager npcManager;
    private final NPCChatSystem chatSystem;
    private final Map<Integer, List<NPCTask>> activeTasks;
    
    public NPCTaskManager(NPCIntegrationPlugin plugin, NPCChatSystem chatSystem) {
        this.plugin = plugin;
        this.npcManager = plugin.getNPCManager();
        this.chatSystem = chatSystem;
        this.activeTasks = new HashMap<>();
    }
    
    /**
     * Assigns a task to an NPC based on chat command
     */
    public void assignTask(NPCManager.NPCDetails details, String taskType, Player player) {
        NPC npc = details.getCitizensNPC();
        if (npc == null || !npc.isSpawned()) {
            return;
        }
        
        List<NPCTask> npcTasks = activeTasks.computeIfAbsent(details.getCitizensId(), k -> new ArrayList<>());
        
        switch (taskType.toLowerCase()) {
            case "woodcutting":
                startWoodcuttingTask(details, player);
                break;
            case "planting":
                startPlantingTask(details, player);
                break;
            case "following":
                startFollowingTask(details, player);
                break;
            case "repairing":
                startRepairingTask(details, player);
                break;
            case "smithing":
                startSmithingTask(details, player);
                break;
            default:
                plugin.getLogger().warning("Unknown task type: " + taskType);
        }
    }
    
    /**
     * Starts a woodcutting task
     */
    private void startWoodcuttingTask(NPCManager.NPCDetails details, Player player) {
        new BukkitRunnable() {
            private int taskDuration = 0;
            private int treesChopped = 0;
            
            @Override
            public void run() {
                if (!isNPCActive(details)) {
                    cancel();
                    return;
                }
                
                taskDuration++;
                
                // Look for trees every 5 seconds
                if (taskDuration % 100 == 0) {
                    Location treeLocation = findNearbyTree(details);
                    if (treeLocation != null) {
                        chopTree(details, treeLocation);
                        treesChopped++;
                    }
                }
                
                // Play chopping animation every 2 seconds
                if (taskDuration % 40 == 0) {
                    playChoppingAnimation(details);
                }
                
                // Report progress every 30 seconds
                if (taskDuration % 600 == 0) {
                    sendTaskUpdate(details, "I've chopped " + treesChopped + " trees so far!");
                }
                
                // Stop task after 5 minutes or if player moves far away
                if (taskDuration >= 6000 || isPlayerTooFar(details, player)) {
                    sendTaskUpdate(details, "I've finished woodcutting! I chopped " + treesChopped + " trees total.");
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 20L, 1L);
    }
    
    /**
     * Starts a tree planting task
     */
    private void startPlantingTask(NPCManager.NPCDetails details, Player player) {
        new BukkitRunnable() {
            private int taskDuration = 0;
            private int saplingsPlanted = 0;
            
            @Override
            public void run() {
                if (!isNPCActive(details)) {
                    cancel();
                    return;
                }
                
                taskDuration++;
                
                // Look for suitable planting spots every 10 seconds
                if (taskDuration % 200 == 0) {
                    Location plantLocation = findPlantingSpot(details);
                    if (plantLocation != null) {
                        plantSapling(details, plantLocation);
                        saplingsPlanted++;
                    }
                }
                
                // Play planting animation every 3 seconds
                if (taskDuration % 60 == 0) {
                    playPlantingAnimation(details);
                }
                
                // Report progress every 30 seconds
                if (taskDuration % 600 == 0) {
                    sendTaskUpdate(details, "I've planted " + saplingsPlanted + " saplings so far!");
                }
                
                // Stop task after 5 minutes
                if (taskDuration >= 6000) {
                    sendTaskUpdate(details, "I've finished planting! I planted " + saplingsPlanted + " saplings total.");
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 20L, 1L);
    }
    
    /**
     * Starts a following task
     */
    private void startFollowingTask(NPCManager.NPCDetails details, Player player) {
        new BukkitRunnable() {
            private int followDuration = 0;
            
            @Override
            public void run() {
                if (!isNPCActive(details) || isPlayerTooFar(details, player)) {
                    sendTaskUpdate(details, "I've lost you! I'll stop following.");
                    cancel();
                    return;
                }
                
                followDuration++;
                
                // Follow player every 2 seconds
                if (followDuration % 40 == 0) {
                    CitizensIntegration citizensIntegration = plugin.getCitizensIntegration();
                    if (citizensIntegration != null) {
                        citizensIntegration.makeNPCFollow(details.getCitizensNPC(), player);
                    }
                }
                
                // Play walking animation
                if (followDuration % 20 == 0) {
                    playWalkingAnimation(details);
                }
                
                // Stop following after 10 minutes
                if (followDuration >= 12000) {
                    sendTaskUpdate(details, "I'm getting tired of following. I'll stay here for now.");
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 20L, 1L);
    }
    
    /**
     * Starts a repairing task
     */
    private void startRepairingTask(NPCManager.NPCDetails details, Player player) {
        new BukkitRunnable() {
            private int taskDuration = 0;
            
            @Override
            public void run() {
                if (!isNPCActive(details)) {
                    cancel();
                    return;
                }
                
                taskDuration++;
                
                // Play smithing animation
                if (taskDuration % 60 == 0) {
                    playSmithingAnimation(details);
                }
                
                // Report progress
                if (taskDuration % 300 == 0) {
                    sendTaskUpdate(details, "I'm working on repairing equipment. Bring me damaged items!");
                }
                
                // Stop task after 3 minutes
                if (taskDuration >= 3600) {
                    sendTaskUpdate(details, "I'm ready to repair your items! Bring them to me.");
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 20L, 1L);
    }
    
    /**
     * Starts a smithing task
     */
    private void startSmithingTask(NPCManager.NPCDetails details, Player player) {
        new BukkitRunnable() {
            private int taskDuration = 0;
            
            @Override
            public void run() {
                if (!isNPCActive(details)) {
                    cancel();
                    return;
                }
                
                taskDuration++;
                
                // Play smithing animation
                if (taskDuration % 40 == 0) {
                    playSmithingAnimation(details);
                }
                
                // Report progress
                if (taskDuration % 200 == 0) {
                    sendTaskUpdate(details, "I'm working at the forge. What would you like me to craft?");
                }
                
                // Stop task after 3 minutes
                if (taskDuration >= 3600) {
                    sendTaskUpdate(details, "I'm ready to craft items for you! Tell me what you need.");
                    cancel();
                }
            }
        }.runTaskTimer(plugin, 20L, 1L);
    }
    
    /**
     * Finds nearby trees for woodcutting
     */
    private Location findNearbyTree(NPCManager.NPCDetails details) {
        NPC npc = details.getCitizensNPC();
        if (npc == null || !npc.isSpawned()) {
            return null;
        }
        
        Location npcLocation = npc.getEntity().getLocation();
        
        // Search in a 15 block radius
        for (int x = -15; x <= 15; x++) {
            for (int z = -15; z <= 15; z++) {
                for (int y = -5; y <= 5; y++) {
                    Location checkLocation = npcLocation.clone().add(x, y, z);
                    Block block = checkLocation.getBlock();
                    
                    if (isLogBlock(block)) {
                        // Check if there's a tree structure
                        if (hasTreeStructure(checkLocation)) {
                            return checkLocation;
                        }
                    }
                }
            }
        }
        
        return null;
    }
    
    /**
     * Checks if a block is a log
     */
    private boolean isLogBlock(Block block) {
        String materialName = block.getType().name().toLowerCase();
        return materialName.contains("log") || materialName.contains("wood");
    }
    
    /**
     * Checks if there's a proper tree structure
     */
    private boolean hasTreeStructure(Location baseLocation) {
        // Simple check - look for leaves above the log
        for (int y = 1; y <= 10; y++) {
            Block block = baseLocation.clone().add(0, y, 0).getBlock();
            if (block.getType().name().toLowerCase().contains("leaves")) {
                return true;
            }
        }
        return false;
    }
    
    /**
     * Chops a tree at the given location
     */
    private void chopTree(NPCManager.NPCDetails details, Location treeLocation) {
        NPC npc = details.getCitizensNPC();
        if (npc != null && npc.isSpawned()) {
            // Make NPC look at the tree
            npc.getEntity().lookAt(treeLocation);
            
            // Move towards the tree
            npc.getNavigator().setTarget(treeLocation);
            
            // Play chopping animation
            playChoppingAnimation(details);
        }
    }
    
    /**
     * Finds suitable spots for planting saplings
     */
    private Location findPlantingSpot(NPCManager.NPCDetails details) {
        NPC npc = details.getCitizensNPC();
        if (npc == null || !npc.isSpawned()) {
            return null;
        }
        
        Location npcLocation = npc.getEntity().getLocation();
        
        // Look for grass blocks with air above them
        for (int x = -10; x <= 10; x++) {
            for (int z = -10; z <= 10; z++) {
                Location checkLocation = npcLocation.clone().add(x, 0, z);
                Block groundBlock = checkLocation.getBlock();
                Block airBlock = checkLocation.clone().add(0, 1, 0).getBlock();
                
                if (groundBlock.getType() == Material.GRASS_BLOCK && airBlock.getType() == Material.AIR) {
                    return checkLocation.clone().add(0, 1, 0);
                }
            }
        }
        
        return null;
    }
    
    /**
     * Plants a sapling at the given location
     */
    private void plantSapling(NPCManager.NPCDetails details, Location plantLocation) {
        NPC npc = details.getCitizensNPC();
        if (npc != null && npc.isSpawned()) {
            // Make NPC look at the planting spot
            npc.getEntity().lookAt(plantLocation);
            
            // Move towards the spot
            npc.getNavigator().setTarget(plantLocation);
            
            // Play planting animation
            playPlantingAnimation(details);
        }
    }
    
    /**
     * Plays chopping animation
     */
    private void playChoppingAnimation(NPCManager.NPCDetails details) {
        ModelEngineIntegration modelEngineIntegration = plugin.getModelEngineIntegration();
        if (modelEngineIntegration != null && details.getModelName() != null) {
            Entity targetEntity = details.getMythicMobEntity();
            if (targetEntity == null && details.getCitizensNPC() != null) {
                targetEntity = (org.bukkit.entity.Entity) details.getCitizensNPC().getEntity();
            }
            
            if (targetEntity != null) {
                modelEngineIntegration.playAnimation(targetEntity, details.getModelName(), "woodcutting");
                
                // Stop animation after 2 seconds
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        modelEngineIntegration.stopAnimation(targetEntity, details.getModelName(), "woodcutting");
                    }
                }.runTaskLater(plugin, 40L);
            }
        }
    }
    
    /**
     * Plays planting animation
     */
    private void playPlantingAnimation(NPCManager.NPCDetails details) {
        ModelEngineIntegration modelEngineIntegration = plugin.getModelEngineIntegration();
        if (modelEngineIntegration != null && details.getModelName() != null) {
            Entity targetEntity = details.getMythicMobEntity();
            if (targetEntity == null && details.getCitizensNPC() != null) {
                targetEntity = (org.bukkit.entity.Entity) details.getCitizensNPC().getEntity();
            }
            
            if (targetEntity != null) {
                modelEngineIntegration.playAnimation(targetEntity, details.getModelName(), "planting");
                
                // Stop animation after 3 seconds
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        modelEngineIntegration.stopAnimation(targetEntity, details.getModelName(), "planting");
                    }
                }.runTaskLater(plugin, 60L);
            }
        }
    }
    
    /**
     * Plays walking animation
     */
    private void playWalkingAnimation(NPCManager.NPCDetails details) {
        ModelEngineIntegration modelEngineIntegration = plugin.getModelEngineIntegration();
        if (modelEngineIntegration != null && details.getModelName() != null) {
            Entity targetEntity = details.getMythicMobEntity();
            if (targetEntity == null && details.getCitizensNPC() != null) {
                targetEntity = (org.bukkit.entity.Entity) details.getCitizensNPC().getEntity();
            }
            
            if (targetEntity != null) {
                modelEngineIntegration.playAnimation(targetEntity, details.getModelName(), "walking");
            }
        }
    }
    
    /**
     * Plays smithing animation
     */
    private void playSmithingAnimation(NPCManager.NPCDetails details) {
        ModelEngineIntegration modelEngineIntegration = plugin.getModelEngineIntegration();
        if (modelEngineIntegration != null && details.getModelName() != null) {
            Entity targetEntity = details.getMythicMobEntity();
            if (targetEntity == null && details.getCitizensNPC() != null) {
                targetEntity = (org.bukkit.entity.Entity) details.getCitizensNPC().getEntity();
            }
            
            if (targetEntity != null) {
                modelEngineIntegration.playAnimation(targetEntity, details.getModelName(), "smithing");
                
                // Stop animation after 4 seconds
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        modelEngineIntegration.stopAnimation(targetEntity, details.getModelName(), "smithing");
                    }
                }.runTaskLater(plugin, 80L);
            }
        }
    }
    
    /**
     * Sends task update to nearby players
     */
    private void sendTaskUpdate(NPCManager.NPCDetails details, String message) {
        NPC npc = details.getCitizensNPC();
        if (npc != null && npc.isSpawned()) {
            Location npcLocation = npc.getEntity().getLocation();
            
            // Send to all players within 10 blocks
            for (Player player : plugin.getServer().getOnlinePlayers()) {
                if (player.getLocation().distance(npcLocation) <= 10) {
                    dev.archdemone.npcintegration.utils.MessageUtil.sendInfo(player, 
                        "&6[" + details.getName() + "] &e" + message);
                }
            }
        }
    }
    
    /**
     * Checks if NPC is still active
     */
    private boolean isNPCActive(NPCManager.NPCDetails details) {
        NPC npc = details.getCitizensNPC();
        return npc != null && npc.isSpawned() && npcManager.getNPCDetails(details.getCitizensId()) != null;
    }
    
    /**
     * Checks if player is too far from NPC
     */
    private boolean isPlayerTooFar(NPCManager.NPCDetails details, Player player) {
        NPC npc = details.getCitizensNPC();
        if (npc == null || !npc.isSpawned()) {
            return true;
        }
        
        return player.getLocation().distance(npc.getEntity().getLocation()) > 50;
    }
    
    /**
     * Inner class for NPC tasks
     */
    public static class NPCTask {
        private final String taskType;
        private final String assignedBy;
        private final long startTime;
        
        public NPCTask(String taskType, String assignedBy) {
            this.taskType = taskType;
            this.assignedBy = assignedBy;
            this.startTime = System.currentTimeMillis();
        }
        
        public String getTaskType() { return taskType; }
        public String getAssignedBy() { return assignedBy; }
        public long getStartTime() { return startTime; }
    }
}