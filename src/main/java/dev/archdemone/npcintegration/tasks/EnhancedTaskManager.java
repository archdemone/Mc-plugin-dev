package dev.archdemone.npcintegration.tasks;

import dev.archdemone.npcintegration.NPCIntegrationPlugin;
import dev.archdemone.npcintegration.managers.NPCManager;
import dev.archdemone.npcintegration.integrations.CitizensIntegration;
import dev.archdemone.npcintegration.integrations.MythicMobsIntegration;
import dev.archdemone.npcintegration.integrations.ModelEngineIntegration;
import dev.archdemone.npcintegration.utils.MessageUtil;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Enhanced task manager with support for multiple tasks, tool requirements, and advanced features
 */
public class EnhancedTaskManager {
    
    private final NPCIntegrationPlugin plugin;
    private final NPCManager npcManager;
    private final Map<Integer, TaskQueue> npcTaskQueues;
    private final Map<Integer, BukkitRunnable> activeTaskRunnables;
    private final Map<Integer, NPCToolInventory> npcInventories;
    private final TaskPersistence persistence;
    
    public EnhancedTaskManager(NPCIntegrationPlugin plugin) {
        this.plugin = plugin;
        this.npcManager = plugin.getNPCManager();
        this.npcTaskQueues = new ConcurrentHashMap<>();
        this.activeTaskRunnables = new ConcurrentHashMap<>();
        this.npcInventories = new ConcurrentHashMap<>();
        this.persistence = new TaskPersistence(plugin);
        
        // Load saved data
        loadSavedData();
    }
    
    /**
     * Assigns a new task to an NPC
     */
    public boolean assignTask(NPCManager.NPCDetails details, String taskType, Player player, TaskConfiguration config) {
        NPC npc = details.getCitizensNPC();
        if (npc == null || !npc.isSpawned()) {
            MessageUtil.sendError(player, "NPC is not available for tasks!");
            return false;
        }
        
        int npcId = details.getCitizensId();
        TaskQueue queue = npcTaskQueues.computeIfAbsent(npcId, k -> new TaskQueue());
        
        // Create the task
        NPCTask task = createTask(taskType, player, config);
        if (task == null) {
            MessageUtil.sendError(player, "Unknown task type: " + taskType);
            return false;
        }
        
        // Check tool requirements
        if (!checkToolRequirements(details, task, player)) {
            return false;
        }
        
        // Add task to queue
        queue.addTask(task);
        
        // If no active task, start the queue
        if (!queue.hasActiveTask()) {
            startNextTask(details, queue);
        } else {
            MessageUtil.sendInfo(player, "&6[" + details.getName() + "] &eI've added that to my task list. I'll get to it after my current work!");
        }
        
        return true;
    }
    
    /**
     * Interrupts current task and switches to a new one
     */
    public boolean switchTask(NPCManager.NPCDetails details, String newTaskType, Player player, TaskConfiguration config) {
        int npcId = details.getCitizensId();
        TaskQueue queue = npcTaskQueues.get(npcId);
        
        if (queue == null || !queue.hasActiveTask()) {
            return assignTask(details, newTaskType, player, config);
        }
        
        NPCTask currentTask = queue.getCurrentTask();
        if (currentTask != null && !currentTask.getConfig().canInterrupt()) {
            MessageUtil.sendError(player, "&6[" + details.getName() + "] &cI can't stop my current task right now. It's too important!");
            return false;
        }
        
        // Stop current task
        stopCurrentTask(details);
        
        // Create new task
        NPCTask newTask = createTask(newTaskType, player, config);
        if (newTask == null) {
            MessageUtil.sendError(player, "Unknown task type: " + newTaskType);
            return false;
        }
        
        // Check tool requirements
        if (!checkToolRequirements(details, newTask, player)) {
            return false;
        }
        
        // Clear queue and add new task
        queue.clear();
        queue.addTask(newTask);
        
        // Start new task
        startNextTask(details, queue);
        
        MessageUtil.sendInfo(player, "&6[" + details.getName() + "] &eI've switched to the new task right away!");
        
        return true;
    }
    
