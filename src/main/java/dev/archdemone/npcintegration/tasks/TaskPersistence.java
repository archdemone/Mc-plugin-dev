package dev.archdemone.npcintegration.tasks;

import dev.archdemone.npcintegration.NPCIntegrationPlugin;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

/**
 * Handles persistence of NPC tasks and tool inventories
 */
public class TaskPersistence {
    
    private final NPCIntegrationPlugin plugin;
    private final File tasksFile;
    private final File toolsFile;
    private FileConfiguration tasksConfig;
    private FileConfiguration toolsConfig;
    
    public TaskPersistence(NPCIntegrationPlugin plugin) {
        this.plugin = plugin;
        this.tasksFile = new File(plugin.getDataFolder(), "tasks.yml");
        this.toolsFile = new File(plugin.getDataFolder(), "tools.yml");
        
        // Create data folder if it doesn't exist
        if (!plugin.getDataFolder().exists()) {
            plugin.getDataFolder().mkdirs();
        }
        
        loadConfigs();
    }
    
    /**
     * Loads configuration files
     */
    private void loadConfigs() {
        // Load tasks config
        if (!tasksFile.exists()) {
            plugin.saveResource("tasks.yml", false);
        }
        tasksConfig = YamlConfiguration.loadConfiguration(tasksFile);
        
        // Load tools config
        if (!toolsFile.exists()) {
            plugin.saveResource("tools.yml", false);
        }
        toolsConfig = YamlConfiguration.loadConfiguration(toolsFile);
    }
    
    /**
     * Saves task queue for an NPC
     */
    public void saveTaskQueue(int npcId, TaskQueue queue) {
        String npcPath = "npcs." + npcId;
        
        // Clear existing tasks for this NPC
        tasksConfig.set(npcPath + ".tasks", null);
        
        // Save current task
        if (queue.getCurrentTask() != null) {
            saveTask(npcPath + ".current", queue.getCurrentTask());
        }
        
        // Save queued tasks
        int index = 0;
        for (NPCTask task : queue.getAllTasks()) {
            if (task != queue.getCurrentTask()) {
                saveTask(npcPath + ".queued." + index, task);
                index++;
            }
        }
        
        // Save queue metadata
        tasksConfig.set(npcPath + ".queueSize", queue.size());
        tasksConfig.set(npcPath + ".hasActiveTask", queue.hasActiveTask());
        
        saveTasksConfig();
    }
    
    /**
     * Loads task queue for an NPC
     */
    public TaskQueue loadTaskQueue(int npcId) {
        String npcPath = "npcs." + npcId;
        TaskQueue queue = new TaskQueue();
        
        if (!tasksConfig.contains(npcPath)) {
            return queue;
        }
        
        // Load current task
        if (tasksConfig.contains(npcPath + ".current")) {
            NPCTask currentTask = loadTask(npcPath + ".current");
            if (currentTask != null) {
                queue.setCurrentTask(currentTask);
            }
        }
        
        // Load queued tasks
        if (tasksConfig.contains(npcPath + ".queued")) {
            ConfigurationSection queuedSection = tasksConfig.getConfigurationSection(npcPath + ".queued");
            if (queuedSection != null) {
                for (String key : queuedSection.getKeys(false)) {
                    NPCTask task = loadTask(npcPath + ".queued." + key);
                    if (task != null) {
                        queue.addTask(task);
                    }
                }
            }
        }
        
        return queue;
    }
    
    /**
     * Saves a single task
     */
    private void saveTask(String path, NPCTask task) {
        tasksConfig.set(path + ".type", task.getType());
        tasksConfig.set(path + ".assignedBy", task.getAssignedBy().getUniqueId().toString());
        tasksConfig.set(path + ".startTime", task.getStartTime());
        tasksConfig.set(path + ".progress", task.getProgress());
        tasksConfig.set(path + ".repetitionsCompleted", task.getRepetitionsCompleted());
        tasksConfig.set(path + ".isCompleted", task.isCompleted());
        tasksConfig.set(path + ".isPaused", task.isPaused());
        
        // Save task-specific data
        if (task instanceof WoodcuttingTask) {
            WoodcuttingTask woodTask = (WoodcuttingTask) task;
            tasksConfig.set(path + ".data.treesChopped", woodTask.getTreesChopped());
            tasksConfig.set(path + ".data.logsCollected", woodTask.getLogsCollected());
        } else if (task instanceof MiningTask) {
            MiningTask miningTask = (MiningTask) task;
            tasksConfig.set(path + ".data.oresMined", miningTask.getOresMined());
            tasksConfig.set(path + ".data.blocksBroken", miningTask.getBlocksBroken());
        } else if (task instanceof FarmingTask) {
            FarmingTask farmingTask = (FarmingTask) task;
            tasksConfig.set(path + ".data.cropsHarvested", farmingTask.getCropsHarvested());
            tasksConfig.set(path + ".data.cropsPlanted", farmingTask.getCropsPlanted());
        }
    }
    
