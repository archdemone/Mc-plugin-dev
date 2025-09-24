package dev.archdemone.npcintegration.managers;

import dev.archdemone.npcintegration.NPCIntegrationPlugin;
import dev.archdemone.npcintegration.integrations.CitizensIntegration;
import dev.archdemone.npcintegration.integrations.MythicMobsIntegration;
import dev.archdemone.npcintegration.integrations.ModelEngineIntegration;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Manager class for handling NPC operations across all integrations
 */
public class NPCManager {
    
    private final NPCIntegrationPlugin plugin;
    private final Map<Integer, NPCDetails> npcDetails;
    
    public NPCManager(NPCIntegrationPlugin plugin) {
        this.plugin = plugin;
        this.npcDetails = new HashMap<>();
        loadNPCs();
    }
    
    /**
     * Creates a complete NPC with all integrations
     * @param location The location to spawn the NPC
     * @param name The name of the NPC
     * @param npcType The type of NPC
     * @return The created NPC details or null if failed
     */
    public NPCDetails createCompleteNPC(Location location, String name, String npcType) {
        NPCDetails details = new NPCDetails();
        details.setName(name);
        details.setType(npcType);
        details.setLocation(location);
        
        // Create Citizens NPC
        CitizensIntegration citizensIntegration = plugin.getCitizensIntegration();
        if (citizensIntegration != null && citizensIntegration.isAvailable()) {
            NPC citizensNPC = citizensIntegration.createNPC(location, name, npcType);
            if (citizensNPC != null) {
                details.setCitizensNPC(citizensNPC);
                details.setCitizensId(citizensNPC.getId());
            }
        }
        
        // Spawn MythicMobs entity
        MythicMobsIntegration mythicMobsIntegration = plugin.getMythicMobsIntegration();
        if (mythicMobsIntegration != null && mythicMobsIntegration.isAvailable()) {
            String mobType = plugin.getConfig().getString("npc-types." + npcType + ".mythicmob-type", npcType);
            Entity mythicMob = mythicMobsIntegration.spawnMythicMob(location, mobType);
            if (mythicMob != null) {
                details.setMythicMobEntity(mythicMob);
                details.setMythicMobType(mobType);
            }
        }
        
        // Apply ModelEngine model
        ModelEngineIntegration modelEngineIntegration = plugin.getModelEngineIntegration();
        if (modelEngineIntegration != null && modelEngineIntegration.isAvailable()) {
            String modelName = plugin.getConfig().getString("npc-types." + npcType + ".model", npcType);
            if (modelEngineIntegration.isModelRegistered(modelName)) {
                // Apply to MythicMobs entity if available, otherwise to Citizens NPC
                Entity targetEntity = details.getMythicMobEntity();
                if (targetEntity == null && details.getCitizensNPC() != null) {
                    targetEntity = (Entity) details.getCitizensNPC().getEntity();
                }
                
                if (targetEntity != null) {
                    boolean applied = modelEngineIntegration.applyModel(targetEntity, modelName);
                    if (applied) {
                        details.setModelName(modelName);
                    }
                }
            }
        }
        
        // Store the details
        npcDetails.put(details.getCitizensId(), details);
        saveNPCs();
        
        return details;
    }
    
    /**
     * Removes an NPC completely
     * @param npcId The ID of the NPC to remove
     * @return true if successful
     */
    public boolean removeNPC(int npcId) {
        NPCDetails details = npcDetails.get(npcId);
        if (details == null) {
            return false;
        }
        
        boolean success = true;
        
        // Remove Citizens NPC
        CitizensIntegration citizensIntegration = plugin.getCitizensIntegration();
        if (citizensIntegration != null && details.getCitizensNPC() != null) {
            success &= citizensIntegration.removeNPC(npcId);
        }
        
        // Remove MythicMobs entity
        MythicMobsIntegration mythicMobsIntegration = plugin.getMythicMobsIntegration();
        if (mythicMobsIntegration != null && details.getMythicMobEntity() != null) {
            success &= mythicMobsIntegration.removeMythicMob(details.getMythicMobEntity());
        }
        
        // Remove from our tracking
        npcDetails.remove(npcId);
        saveNPCs();
        
        return success;
    }
    
    /**
     * Gets NPC details by ID
     * @param npcId The NPC ID
     * @return The NPC details or null if not found
     */
    public NPCDetails getNPCDetails(int npcId) {
        return npcDetails.get(npcId);
    }
    
    /**
     * Gets all NPC details
     * @return Collection of all NPC details
     */
    public Collection<NPCDetails> getAllNPCDetails() {
        return npcDetails.values();
    }
    
    /**
     * Gets NPC details by type
     * @param npcType The NPC type
     * @return List of NPC details of the specified type
     */
    public List<NPCDetails> getNPCsByType(String npcType) {
        List<NPCDetails> result = new ArrayList<>();
        for (NPCDetails details : npcDetails.values()) {
            if (npcType.equals(details.getType())) {
                result.add(details);
            }
        }
        return result;
    }
    
