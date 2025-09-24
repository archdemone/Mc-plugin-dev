package dev.archdemone.npcintegration.chat;

import dev.archdemone.npcintegration.NPCIntegrationPlugin;
import dev.archdemone.npcintegration.managers.NPCManager;
import dev.archdemone.npcintegration.utils.MessageUtil;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.regex.Pattern;

/**
 * Chat system for NPC interactions
 * Allows players to talk to NPCs through natural conversation
 */
public class NPCChatSystem {
    
    private final NPCIntegrationPlugin plugin;
    private final NPCManager npcManager;
    private final Map<UUID, NPCConversation> activeConversations;
    private final Map<Integer, NPCMemory> npcMemories;
    
    // Patterns to detect when a player is talking to an NPC
    private final Pattern npcNamePattern = Pattern.compile("@(\\w+)", Pattern.CASE_INSENSITIVE);
    private final Pattern npcIdPattern = Pattern.compile("#(\\d+)", Pattern.CASE_INSENSITIVE);
    
    public NPCChatSystem(NPCIntegrationPlugin plugin) {
        this.plugin = plugin;
        this.npcManager = plugin.getNPCManager();
        this.activeConversations = new HashMap<>();
        this.npcMemories = new HashMap<>();
    }
    
    /**
     * Processes a chat message to see if it's directed at an NPC
     * @param player The player who sent the message
     * @param message The message content
     * @return true if the message was processed as an NPC interaction
     */
    public boolean processChatMessage(Player player, String message) {
        String lowerMessage = message.toLowerCase();
        
        // Check for NPC name mentions (@NPCName)
        var nameMatcher = npcNamePattern.matcher(message);
        if (nameMatcher.find()) {
            String npcName = nameMatcher.group(1);
            return handleNPCMention(player, npcName, message);
        }
        
        // Check for NPC ID mentions (#ID)
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
        
        // Check if player is in an active conversation
        UUID playerId = player.getUniqueId();
        if (activeConversations.containsKey(playerId)) {
            NPCConversation conversation = activeConversations.get(playerId);
            return continueConversation(player, conversation, message);
        }
        
        // Check for nearby NPCs (within 5 blocks)
        return handleNearbyNPCInteraction(player, message);
    }
    
    /**
     * Handles when a player mentions an NPC by name
     */
    private boolean handleNPCMention(Player player, String npcName, String message) {
        // Find NPC by name
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
        
        // Process the message and generate response
        String response = processNPCMessage(details, player, message);
        
        // Send response to player
        MessageUtil.sendInfo(player, "&6[" + details.getName() + "] &e" + response);
        
        // Start or continue conversation
        UUID playerId = player.getUniqueId();
        NPCConversation conversation = activeConversations.getOrDefault(playerId, new NPCConversation());
        conversation.setNPCId(npcId);
        conversation.setLastMessage(System.currentTimeMillis());
        activeConversations.put(playerId, conversation);
        
        // Update NPC memory
        updateNPCMemory(details, player, message, response);
        
        return true;
    }
    
    /**
     * Handles interaction with nearby NPCs
     */
    private boolean handleNearbyNPCInteraction(Player player, String message) {
        List<NPCManager.NPCDetails> nearbyNPCs = new ArrayList<>();
        
        // Find NPCs within 5 blocks
        for (NPCManager.NPCDetails details : npcManager.getAllNPCDetails()) {
            if (details.getCitizensNPC() != null && details.getCitizensNPC().isSpawned()) {
                double distance = player.getLocation().distance(details.getCitizensNPC().getEntity().getLocation());
                if (distance <= 5.0) {
                    nearbyNPCs.add(details);
                }
            }
        }
        
        if (nearbyNPCs.isEmpty()) {
            return false; // No nearby NPCs, let normal chat continue
        }
        
        // If multiple NPCs nearby, ask for clarification
        if (nearbyNPCs.size() > 1) {
            MessageUtil.sendInfo(player, "&7I can see " + nearbyNPCs.size() + " NPCs nearby. Use @Name or #ID to specify which one.");
            return false;
        }
        
        // Single NPC nearby, interact with it
        NPCManager.NPCDetails details = nearbyNPCs.get(0);
        return handleNPCInteraction(player, details.getCitizensId(), message);
    }
    
    /**
     * Continues an active conversation
     */
    private boolean continueConversation(Player player, NPCConversation conversation, String message) {
        // Check if conversation is still active (within 30 seconds)
        if (System.currentTimeMillis() - conversation.getLastMessage() > 30000) {
            activeConversations.remove(player.getUniqueId());
            return false;
        }
        
        NPCManager.NPCDetails details = npcManager.getNPCDetails(conversation.getNPCId());
        if (details == null) {
            activeConversations.remove(player.getUniqueId());
            return false;
        }
        
        // Process the message
        String response = processNPCMessage(details, player, message);
        
        // Send response
        MessageUtil.sendInfo(player, "&6[" + details.getName() + "] &e" + response);
        
        // Update conversation
        conversation.setLastMessage(System.currentTimeMillis());
        
        return true;
    }
    
