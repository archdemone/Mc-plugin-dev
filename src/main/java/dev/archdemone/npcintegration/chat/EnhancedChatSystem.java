package dev.archdemone.npcintegration.chat;

import dev.archdemone.npcintegration.NPCIntegrationPlugin;
import dev.archdemone.npcintegration.managers.NPCManager;
import dev.archdemone.npcintegration.tasks.TaskConfiguration;
import dev.archdemone.npcintegration.tasks.EnhancedTaskManager;
import dev.archdemone.npcintegration.tasks.TaskQueue;
import dev.archdemone.npcintegration.utils.MessageUtil;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Enhanced chat system with robust keyword detection and natural language processing
 */
public class EnhancedChatSystem {
    
    private final NPCIntegrationPlugin plugin;
    private final NPCManager npcManager;
    private final EnhancedTaskManager taskManager;
    private final Map<UUID, NPCConversation> activeConversations;
    private final Map<Integer, NPCMemory> npcMemories;
    
    // Patterns for NPC mentions
    private final Pattern npcNamePattern = Pattern.compile("@(\\w+)", Pattern.CASE_INSENSITIVE);
    private final Pattern npcIdPattern = Pattern.compile("#(\\d+)", Pattern.CASE_INSENSITIVE);
    
    // Keyword maps for different task types
    private final Map<String, Set<String>> taskKeywords;
    private final Map<String, Set<String>> toolKeywords;
    private final Map<String, Set<String>> materialKeywords;
    private final Map<String, Set<String>> actionKeywords;
    
    public EnhancedChatSystem(NPCIntegrationPlugin plugin, EnhancedTaskManager taskManager) {
        this.plugin = plugin;
        this.npcManager = plugin.getNPCManager();
        this.taskManager = taskManager;
        this.activeConversations = new HashMap<>();
        this.npcMemories = new HashMap<>();
        
        // Initialize keyword maps
        this.taskKeywords = new HashMap<>();
        this.toolKeywords = new HashMap<>();
        this.materialKeywords = new HashMap<>();
        this.actionKeywords = new HashMap<>();
        
        setupKeywords();
    }
    