    /**
     * Loads a single task
     */
    private NPCTask loadTask(String path) {
        if (!tasksConfig.contains(path)) {
            return null;
        }
        
        String taskType = tasksConfig.getString(path + ".type");
        String assignedByStr = tasksConfig.getString(path + ".assignedBy");
        long startTime = tasksConfig.getLong(path + ".startTime");
        int progress = tasksConfig.getInt(path + ".progress");
        int repetitionsCompleted = tasksConfig.getInt(path + ".repetitionsCompleted");
        boolean isCompleted = tasksConfig.getBoolean(path + ".isCompleted");
        boolean isPaused = tasksConfig.getBoolean(path + ".isPaused");
        
        // Find the assigned player
        org.bukkit.entity.Player assignedBy = null;
        if (assignedByStr != null) {
            try {
                UUID playerUuid = UUID.fromString(assignedByStr);
                assignedBy = plugin.getServer().getPlayer(playerUuid);
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid player UUID in task data: " + assignedByStr);
            }
        }
        
        if (assignedBy == null) {
            plugin.getLogger().warning("Could not find assigned player for task, skipping");
            return null;
        }
        
        // Create task based on type
        TaskConfiguration config = createDefaultConfig(taskType);
        NPCTask task = createTask(taskType, assignedBy, config);
        
        if (task != null) {
            // Restore task state
            task.progress = progress;
            task.repetitionsCompleted = repetitionsCompleted;
            task.isCompleted = isCompleted;
            task.isPaused = isPaused;
            
            // Restore task-specific data
            if (task instanceof WoodcuttingTask && tasksConfig.contains(path + ".data")) {
                WoodcuttingTask woodTask = (WoodcuttingTask) task;
                woodTask.treesChopped = tasksConfig.getInt(path + ".data.treesChopped");
                woodTask.logsCollected = tasksConfig.getInt(path + ".data.logsCollected");
            } else if (task instanceof MiningTask && tasksConfig.contains(path + ".data")) {
                MiningTask miningTask = (MiningTask) task;
                miningTask.oresMined = tasksConfig.getInt(path + ".data.oresMined");
                miningTask.blocksBroken = tasksConfig.getInt(path + ".data.blocksBroken");
            }
        }
        
        return task;
    }
    
    /**
     * Creates a task based on type
     */
    private NPCTask createTask(String taskType, org.bukkit.entity.Player assignedBy, TaskConfiguration config) {
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
     * Creates default configuration for task type
     */
    private TaskConfiguration createDefaultConfig(String taskType) {
        switch (taskType.toLowerCase()) {
            case "woodcutting":
                return TaskConfiguration.createWoodcuttingConfig();
            case "mining":
                return TaskConfiguration.createMiningConfig();
            case "farming":
                return TaskConfiguration.createFarmingConfig();
            default:
                return new TaskConfiguration();
        }
    }
    
    /**
     * Saves NPC tool inventory
     */
    public void saveToolInventory(int npcId, EnhancedTaskManager.NPCToolInventory inventory) {
        String npcPath = "npcs." + npcId;
        
        // Clear existing tools
        toolsConfig.set(npcPath + ".tools", null);
        
        // Save tools
        int index = 0;
        for (Map.Entry<org.bukkit.Material, org.bukkit.inventory.ItemStack> entry : inventory.getAllTools().entrySet()) {
            String toolPath = npcPath + ".tools." + index;
            toolsConfig.set(toolPath + ".material", entry.getKey().name());
            toolsConfig.set(toolPath + ".amount", entry.getValue().getAmount());
            toolsConfig.set(toolPath + ".durability", entry.getValue().getDurability());
            index++;
        }
        
        saveToolsConfig();
    }
    
    /**
     * Loads NPC tool inventory
     */
    public EnhancedTaskManager.NPCToolInventory loadToolInventory(int npcId) {
        String npcPath = "npcs." + npcId;
        EnhancedTaskManager.NPCToolInventory inventory = new EnhancedTaskManager.NPCToolInventory();
        
        if (!toolsConfig.contains(npcPath + ".tools")) {
            return inventory;
        }
        
        ConfigurationSection toolsSection = toolsConfig.getConfigurationSection(npcPath + ".tools");
        if (toolsSection != null) {
            for (String key : toolsSection.getKeys(false)) {
                String materialStr = toolsConfig.getString(npcPath + ".tools." + key + ".material");
                int amount = toolsConfig.getInt(npcPath + ".tools." + key + ".amount", 1);
                short durability = (short) toolsConfig.getInt(npcPath + ".tools." + key + ".durability", 0);
                
                try {
                    org.bukkit.Material material = org.bukkit.Material.valueOf(materialStr);
                    org.bukkit.inventory.ItemStack tool = new org.bukkit.inventory.ItemStack(material, amount);
                    tool.setDurability(durability);
                    
                    inventory.addTool(material, tool);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid material in tool data: " + materialStr);
                }
            }
        }
        
        return inventory;
    }
    
    /**
     * Saves tasks configuration to file
     */
    private void saveTasksConfig() {
        try {
            tasksConfig.save(tasksFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save tasks configuration: " + e.getMessage());
        }
    }
    
    /**
     * Saves tools configuration to file
     */
    private void saveToolsConfig() {
        try {
            toolsConfig.save(toolsFile);
        } catch (IOException e) {
            plugin.getLogger().severe("Could not save tools configuration: " + e.getMessage());
        }
    }
    
    /**
     * Clears all data for an NPC
     */
    public void clearNPCData(int npcId) {
        tasksConfig.set("npcs." + npcId, null);
        toolsConfig.set("npcs." + npcId, null);
        
        saveTasksConfig();
        saveToolsConfig();
    }
    
    /**
     * Gets all NPCs with saved data
     */
    public java.util.Set<Integer> getNPCsWithData() {
        java.util.Set<Integer> npcIds = new java.util.HashSet<>();
        
        if (tasksConfig.contains("npcs")) {
            ConfigurationSection npcsSection = tasksConfig.getConfigurationSection("npcs");
            if (npcsSection != null) {
                for (String key : npcsSection.getKeys(false)) {
                    try {
                        npcIds.add(Integer.parseInt(key));
                    } catch (NumberFormatException e) {
                        plugin.getLogger().warning("Invalid NPC ID in tasks data: " + key);
                    }
                }
            }
        }
        
        return npcIds;
    }
}