    /**
     * Processes a message and generates an NPC response
     */
    private String processNPCMessage(NPCManager.NPCDetails details, Player player, String message) {
        String lowerMessage = message.toLowerCase();
        String npcType = details.getType().toLowerCase();
        
        // Get NPC's memory for context
        NPCMemory memory = npcMemories.getOrDefault(details.getCitizensId(), new NPCMemory());
        
        // Handle greetings
        if (isGreeting(lowerMessage)) {
            return generateGreeting(details, player, memory);
        }
        
        // Handle task assignments
        if (isTaskAssignment(lowerMessage)) {
            return handleTaskAssignment(details, player, message, memory);
        }
        
        // Handle questions
        if (isQuestion(lowerMessage)) {
            return handleQuestion(details, player, message, memory);
        }
        
        // Handle requests for help
        if (isHelpRequest(lowerMessage)) {
            return generateHelpResponse(details, npcType);
        }
        
        // Handle farewells
        if (isFarewell(lowerMessage)) {
            activeConversations.remove(player.getUniqueId());
            return generateFarewell(details, player);
        }
        
        // Default response based on NPC type
        return generateDefaultResponse(details, npcType, memory);
    }
    
    /**
     * Handles task assignment to NPCs
     */
    private String handleTaskAssignment(NPCManager.NPCDetails details, Player player, String message, NPCMemory memory) {
        String npcType = details.getType().toLowerCase();
        
        switch (npcType) {
            case "woodcutter":
                return handleWoodcutterTask(details, player, message, memory);
            case "blacksmith":
                return handleBlacksmithTask(details, player, message, memory);
            default:
                return "I'm not sure what tasks I can help you with. Try asking me what I can do!";
        }
    }
    
    /**
     * Handles woodcutter-specific tasks
     */
    private String handleWoodcutterTask(NPCManager.NPCDetails details, Player player, String message, NPCMemory memory) {
        String lowerMessage = message.toLowerCase();
        
        if (lowerMessage.contains("chop") || lowerMessage.contains("cut") || lowerMessage.contains("wood")) {
            memory.addTask("woodcutting", player.getName());
            plugin.getTaskManager().assignTask(details, "woodcutting", player);
            return "I'll start chopping trees for you! I'll look for nearby trees and get to work.";
        }
        
        if (lowerMessage.contains("plant") || lowerMessage.contains("sapling")) {
            memory.addTask("planting", player.getName());
            plugin.getTaskManager().assignTask(details, "planting", player);
            return "I'll help you plant saplings! I'll look for suitable spots and plant them.";
        }
        
        if (lowerMessage.contains("follow")) {
            memory.addTask("following", player.getName());
            plugin.getTaskManager().assignTask(details, "following", player);
            return "I'll follow you around! Just say 'stop following' when you want me to stay put.";
        }
        
        return "I can help you with chopping trees, planting saplings, or following you around. What would you like me to do?";
    }
    
    /**
     * Handles blacksmith-specific tasks
     */
    private String handleBlacksmithTask(NPCManager.NPCDetails details, Player player, String message, NPCMemory memory) {
        String lowerMessage = message.toLowerCase();
        
        if (lowerMessage.contains("repair") || lowerMessage.contains("fix")) {
            memory.addTask("repairing", player.getName());
            plugin.getTaskManager().assignTask(details, "repairing", player);
            return "I'll help you repair your equipment! Bring me your damaged items and I'll fix them up.";
        }
        
        if (lowerMessage.contains("smith") || lowerMessage.contains("craft") || lowerMessage.contains("make")) {
            memory.addTask("smithing", player.getName());
            plugin.getTaskManager().assignTask(details, "smithing", player);
            return "I'll help you with smithing! What would you like me to craft for you?";
        }
        
        if (lowerMessage.contains("follow")) {
            memory.addTask("following", player.getName());
            plugin.getTaskManager().assignTask(details, "following", player);
            return "I'll follow you around! Just say 'stop following' when you want me to stay put.";
        }
        
        return "I can help you with repairing equipment, smithing items, or following you around. What would you like me to do?";
    }
    