    /**
     * Sets up all keyword mappings for natural language processing
     */
    private void setupKeywords() {
        // Woodcutting keywords
        Set<String> woodcuttingKeywords = new HashSet<>(Arrays.asList(
            "wood", "chop", "cut", "tree", "log", "lumber", "timber", "axe", "woodcutting", "woodcutter",
            "forest", "branch", "trunk", "wooden", "oak", "birch", "spruce", "jungle", "acacia", "dark_oak",
            "mangrove", "cherry", "stump", "sapling", "plant tree", "grow tree"
        ));
        taskKeywords.put("woodcutting", woodcuttingKeywords);
        
        // Mining keywords
        Set<String> miningKeywords = new HashSet<>(Arrays.asList(
            "mine", "mining", "ore", "pickaxe", "stone", "rock", "cave", "underground", "coal", "iron",
            "gold", "diamond", "emerald", "lapis", "redstone", "netherite", "mineral", "gem", "crystal",
            "dig", "excavate", "quarry", "tunnel", "undermine"
        ));
        taskKeywords.put("mining", miningKeywords);
        
        // Farming keywords
        Set<String> farmingKeywords = new HashSet<>(Arrays.asList(
            "farm", "farming", "crop", "plant", "harvest", "seed", "wheat", "carrot", "potato", "beetroot",
            "hoe", "agriculture", "grow", "cultivate", "till", "soil", "field", "garden", "vegetable", "food"
        ));
        taskKeywords.put("farming", farmingKeywords);
        
        // Following keywords
        Set<String> followingKeywords = new HashSet<>(Arrays.asList(
            "follow", "come", "with", "accompany", "escort", "guide", "lead", "walk", "move", "go", "travel"
        ));
        taskKeywords.put("following", followingKeywords);
        
        // Repairing keywords
        Set<String> repairingKeywords = new HashSet<>(Arrays.asList(
            "repair", "fix", "mend", "restore", "broken", "damaged", "worn", "tool", "equipment", "gear",
            "anvil", "craft", "smith", "forge", "hammer"
        ));
        taskKeywords.put("repairing", repairingKeywords);
        
        // Smithing keywords
        Set<String> smithingKeywords = new HashSet<>(Arrays.asList(
            "smith", "smithing", "forge", "anvil", "hammer", "metal", "iron", "steel", "craft", "make",
            "create", "build", "construct", "weapon", "armor", "tool", "blade", "sword", "axe", "pickaxe"
        ));
        taskKeywords.put("smithing", smithingKeywords);
        
        // Planting keywords
        Set<String> plantingKeywords = new HashSet<>(Arrays.asList(
            "plant", "planting", "sapling", "seed", "grow", "cultivate", "sow", "garden", "tree", "flower",
            "crop", "vegetable", "herb", "bush", "vine"
        ));
        taskKeywords.put("planting", plantingKeywords);
        
        // Tool keywords
        toolKeywords.put("axe", new HashSet<>(Arrays.asList("axe", "hatchet", "chopper", "cutter")));
        toolKeywords.put("pickaxe", new HashSet<>(Arrays.asList("pickaxe", "pick", "mining tool")));
        toolKeywords.put("hoe", new HashSet<>(Arrays.asList("hoe", "farming tool", "cultivator")));
        toolKeywords.put("shovel", new HashSet<>(Arrays.asList("shovel", "spade", "digging tool")));
        toolKeywords.put("sword", new HashSet<>(Arrays.asList("sword", "blade", "weapon")));
        
        // Material keywords
        materialKeywords.put("wood", new HashSet<>(Arrays.asList("wood", "wooden", "oak", "birch", "spruce")));
        materialKeywords.put("stone", new HashSet<>(Arrays.asList("stone", "rock", "cobblestone")));
        materialKeywords.put("iron", new HashSet<>(Arrays.asList("iron", "metal", "steel")));
        materialKeywords.put("diamond", new HashSet<>(Arrays.asList("diamond", "gem", "precious")));
        materialKeywords.put("gold", new HashSet<>(Arrays.asList("gold", "golden", "precious metal")));
        
        // Action keywords
        actionKeywords.put("give", new HashSet<>(Arrays.asList("give", "hand", "provide", "lend", "offer")));
        actionKeywords.put("stop", new HashSet<>(Arrays.asList("stop", "halt", "cease", "end", "quit", "cancel")));
        actionKeywords.put("help", new HashSet<>(Arrays.asList("help", "assist", "aid", "support")));
        actionKeywords.put("status", new HashSet<>(Arrays.asList("status", "progress", "how", "doing", "work")));
    }
    
    /**
     * Processes a chat message with enhanced keyword detection
     */
    public boolean processChatMessage(Player player, String message) {
        String lowerMessage = message.toLowerCase();
        
        // Check for NPC mentions
        var nameMatcher = npcNamePattern.matcher(message);
        if (nameMatcher.find()) {
            String npcName = nameMatcher.group(1);
            return handleNPCMention(player, npcName, message);
        }
        
        var idMatcher = npcIdPattern.matcher(message);
        if (idMatcher.find()) {
            String npcIdStr = idMatcher.group(1);
            try {
                int npcId = Integer.parseInt(npcIdStr);
                return handleNPCInteraction(player, npcId, message);
            } catch (NumberFormatException e) {
                return false;
            }
        }
        
        // Check for nearby NPCs
        return handleNearbyNPCInteraction(player, message);
    }
    
    /**
     * Handles NPC mention by name
     */
    private boolean handleNPCMention(Player player, String npcName, String message) {
        for (NPCManager.NPCDetails details : npcManager.getAllNPCDetails()) {
            if (details.getName().toLowerCase().contains(npcName.toLowerCase())) {
                return handleNPCInteraction(player, details.getCitizensId(), message);
            }
        }
        
        MessageUtil.sendError(player, "I don't know anyone named '" + npcName + "'");
        return true;
    }
    
