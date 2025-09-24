package dev.archdemone.npcintegration.integrations;

import dev.archdemone.npcintegration.NPCIntegrationPlugin;
import io.lumine.mythic.api.adapters.AbstractEntity;
import io.lumine.mythic.api.adapters.AbstractLocation;
import io.lumine.mythic.api.mobs.MythicMob;
import io.lumine.mythic.bukkit.BukkitAdapter;
import io.lumine.mythic.bukkit.MythicBukkit;
import org.bukkit.Location;
import org.bukkit.entity.Entity;

import java.util.Optional;

/**
 * Integration class for MythicMobs plugin
 * Handles custom mob creation and behavior through MythicMobs API
 */
public class MythicMobsIntegration {
    
    private final NPCIntegrationPlugin plugin;
    private final MythicBukkit mythicBukkit;
    
    public MythicMobsIntegration(NPCIntegrationPlugin plugin) {
        this.plugin = plugin;
        this.mythicBukkit = MythicBukkit.inst();
    }
    
    /**
     * Spawns a MythicMob at the specified location
     * @param location The location to spawn the mob
     * @param mobType The type of MythicMob to spawn
     * @param level The level of the mob (optional)
     * @return The spawned entity or null if failed
     */
    public Entity spawnMythicMob(Location location, String mobType, int level) {
        try {
            AbstractLocation mythicLocation = BukkitAdapter.adapt(location);
            
            // Get the MythicMob type
            Optional<MythicMob> mythicMobOpt = mythicBukkit.getMobManager().getMythicMob(mobType);
            if (!mythicMobOpt.isPresent()) {
                plugin.getLogger().warning("MythicMob type not found: " + mobType);
                return null;
            }
            
            // Spawn the mob
            Optional<AbstractEntity> spawnedEntity = mythicBukkit.getMobManager()
                .spawnMythicMob(mythicMobOpt.get(), mythicLocation, level);
            
            if (spawnedEntity.isPresent()) {
                Entity entity = (Entity) spawnedEntity.get().getBukkitEntity();
                plugin.getLogger().info("Spawned MythicMob: " + mobType + " at " + location);
                return entity;
            } else {
                plugin.getLogger().warning("Failed to spawn MythicMob: " + mobType);
                return null;
            }
            
        } catch (Exception e) {
            plugin.getLogger().severe("Error spawning MythicMob: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Spawns a MythicMob at the specified location with default level
     * @param location The location to spawn the mob
     * @param mobType The type of MythicMob to spawn
     * @return The spawned entity or null if failed
     */
    public Entity spawnMythicMob(Location location, String mobType) {
        return spawnMythicMob(location, mobType, 1);
    }
    
    /**
     * Checks if a MythicMob type exists
     * @param mobType The mob type to check
     * @return true if the mob type exists
     */
    public boolean isValidMythicMobType(String mobType) {
        return mythicBukkit.getMobManager().getMythicMob(mobType).isPresent();
    }
    
    /**
     * Gets a MythicMob by type name
     * @param mobType The mob type name
     * @return Optional containing the MythicMob if found
     */
    public Optional<MythicMob> getMythicMob(String mobType) {
        return mythicBukkit.getMobManager().getMythicMob(mobType);
    }
    
    /**
     * Checks if an entity is a MythicMob
     * @param entity The entity to check
     * @return true if the entity is a MythicMob
     */
    public boolean isMythicMob(Entity entity) {
        return mythicBukkit.getMobManager().isMythicMob(BukkitAdapter.adapt(entity));
    }
    
    /**
     * Gets the MythicMob type of an entity
     * @param entity The entity to check
     * @return The MythicMob type name or null if not a MythicMob
     */
    public String getMythicMobType(Entity entity) {
        AbstractEntity mythicEntity = BukkitAdapter.adapt(entity);
        if (mythicBukkit.getMobManager().isMythicMob(mythicEntity)) {
            return mythicBukkit.getMobManager().getMythicMobInstance(mythicEntity)
                .map(instance -> instance.getType().getInternalName())
                .orElse(null);
        }
        return null;
    }
    
    /**
     * Removes a MythicMob entity
     * @param entity The entity to remove
     * @return true if successful
     */
    public boolean removeMythicMob(Entity entity) {
        try {
            AbstractEntity mythicEntity = BukkitAdapter.adapt(entity);
            if (mythicBukkit.getMobManager().isMythicMob(mythicEntity)) {
                entity.remove();
                return true;
            }
            return false;
        } catch (Exception e) {
            plugin.getLogger().severe("Error removing MythicMob: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Checks if MythicMobs is available
     * @return true if MythicMobs is available
     */
    public boolean isAvailable() {
        return mythicBukkit != null;
    }
    
    /**
     * Gets the MythicBukkit instance
     * @return The MythicBukkit instance
     */
    public MythicBukkit getMythicBukkit() {
        return mythicBukkit;
    }
}