    /**
     * Generates appropriate greetings
     */
    private String generateGreeting(NPCManager.NPCDetails details, Player player, NPCMemory memory) {
        String npcType = details.getType().toLowerCase();
        String playerName = player.getName();
        
        if (memory.hasMetPlayer(playerName)) {
            return "Hello again, " + playerName + "! Good to see you back. How can I help you today?";
        }
        
        memory.rememberPlayer(playerName);
        
        switch (npcType) {
            case "woodcutter":
                return "Greetings, " + playerName + "! I'm " + details.getName() + ", the local woodcutter. I can help you with chopping trees, planting saplings, or any other wood-related tasks!";
            case "blacksmith":
                return "Hello there, " + playerName + "! I'm " + details.getName() + ", the blacksmith. I can repair your equipment, craft items, or help with any smithing needs!";
            default:
                return "Hello, " + playerName + "! I'm " + details.getName() + ". How can I assist you today?";
        }
    }
    
    /**
     * Generates help responses
     */
    private String generateHelpResponse(NPCManager.NPCDetails details, String npcType) {
        switch (npcType) {
            case "woodcutter":
                return "I can help you with:\n&7- Chopping trees: 'chop some trees for me'\n&7- Planting saplings: 'plant some saplings'\n&7- Following you: 'follow me'\n&7- Stopping: 'stop following' or 'stay here'";
            case "blacksmith":
                return "I can help you with:\n&7- Repairing equipment: 'repair my items'\n&7- Smithing: 'craft me some tools'\n&7- Following you: 'follow me'\n&7- Stopping: 'stop following' or 'stay here'";
            default:
                return "I can help you with various tasks. Try asking me what I can do!";
        }
    }
    
    /**
     * Generates farewell responses
     */
    private String generateFarewell(NPCManager.NPCDetails details, Player player) {
        String[] farewells = {
            "Take care, " + player.getName() + "! Come back anytime you need help.",
            "Goodbye, " + player.getName() + "! Safe travels!",
            "See you later, " + player.getName() + "! Don't hesitate to ask if you need anything.",
            "Farewell, " + player.getName() + "! I'll be here if you need me."
        };
        return farewells[new Random().nextInt(farewells.length)];
    }
    
    /**
     * Generates default responses
     */
    private String generateDefaultResponse(NPCManager.NPCDetails details, String npcType, NPCMemory memory) {
        String[] responses;
        
        switch (npcType) {
            case "woodcutter":
                responses = new String[]{
                    "I'm always ready to help with woodcutting tasks!",
                    "Need some wood? I'm your guy!",
                    "I love working with wood. What can I do for you?",
                    "Trees are my specialty! How can I help?"
                };
                break;
            case "blacksmith":
                responses = new String[]{
                    "I'm here to help with all your smithing needs!",
                    "Metal work is my passion. What do you need?",
                    "I can fix, craft, or improve your equipment!",
                    "Fire and steel - that's my expertise!"
                };
                break;
            default:
                responses = new String[]{
                    "I'm here to help! What do you need?",
                    "How can I assist you today?",
                    "I'm ready to help with whatever you need!",
                    "What can I do for you?"
                };
        }
        
        return responses[new Random().nextInt(responses.length)];
    }
    
    // Helper methods for message classification
    private boolean isGreeting(String message) {
        String[] greetings = {"hello", "hi", "hey", "greetings", "good morning", "good afternoon", "good evening"};
        return Arrays.stream(greetings).anyMatch(message::contains);
    }
    
    private boolean isTaskAssignment(String message) {
        String[] taskWords = {"can you", "please", "help me", "do this", "chop", "cut", "plant", "repair", "fix", "craft", "make", "follow"};
        return Arrays.stream(taskWords).anyMatch(message::contains);
    }
    
    private boolean isQuestion(String message) {
        return message.contains("?") || message.startsWith("what") || message.startsWith("how") || 
               message.startsWith("where") || message.startsWith("when") || message.startsWith("why");
    }
    
    private boolean isHelpRequest(String message) {
        String[] helpWords = {"help", "what can you do", "abilities", "skills", "tasks"};
        return Arrays.stream(helpWords).anyMatch(message::contains);
    }
    
    private boolean isFarewell(String message) {
        String[] farewells = {"bye", "goodbye", "see you", "farewell", "later", "thanks", "thank you"};
        return Arrays.stream(farewells).anyMatch(message::contains);
    }
    
    /**
     * Updates NPC memory with conversation
     */
    private void updateNPCMemory(NPCManager.NPCDetails details, Player player, String message, String response) {
        NPCMemory memory = npcMemories.getOrDefault(details.getCitizensId(), new NPCMemory());
        memory.addConversation(player.getName(), message, response);
        npcMemories.put(details.getCitizensId(), memory);
    }
    
    /**
     * Gets NPC memory
     */
    public NPCMemory getNPCMemory(int npcId) {
        return npcMemories.get(npcId);
    }
    
    /**
     * Clears active conversation for a player
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