    /**
     * Checks if NPC has required tools for the task
     */
    private boolean checkToolRequirements(NPCManager.NPCDetails details, NPCTask task, Player player) {
        TaskConfiguration config = task.getConfig();
        Material requiredTool = config.getRequiredTool();
        
        if (requiredTool == null) {
            return true; // No tool required
        }
        
        NPCToolInventory inventory = npcInventories.computeIfAbsent(details.getCitizensId(), k -> new NPCToolInventory());
        
        // Check if NPC has the tool
        if (inventory.hasTool(requiredTool)) {
            return true;
        }
        
        // Check if player has the tool and can give it
        if (player.getInventory().contains(requiredTool)) {
            // Ask player for the tool
            MessageUtil.sendInfo(player, "&6[" + details.getName() + "] &eI need a " + 
                requiredTool.name().toLowerCase().replace("_", " ") + " for this task. Can you give me one?");
            
            // Wait for player response (this would need to be handled by chat system)
            return false;
        }
        
        // Check for preferred tool materials
        Material[] preferredTools = config.getPreferredToolMaterials();
        for (Material tool : preferredTools) {
            if (player.getInventory().contains(tool)) {
                MessageUtil.sendInfo(player, "&6[" + details.getName() + "] &eI could use a " + 
                    tool.name().toLowerCase().replace("_", " ") + " for this task. Can you give me one?");
                return false;
            }
        }
        
        MessageUtil.sendError(player, "&6[" + details.getName() + "] &cI need a " + 
            requiredTool.name().toLowerCase().replace("_", " ") + " for this task!");
        return false;
    }
    
    /**
     * Gives a tool to an NPC
     */
    public boolean giveToolToNPC(NPCManager.NPCDetails details, Player player, Material toolType) {
        ItemStack tool = player.getInventory().getItem(player.getInventory().first(toolType));
        if (tool == null) {
            MessageUtil.sendError(player, "You don't have that tool!");
            return false;
        }
        
        // Remove tool from player
        player.getInventory().removeItem(tool);
        
        // Give tool to NPC
        NPCToolInventory inventory = npcInventories.computeIfAbsent(details.getCitizensId(), k -> new NPCToolInventory());
        inventory.addTool(toolType, tool);
        
        MessageUtil.sendInfo(player, "&6[" + details.getName() + "] &eThank you for the " + 
            toolType.name().toLowerCase().replace("_", " ") + "! I'll put it to good use.");
        
        // If NPC was waiting for a tool, restart task
        TaskQueue queue = npcTaskQueues.get(details.getCitizensId());
        if (queue != null && !queue.hasActiveTask() && !queue.isEmpty()) {
            startNextTask(details, queue);
        }
        
        return true;
    }
    
    /**
     * Creates a task based on type
     */
    private NPCTask createTask(String taskType, Player assignedBy, TaskConfiguration config) {
        switch (taskType.toLowerCase()) {
            case "woodcutting":
                return new WoodcuttingTask(assignedBy, config);
            case "mining":
                return new MiningTask(assignedBy, config);
            case "farming":
                return new FarmingTask(assignedBy, config);
            case "planting":
                return new PlantingTask(assignedBy, config);
            case "following":
                return new FollowingTask(assignedBy, config);
            case "repairing":
                return new RepairingTask(assignedBy, config);
            case "smithing":
                return new SmithingTask(assignedBy, config);
            default:
                return null;
        }
    }
    
    /**
     * Starts the next task in the queue
     */
    public void startNextTask(NPCManager.NPCDetails details, TaskQueue queue) {
        if (queue.isEmpty()) {
            return;
        }
        
        NPCTask task = queue.getNextTask();
        if (task == null) {
            return;
        }
        
        // Stop any existing task
        stopCurrentTask(details);
        
        // Start new task
        BukkitRunnable runnable = createTaskRunnable(details, task);
        if (runnable != null) {
            runnable.runTaskTimer(plugin, 20L, 1L);
            activeTaskRunnables.put(details.getCitizensId(), runnable);
            queue.setCurrentTask(task);
            
            MessageUtil.sendInfo(null, "&6[" + details.getName() + "] &eStarted " + task.getType() + " task!");
        }
    }
    
    /**
     * Stops the current task
     */
    private void stopCurrentTask(NPCManager.NPCDetails details) {
        int npcId = details.getCitizensId();
        BukkitRunnable runnable = activeTaskRunnables.remove(npcId);
        if (runnable != null) {
            runnable.cancel();
        }
        
        TaskQueue queue = npcTaskQueues.get(npcId);
        if (queue != null) {
            queue.setCurrentTask(null);
        }
    }
    
