package dev.archdemone.npcintegration.integrations;

import dev.archdemone.npcintegration.NPCIntegrationPlugin;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.Optional;

/**
 * Stub implementation for MythicMobs integration
 * This will be replaced with proper integration when dependencies are available
 */
public class MythicMobsIntegration {
    
    private final NPCIntegrationPlugin plugin;
    
    public MythicMobsIntegration(NPCIntegrationPlugin plugin) {
        this.plugin = plugin;
        plugin.getLogger().warning("MythicMobs integration is not available - using stub implementation");
    }
    
    /**
     * Spawns a MythicMob at the specified location (stub implementation)
     */
    public Entity spawnMythicMob(Location location, String mobType, int level) {
        plugin.getLogger().warning("MythicMobs not available - cannot spawn mob: " + mobType);
        return null;
    }
    
    /**
     * Spawns a MythicMob at the specified location with default level (stub implementation)
     */
    public Entity spawnMythicMob(Location location, String mobType) {
        return spawnMythicMob(location, mobType, 1);
    }
    
    /**
     * Checks if a MythicMob type exists (stub implementation)
     */
    public boolean isValidMythicMobType(String mobType) {
        return false; // Always return false since MythicMobs is not available
    }
    
    /**
     * Gets a MythicMob by type name (stub implementation)
     */
    public Optional<Object> getMythicMob(String mobType) {
        return Optional.empty();
    }
    
    /**
     * Checks if an entity is a MythicMob (stub implementation)
     */
    public boolean isMythicMob(Entity entity) {
        return false; // Always return false since MythicMobs is not available
    }
    
    /**
     * Gets the MythicMob type of an entity (stub implementation)
     */
    public String getMythicMobType(Entity entity) {
        return null;
    }
    
    /**
     * Removes a MythicMob entity (stub implementation)
     */
    public boolean removeMythicMob(Entity entity) {
        return false;
    }
    
    /**
     * Checks if MythicMobs is available
     */
    public boolean isAvailable() {
        return false; // Always false since we're using stub implementation
    }
    
    /**
     * Gets the MythicBukkit instance (stub implementation)
     */
    public Object getMythicBukkit() {
        return null;
    }
}