    /**
     * Handles interaction with a specific NPC
     */
    private boolean handleNPCInteraction(Player player, int npcId, String message) {
        NPCManager.NPCDetails details = npcManager.getNPCDetails(npcId);
        if (details == null) {
            MessageUtil.sendError(player, "That NPC doesn't exist!");
            return true;
        }
        
        // Make NPC look at player
        if (plugin.getCitizensIntegration() != null && details.getCitizensNPC() != null) {
            plugin.getCitizensIntegration().makeNPCLookAt(details.getCitizensNPC(), player);
        }
        
        // Process the message
        String response = processNPCMessage(details, player, message);
        
        // Send response
        MessageUtil.sendInfo(player, "&6[" + details.getName() + "] &e" + response);
        
        // Update conversation
        UUID playerId = player.getUniqueId();
        NPCConversation conversation = activeConversations.getOrDefault(playerId, new NPCConversation());
        conversation.setNPCId(npcId);
        conversation.setLastMessage(System.currentTimeMillis());
        activeConversations.put(playerId, conversation);
        
        return true;
    }
    
    /**
     * Handles interaction with nearby NPCs
     */
    private boolean handleNearbyNPCInteraction(Player player, String message) {
        List<NPCManager.NPCDetails> nearbyNPCs = new ArrayList<>();
        
        for (NPCManager.NPCDetails details : npcManager.getAllNPCDetails()) {
            if (details.getCitizensNPC() != null && details.getCitizensNPC().isSpawned()) {
                double distance = player.getLocation().distance(details.getCitizensNPC().getEntity().getLocation());
                if (distance <= 5.0) {
                    nearbyNPCs.add(details);
                }
            }
        }
        
        if (nearbyNPCs.isEmpty()) {
            return false;
        }
        
        if (nearbyNPCs.size() > 1) {
            MessageUtil.sendInfo(player, "&7I can see " + nearbyNPCs.size() + " NPCs nearby. Use @Name or #ID to specify which one.");
            return false;
        }
        
        return handleNPCInteraction(player, nearbyNPCs.get(0).getCitizensId(), message);
    }
    
    /**
     * Processes NPC message with enhanced keyword detection
     */
    private String processNPCMessage(NPCManager.NPCDetails details, Player player, String message) {
        String lowerMessage = message.toLowerCase();
        
        // Check for greetings
        if (isGreeting(lowerMessage)) {
            return generateGreeting(details, player);
        }
        
        // Check for task assignments using keyword detection
        String detectedTask = detectTaskType(lowerMessage);
        if (detectedTask != null) {
            return handleTaskAssignment(details, player, message, detectedTask);
        }
        
        // Check for tool giving
        if (detectToolGiving(lowerMessage)) {
            return handleToolGiving(details, player, message);
        }
        
        // Check for task status requests
        if (detectStatusRequest(lowerMessage)) {
            return handleStatusRequest(details, player);
        }
        
        // Check for task stopping
        if (detectTaskStopping(lowerMessage)) {
            return handleTaskStopping(details, player);
        }
        
        // Check for help requests
        if (isHelpRequest(lowerMessage)) {
            return generateHelpResponse(details);
        }
        
        // Check for farewells
        if (isFarewell(lowerMessage)) {
            activeConversations.remove(player.getUniqueId());
            return generateFarewell(details, player);
        }
        
        // Default response
        return generateDefaultResponse(details, player);
    }
    
    /**
     * Detects task type from message using keyword matching
     */
    private String detectTaskType(String message) {
        Map<String, Integer> taskScores = new HashMap<>();
        
        for (Map.Entry<String, Set<String>> entry : taskKeywords.entrySet()) {
            String taskType = entry.getKey();
            Set<String> keywords = entry.getValue();
            
            int score = 0;
            for (String keyword : keywords) {
                if (message.contains(keyword)) {
                    score++;
                }
            }
            
            if (score > 0) {
                taskScores.put(taskType, score);
            }
        }
        
        // Return task with highest score
        return taskScores.entrySet().stream()
            .max(Map.Entry.comparingByValue())
            .map(Map.Entry::getKey)
            .orElse(null);
    }
    