    /**
     * Creates a task runnable based on task type
     */
    private BukkitRunnable createTaskRunnable(NPCManager.NPCDetails details, NPCTask task) {
        switch (task.getType()) {
            case "woodcutting":
                return new WoodcuttingTaskRunnable(details, (WoodcuttingTask) task);
            case "mining":
                return new MiningTaskRunnable(details, (MiningTask) task);
            case "farming":
                return new FarmingTaskRunnable(details, (FarmingTask) task);
            case "planting":
                return new PlantingTaskRunnable(details, (PlantingTask) task);
            case "following":
                return new FollowingTaskRunnable(details, (FollowingTask) task);
            case "repairing":
                return new RepairingTaskRunnable(details, (RepairingTask) task);
            case "smithing":
                return new SmithingTaskRunnable(details, (SmithingTask) task);
            default:
                return null;
        }
    }
    
    /**
     * Gets the current task for an NPC
     */
    public NPCTask getCurrentTask(int npcId) {
        TaskQueue queue = npcTaskQueues.get(npcId);
        return queue != null ? queue.getCurrentTask() : null;
    }
    
    /**
     * Gets the task queue for an NPC
     */
    public TaskQueue getTaskQueue(int npcId) {
        return npcTaskQueues.get(npcId);
    }
    
    /**
     * Clears all tasks for an NPC
     */
    public void clearAllTasks(NPCManager.NPCDetails details) {
        stopCurrentTask(details);
        int npcId = details.getCitizensId();
        TaskQueue queue = npcTaskQueues.get(npcId);
        if (queue != null) {
            queue.clear();
        }
    }
    
    /**
     * Inner class for managing NPC tool inventory
     */
    public static class NPCToolInventory {
        private final Map<Material, ItemStack> tools;
        
        public NPCToolInventory() {
            this.tools = new HashMap<>();
        }
        
        public boolean hasTool(Material toolType) {
            return tools.containsKey(toolType);
        }
        
        public void addTool(Material toolType, ItemStack tool) {
            tools.put(toolType, tool);
        }
        
        public ItemStack getTool(Material toolType) {
            return tools.get(toolType);
        }
        
        public void removeTool(Material toolType) {
            tools.remove(toolType);
        }
        
        public Map<Material, ItemStack> getAllTools() {
            return new HashMap<>(tools);
        }
    }
    
    /**
     * Loads saved data on startup
     */
    private void loadSavedData() {
        for (int npcId : persistence.getNPCsWithData()) {
            // Load task queue
            TaskQueue queue = persistence.loadTaskQueue(npcId);
            if (!queue.isEmpty()) {
                npcTaskQueues.put(npcId, queue);
            }
            
            // Load tool inventory
            NPCToolInventory inventory = persistence.loadToolInventory(npcId);
            if (!inventory.getAllTools().isEmpty()) {
                npcInventories.put(npcId, inventory);
            }
        }
        
        plugin.getLogger().info("Loaded saved data for " + persistence.getNPCsWithData().size() + " NPCs");
    }
    
    /**
     * Saves all data
     */
    public void saveAllData() {
        for (Map.Entry<Integer, TaskQueue> entry : npcTaskQueues.entrySet()) {
            persistence.saveTaskQueue(entry.getKey(), entry.getValue());
        }
        
        for (Map.Entry<Integer, NPCToolInventory> entry : npcInventories.entrySet()) {
            persistence.saveToolInventory(entry.getKey(), entry.getValue());
        }
        
        plugin.getLogger().info("Saved data for " + npcTaskQueues.size() + " NPC task queues and " + npcInventories.size() + " tool inventories");
    }
    
    /**
     * Saves data for a specific NPC
     */
    public void saveNPCData(int npcId) {
        TaskQueue queue = npcTaskQueues.get(npcId);
        if (queue != null) {
            persistence.saveTaskQueue(npcId, queue);
        }
        
        NPCToolInventory inventory = npcInventories.get(npcId);
        if (inventory != null) {
            persistence.saveToolInventory(npcId, inventory);
        }
    }
    
    /**
     * Clears all data for an NPC
     */
    public void clearNPCData(int npcId) {
        // Stop any active tasks
        BukkitRunnable runnable = activeTaskRunnables.remove(npcId);
        if (runnable != null) {
            runnable.cancel();
        }
        
        // Clear from memory
        npcTaskQueues.remove(npcId);
        npcInventories.remove(npcId);
        
        // Clear from persistence
        persistence.clearNPCData(npcId);
    }
    
    /**
     * Auto-saves data periodically
     */
    public void startAutoSave() {
        new BukkitRunnable() {
            @Override
            public void run() {
                saveAllData();
            }
        }.runTaskTimer(plugin, 20L * 60L, 20L * 60L); // Save every minute
    }
}