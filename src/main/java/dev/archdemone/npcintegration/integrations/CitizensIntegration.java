package dev.archdemone.npcintegration.integrations;

import dev.archdemone.npcintegration.NPCIntegrationPlugin;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.trait.trait.Equipment;
import net.citizensnpcs.trait.LookClose;
import net.citizensnpcs.trait.SkinTrait;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

/**
 * Integration class for Citizens plugin
 * Handles NPC creation, management, and interaction through Citizens API
 */
public class CitizensIntegration {
    
    private final NPCIntegrationPlugin plugin;
    private final NPCRegistry registry;
    
    public CitizensIntegration(NPCIntegrationPlugin plugin) {
        this.plugin = plugin;
        this.registry = CitizensAPI.getNPCRegistry();
    }
    
    /**
     * Creates a new NPC at the specified location
     * @param location The location to spawn the NPC
     * @param name The name of the NPC
     * @param npcType The type of NPC (woodcutter, blacksmith, etc.)
     * @return The created NPC or null if failed
     */
    public NPC createNPC(Location location, String name, String npcType) {
        try {
            // Create the NPC
            NPC npc = registry.createNPC(EntityType.PLAYER, name);
            
            // Set basic properties
            npc.spawn(location);
            
            // Apply NPC type specific configuration
            applyNPCTypeConfiguration(npc, npcType);
            
            plugin.getLogger().info("Created NPC: " + name + " of type: " + npcType);
            return npc;
            
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to create NPC: " + e.getMessage());
            return null;
        }
    }
    
    /**
     * Applies configuration based on NPC type
     * @param npc The NPC to configure
     * @param npcType The type of NPC
     */
    private void applyNPCTypeConfiguration(NPC npc, String npcType) {
        String configPath = "npc-types." + npcType;
        
        // Set profession - Note: Profession trait may need different approach in newer Citizens versions
        // For now, we'll skip profession setting and rely on skin
        
        // Set skin based on profession
        String profession = plugin.getConfig().getString(configPath + ".profession", "fletcher");
        SkinTrait skinTrait = npc.getOrAddTrait(SkinTrait.class);
        skinTrait.setSkinName(profession);
        
        // Set equipment
        Equipment equipment = npc.getOrAddTrait(Equipment.class);
        for (String itemString : plugin.getConfig().getStringList(configPath + ".inventory")) {
            try {
                Material material = Material.valueOf(itemString.toUpperCase());
                ItemStack item = new ItemStack(material);
                
                // Set appropriate equipment slot based on item type
                if (material.name().contains("AXE") || material.name().contains("SWORD")) {
                    equipment.set(Equipment.EquipmentSlot.HAND, item);
                } else if (material.name().contains("HELMET")) {
                    equipment.set(Equipment.EquipmentSlot.HELMET, item);
                } else if (material.name().contains("CHESTPLATE")) {
                    equipment.set(Equipment.EquipmentSlot.CHESTPLATE, item);
                } else if (material.name().contains("LEGGINGS")) {
                    equipment.set(Equipment.EquipmentSlot.LEGGINGS, item);
                } else if (material.name().contains("BOOTS")) {
                    equipment.set(Equipment.EquipmentSlot.BOOTS, item);
                }
            } catch (IllegalArgumentException e) {
                plugin.getLogger().warning("Invalid material: " + itemString);
            }
        }
        
        // Enable look close behavior
        npc.getOrAddTrait(LookClose.class).lookClose(true);
    }
    
    /**
     * Removes an NPC by ID
     * @param id The ID of the NPC to remove
     * @return true if successful, false otherwise
     */
    public boolean removeNPC(int id) {
        try {
            NPC npc = registry.getById(id);
            if (npc != null) {
                npc.destroy();
                plugin.getLogger().info("Removed NPC with ID: " + id);
                return true;
            }
            return false;
        } catch (Exception e) {
            plugin.getLogger().severe("Failed to remove NPC: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Gets an NPC by ID
     * @param id The ID of the NPC
     * @return The NPC or null if not found
     */
    public NPC getNPC(int id) {
        return registry.getById(id);
    }
    
    /**
     * Gets an NPC by entity
     * @param entity The entity to find NPC for
     * @return The NPC or null if not found
     */
    public NPC getNPC(Entity entity) {
        // Citizens API doesn't have getByEntity, so we need to iterate through all NPCs
        for (NPC npc : registry) {
            if (npc.getEntity() != null && npc.getEntity().equals(entity)) {
                return npc;
            }
        }
        return null;
    }
    
    /**
     * Makes an NPC look at a player
     * @param npc The NPC
     * @param player The player to look at
     */
    public void makeNPCLookAt(NPC npc, Player player) {
        if (npc != null && npc.isSpawned()) {
            // Use Citizens navigation to look at player
            npc.getNavigator().setTarget(player.getLocation());
        }
    }
    
    /**
     * Makes an NPC follow a player
     * @param npc The NPC
     * @param player The player to follow
     */
    public void makeNPCFollow(NPC npc, Player player) {
        if (npc != null && npc.isSpawned()) {
            npc.getNavigator().setTarget(player.getLocation());
        }
    }
    
    /**
     * Checks if Citizens is available
     * @return true if Citizens is available
     */
    public boolean isAvailable() {
        return registry != null;
    }
    
    /**
     * Gets the NPC registry
     * @return The NPC registry
     */
    public NPCRegistry getRegistry() {
        return registry;
    }
}