    /**
     * Detects if player is trying to give a tool
     */
    private boolean detectToolGiving(String message) {
        Set<String> giveKeywords = actionKeywords.get("give");
        if (giveKeywords == null) return false;
        
        boolean hasGiveKeyword = giveKeywords.stream().anyMatch(message::contains);
        boolean hasToolKeyword = toolKeywords.values().stream()
            .flatMap(Set::stream)
            .anyMatch(message::contains);
        
        return hasGiveKeyword && hasToolKeyword;
    }
    
    /**
     * Detects if player is requesting status
     */
    private boolean detectStatusRequest(String message) {
        Set<String> statusKeywords = actionKeywords.get("status");
        return statusKeywords != null && statusKeywords.stream().anyMatch(message::contains);
    }
    
    /**
     * Detects if player wants to stop tasks
     */
    private boolean detectTaskStopping(String message) {
        Set<String> stopKeywords = actionKeywords.get("stop");
        return stopKeywords != null && stopKeywords.stream().anyMatch(message::contains);
    }
    
    /**
     * Handles task assignment with enhanced detection
     */
    private String handleTaskAssignment(NPCManager.NPCDetails details, Player player, String message, String taskType) {
        // Create appropriate task configuration
        TaskConfiguration config = createTaskConfiguration(taskType, message);
        
        // Check if NPC is already doing a task
        if (taskManager.getCurrentTask(details.getCitizensId()) != null) {
            // Check if we should interrupt or queue
            if (config.canInterrupt()) {
                taskManager.switchTask(details, taskType, player, config);
                return "I'll switch to " + taskType + " right away!";
            } else {
                taskManager.assignTask(details, taskType, player, config);
                return "I'll add " + taskType + " to my task list!";
            }
        } else {
            taskManager.assignTask(details, taskType, player, config);
            return "I'll start " + taskType + " right now!";
        }
    }
    
    /**
     * Handles tool giving
     */
    private String handleToolGiving(NPCManager.NPCDetails details, Player player, String message) {
        // Detect what tool the player wants to give
        Material toolToGive = detectToolFromMessage(message);
        
        if (toolToGive != null) {
            if (taskManager.giveToolToNPC(details, player, toolToGive)) {
                return "Thank you for the " + toolToGive.name().toLowerCase().replace("_", " ") + "!";
            }
        }
        
        return "I'm not sure what tool you want to give me. Can you be more specific?";
    }
    
    /**
     * Handles status requests
     */
    private String handleStatusRequest(NPCManager.NPCDetails details, Player player) {
        TaskQueue queue = taskManager.getTaskQueue(details.getCitizensId());
        if (queue == null || queue.isEmpty()) {
            return "I'm not currently working on any tasks. What would you like me to do?";
        }
        
        StringBuilder status = new StringBuilder();
        status.append("Current status:\n");
        status.append("&7- ").append(queue.getStatusSummary());
        
        if (queue.hasActiveTask()) {
            status.append("\n&7- Progress: ").append(queue.getCurrentTask().getProgressPercentage()).append("%");
        }
        
        return status.toString();
    }
    
    /**
     * Handles task stopping
     */
    private String handleTaskStopping(NPCManager.NPCDetails details, Player player) {
        taskManager.clearAllTasks(details);
        return "I've stopped all my tasks. What would you like me to do now?";
    }
    
    /**
     * Creates task configuration based on message content
     */
    private TaskConfiguration createTaskConfiguration(String taskType, String message) {
        TaskConfiguration config;
        
        switch (taskType) {
            case "woodcutting":
                config = TaskConfiguration.createWoodcuttingConfig();
                break;
            case "mining":
                config = TaskConfiguration.createMiningConfig();
                break;
            case "farming":
                config = TaskConfiguration.createFarmingConfig();
                break;
            default:
                config = new TaskConfiguration();
        }
        
        // Parse additional parameters from message
        parseTaskParameters(config, message);
        
        return config;
    }
    
