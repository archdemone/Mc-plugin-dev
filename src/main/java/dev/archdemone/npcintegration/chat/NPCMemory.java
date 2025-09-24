package dev.archdemone.npcintegration.chat;

import java.util.*;

/**
 * Memory system for NPCs to remember conversations and tasks
 */
public class NPCMemory {
    
    private final Map<String, List<ConversationEntry>> playerConversations;
    private final Map<String, Long> playerLastSeen;
    private final Map<String, List<String>> playerTasks;
    private final Set<String> knownPlayers;
    
    public NPCMemory() {
        this.playerConversations = new HashMap<>();
        this.playerLastSeen = new HashMap<>();
        this.playerTasks = new HashMap<>();
        this.knownPlayers = new HashSet<>();
    }
    
    /**
     * Adds a conversation entry for a player
     */
    public void addConversation(String playerName, String playerMessage, String npcResponse) {
        playerConversations.computeIfAbsent(playerName, k -> new ArrayList<>())
            .add(new ConversationEntry(System.currentTimeMillis(), playerMessage, npcResponse));
        
        // Keep only last 10 conversations per player
        List<ConversationEntry> conversations = playerConversations.get(playerName);
        if (conversations.size() > 10) {
            conversations.remove(0);
        }
        
        playerLastSeen.put(playerName, System.currentTimeMillis());
    }
    
    /**
     * Remembers a player
     */
    public void rememberPlayer(String playerName) {
        knownPlayers.add(playerName);
        playerLastSeen.put(playerName, System.currentTimeMillis());
    }
    
    /**
     * Checks if NPC has met this player before
     */
    public boolean hasMetPlayer(String playerName) {
        return knownPlayers.contains(playerName);
    }
    
    /**
     * Adds a task for a player
     */
    public void addTask(String taskType, String playerName) {
        playerTasks.computeIfAbsent(playerName, k -> new ArrayList<>()).add(taskType);
        playerLastSeen.put(playerName, System.currentTimeMillis());
    }
    
    /**
     * Gets tasks for a player
     */
    public List<String> getPlayerTasks(String playerName) {
        return playerTasks.getOrDefault(playerName, new ArrayList<>());
    }
    
    /**
     * Gets conversation history with a player
     */
    public List<ConversationEntry> getConversationHistory(String playerName) {
        return playerConversations.getOrDefault(playerName, new ArrayList<>());
    }
    
    /**
     * Gets the last time a player was seen
     */
    public long getLastSeen(String playerName) {
        return playerLastSeen.getOrDefault(playerName, 0L);
    }
    
    /**
     * Gets all known players
     */
    public Set<String> getKnownPlayers() {
        return new HashSet<>(knownPlayers);
    }
    
    /**
     * Clears old conversations (older than 24 hours)
     */
    public void clearOldConversations() {
        long dayAgo = System.currentTimeMillis() - (24 * 60 * 60 * 1000);
        
        playerConversations.entrySet().removeIf(entry -> {
            List<ConversationEntry> conversations = entry.getValue();
            conversations.removeIf(conv -> conv.getTimestamp() < dayAgo);
            return conversations.isEmpty();
        });
    }
    
    /**
     * Gets a summary of recent conversations for context
     */
    public String getRecentContext(String playerName) {
        List<ConversationEntry> conversations = getConversationHistory(playerName);
        if (conversations.isEmpty()) {
            return "";
        }
        
        // Get last 3 conversations for context
        int start = Math.max(0, conversations.size() - 3);
        List<ConversationEntry> recent = conversations.subList(start, conversations.size());
        
        StringBuilder context = new StringBuilder();
        for (ConversationEntry entry : recent) {
            context.append(entry.getPlayerMessage()).append(" -> ").append(entry.getNpcResponse()).append("; ");
        }
        
        return context.toString();
    }
    
    /**
     * Inner class for conversation entries
     */
    public static class ConversationEntry {
        private final long timestamp;
        private final String playerMessage;
        private final String npcResponse;
        
        public ConversationEntry(long timestamp, String playerMessage, String npcResponse) {
            this.timestamp = timestamp;
            this.playerMessage = playerMessage;
            this.npcResponse = npcResponse;
        }
        
        public long getTimestamp() { return timestamp; }
        public String getPlayerMessage() { return playerMessage; }
        public String getNpcResponse() { return npcResponse; }
    }
}