    /**
     * Makes an NPC perform a specific action
     * @param npcId The NPC ID
     * @param action The action to perform
     * @param player The player involved in the action
     * @return true if successful
     */
    public boolean performNPCAction(int npcId, String action, Player player) {
        NPCDetails details = npcDetails.get(npcId);
        if (details == null) {
            return false;
        }
        
        switch (action.toLowerCase()) {
            case "look":
                return makeNPCLookAt(details, player);
            case "follow":
                return makeNPCFollow(details, player);
            case "stop":
                return makeNPCStop(details);
            default:
                return false;
        }
    }
    
    private boolean makeNPCLookAt(NPCDetails details, Player player) {
        CitizensIntegration citizensIntegration = plugin.getCitizensIntegration();
        if (citizensIntegration != null && details.getCitizensNPC() != null) {
            citizensIntegration.makeNPCLookAt(details.getCitizensNPC(), player);
            return true;
        }
        return false;
    }
    
    private boolean makeNPCFollow(NPCDetails details, Player player) {
        CitizensIntegration citizensIntegration = plugin.getCitizensIntegration();
        if (citizensIntegration != null && details.getCitizensNPC() != null) {
            citizensIntegration.makeNPCFollow(details.getCitizensNPC(), player);
            return true;
        }
        return false;
    }
    
    private boolean makeNPCStop(NPCDetails details) {
        CitizensIntegration citizensIntegration = plugin.getCitizensIntegration();
        if (citizensIntegration != null && details.getCitizensNPC() != null) {
            details.getCitizensNPC().getNavigator().cancelNavigation();
            return true;
        }
        return false;
    }
    
    private void loadNPCs() {
        // Load NPCs from config file
        FileConfiguration config = plugin.getConfig();
        ConfigurationSection npcsSection = config.getConfigurationSection("saved-npcs");
        if (npcsSection != null) {
            for (String key : npcsSection.getKeys(false)) {
                try {
                    int npcId = Integer.parseInt(key);
                    NPCDetails details = new NPCDetails();
                    details.setCitizensId(npcId);
                    details.setName(npcsSection.getString(key + ".name"));
                    details.setType(npcsSection.getString(key + ".type"));
                    details.setLocation((Location) npcsSection.get(key + ".location"));
                    
                    // Relink with existing Citizens NPC
                    CitizensIntegration citizensIntegration = plugin.getCitizensIntegration();
                    if (citizensIntegration != null) {
                        NPC npc = citizensIntegration.getNPC(npcId);
                        if (npc != null) {
                            details.setCitizensNPC(npc);
                        }
                    }
                    
                    npcDetails.put(npcId, details);
                } catch (NumberFormatException e) {
                    plugin.getLogger().warning("Invalid NPC ID in config: " + key);
                }
            }
        }
    }
    
    public void saveNPCs() {
        // Save NPCs to config file
        FileConfiguration config = plugin.getConfig();
        config.set("saved-npcs", null); // Clear existing
        
        for (Map.Entry<Integer, NPCDetails> entry : npcDetails.entrySet()) {
            int npcId = entry.getKey();
            NPCDetails details = entry.getValue();
            
            String basePath = "saved-npcs." + npcId;
            config.set(basePath + ".name", details.getName());
            config.set(basePath + ".type", details.getType());
            config.set(basePath + ".location", details.getLocation());
        }
        
        plugin.saveConfig();
    }
    
    public void saveAll() {
        saveNPCs();
    }
    
    /**
     * Inner class to store NPC details across all integrations
     */
    public static class NPCDetails {
        private int citizensId;
        private NPC citizensNPC;
        private Entity mythicMobEntity;
        private String mythicMobType;
        private String modelName;
        private String name;
        private String type;
        private Location location;
        
        // Getters and Setters
        public int getCitizensId() { return citizensId; }
        public void setCitizensId(int citizensId) { this.citizensId = citizensId; }
        
        public NPC getCitizensNPC() { return citizensNPC; }
        public void setCitizensNPC(NPC citizensNPC) { this.citizensNPC = citizensNPC; }
        
        public Entity getMythicMobEntity() { return mythicMobEntity; }
        public void setMythicMobEntity(Entity mythicMobEntity) { this.mythicMobEntity = mythicMobEntity; }
        
        public String getMythicMobType() { return mythicMobType; }
        public void setMythicMobType(String mythicMobType) { this.mythicMobType = mythicMobType; }
        
        public String getModelName() { return modelName; }
        public void setModelName(String modelName) { this.modelName = modelName; }
        
        public String getName() { return name; }
        public void setName(String name) { this.name = name; }
        
        public String getType() { return type; }
        public void setType(String type) { this.type = type; }
        
        public Location getLocation() { return location; }
        public void setLocation(Location location) { this.location = location; }
    }
}