    /**
     * Parses additional task parameters from message
     */
    private void parseTaskParameters(TaskConfiguration config, String message) {
        // Parse duration (e.g., "for 10 minutes", "for an hour")
        if (message.contains("minute") || message.contains("hour") || message.contains("second")) {
            // Simple duration parsing - could be enhanced
            if (message.contains("hour")) {
                config.setDuration(72000); // 1 hour
            } else if (message.contains("minute")) {
                config.setDuration(6000); // 5 minutes default
            }
        }
        
        // Parse repetitions (e.g., "3 times", "multiple times")
        if (message.contains("time")) {
            // Simple repetition parsing
            config.setRepetitions(3);
        }
    }
    
    /**
     * Detects tool type from message
     */
    private Material detectToolFromMessage(String message) {
        String lowerMessage = message.toLowerCase();
        
        for (Map.Entry<String, Set<String>> entry : toolKeywords.entrySet()) {
            String toolType = entry.getKey();
            Set<String> keywords = entry.getValue();
            
            if (keywords.stream().anyMatch(lowerMessage::contains)) {
                // Map tool type to material
                switch (toolType) {
                    case "axe":
                        return Material.WOODEN_AXE;
                    case "pickaxe":
                        return Material.WOODEN_PICKAXE;
                    case "hoe":
                        return Material.WOODEN_HOE;
                    case "shovel":
                        return Material.WOODEN_SHOVEL;
                    case "sword":
                        return Material.WOODEN_SWORD;
                }
            }
        }
        
        return null;
    }
    
    // Helper methods for message classification
    private boolean isGreeting(String message) {
        String[] greetings = {"hello", "hi", "hey", "greetings", "good morning", "good afternoon", "good evening"};
        return Arrays.stream(greetings).anyMatch(message::contains);
    }
    
    private boolean isHelpRequest(String message) {
        Set<String> helpKeywords = actionKeywords.get("help");
        return helpKeywords != null && helpKeywords.stream().anyMatch(message::contains);
    }
    
    private boolean isFarewell(String message) {
        String[] farewells = {"bye", "goodbye", "see you", "farewell", "later", "thanks", "thank you"};
        return Arrays.stream(farewells).anyMatch(message::contains);
    }
    
    private String generateGreeting(NPCManager.NPCDetails details, Player player) {
        String[] greetings = {
            "Hello there, " + player.getName() + "! How can I help you today?",
            "Greetings, " + player.getName() + "! What would you like me to do?",
            "Hi " + player.getName() + "! I'm ready to work. What's the task?",
            "Good to see you, " + player.getName() + "! What can I help you with?"
        };
        return greetings[new Random().nextInt(greetings.length)];
    }
    
    private String generateHelpResponse(NPCManager.NPCDetails details) {
        return "I can help you with woodcutting, mining, farming, planting, following, repairing, and smithing tasks. " +
               "Just tell me what you need! For example: 'Can you chop some wood for me?' or 'I need you to mine some ore.'";
    }
    
    private String generateFarewell(NPCManager.NPCDetails details, Player player) {
        String[] farewells = {
            "Take care, " + player.getName() + "! Come back anytime you need help.",
            "Goodbye, " + player.getName() + "! Safe travels!",
            "See you later, " + player.getName() + "! Don't hesitate to ask if you need anything.",
            "Farewell, " + player.getName() + "! I'll be here if you need me."
        };
        return farewells[new Random().nextInt(farewells.length)];
    }
    
    private String generateDefaultResponse(NPCManager.NPCDetails details, Player player) {
        String[] responses = {
            "I'm here to help! What would you like me to do?",
            "I can assist with various tasks. What do you need?",
            "I'm ready to work. Just tell me what to do!",
            "What can I help you with today?"
        };
        return responses[new Random().nextInt(responses.length)];
    }
    
    /**
     * Clears conversation for a player
     */
    public void clearConversation(Player player) {
        activeConversations.remove(player.getUniqueId());
    }
    
    /**
     * Inner class for tracking conversations
     */
    public static class NPCConversation {
        private int npcId;
        private long lastMessage;
        
        public int getNPCId() { return npcId; }
        public void setNPCId(int npcId) { this.npcId = npcId; }
        
        public long getLastMessage() { return lastMessage; }
        public void setLastMessage(long lastMessage) { this.lastMessage = lastMessage; }
    }
}