package dev.archdemone.npcintegration.examples;

import dev.archdemone.npcintegration.NPCIntegrationPlugin;
import dev.archdemone.npcintegration.managers.NPCManager;
import dev.archdemone.npcintegration.integrations.CitizensIntegration;
import dev.archdemone.npcintegration.integrations.MythicMobsIntegration;
import dev.archdemone.npcintegration.integrations.ModelEngineIntegration;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Example implementation of a Woodcutter NPC using all three integrations
 * This demonstrates how to create a fully functional NPC with custom behavior
 */
public class WoodcutterExample {
    
    private final NPCIntegrationPlugin plugin;
    private final NPCManager npcManager;
    
    public WoodcutterExample(NPCIntegrationPlugin plugin) {
        this.plugin = plugin;
        this.npcManager = plugin.getNPCManager();
    }
    
    /**
     * Creates a complete woodcutter NPC with all integrations
     * @param location The location to spawn the NPC
     * @param name The name of the woodcutter
     * @return The created NPC details
     */
    public NPCManager.NPCDetails createWoodcutterNPC(Location location, String name) {
        plugin.getLogger().info("Creating woodcutter NPC: " + name);
        
        // Create the complete NPC using the manager
        NPCManager.NPCDetails details = npcManager.createCompleteNPC(location, name, "woodcutter");
        
        if (details != null) {
            // Set up additional woodcutter-specific behavior
            setupWoodcutterBehavior(details);
            plugin.getLogger().info("Woodcutter NPC created successfully with ID: " + details.getCitizensId());
        } else {
            plugin.getLogger().severe("Failed to create woodcutter NPC!");
        }
        
        return details;
    }
    
    /**
     * Sets up woodcutter-specific behavior and animations
     * @param details The NPC details to configure
     */
    private void setupWoodcutterBehavior(NPCManager.NPCDetails details) {
        // Set up Citizens NPC behavior
        CitizensIntegration citizensIntegration = plugin.getCitizensIntegration();
        if (citizensIntegration != null && details.getCitizensNPC() != null) {
            setupCitizensWoodcutter(details.getCitizensNPC());
        }
        
        // Set up MythicMobs behavior
        MythicMobsIntegration mythicMobsIntegration = plugin.getMythicMobsIntegration();
        if (mythicMobsIntegration != null && details.getMythicMobEntity() != null) {
            setupMythicMobsWoodcutter(details.getMythicMobEntity());
        }
        
        // Set up ModelEngine animations
        ModelEngineIntegration modelEngineIntegration = plugin.getModelEngineIntegration();
        if (modelEngineIntegration != null && details.getModelName() != null) {
            setupModelEngineWoodcutter(details);
        }
        
        // Start the woodcutting routine
        startWoodcuttingRoutine(details);
    }
    
    /**
     * Sets up Citizens NPC specific behavior for woodcutter
     * @param npc The Citizens NPC to configure
     */
    private void setupCitizensWoodcutter(NPC npc) {
        // Add custom traits for woodcutter behavior
        // This would require custom trait implementation in a real scenario
        
        // Set up equipment
        npc.getOrAddTrait(net.citizensnpcs.api.trait.trait.Equipment.class)
            .set(net.citizensnpcs.api.trait.trait.Equipment.EquipmentSlot.HAND, new ItemStack(Material.WOODEN_AXE));
        
        // Enable look close behavior
        npc.getOrAddTrait(net.citizensnpcs.trait.LookClose.class).lookClose(true);
        
        // Set up pathfinding for movement
        npc.getNavigator().getLocalParameters().speedModifier(0.5f);
    }
    
    /**
     * Sets up MythicMobs specific behavior for woodcutter
     * @param entity The MythicMobs entity to configure
     */
    private void setupMythicMobsWoodcutter(Entity entity) {
        // MythicMobs configuration would be done through config files
        // This method is a placeholder for any runtime configuration
        
        plugin.getLogger().info("MythicMobs woodcutter entity configured: " + entity.getType());
    }
    
    /**
     * Sets up ModelEngine animations for woodcutter
     * @param details The NPC details containing model information
     */
    private void setupModelEngineWoodcutter(NPCManager.NPCDetails details) {
        ModelEngineIntegration modelEngineIntegration = plugin.getModelEngineIntegration();
        Entity targetEntity = details.getMythicMobEntity();
        
        if (targetEntity == null && details.getCitizensNPC() != null) {
            targetEntity = (Entity) details.getCitizensNPC().getEntity();
        }
        
        if (targetEntity != null && details.getModelName() != null) {
            // Play idle animation
            modelEngineIntegration.playAnimation(targetEntity, details.getModelName(), "idle");
            
            plugin.getLogger().info("ModelEngine animations configured for woodcutter model: " + details.getModelName());
        }
    }
    
    /**
     * Starts the woodcutting routine for the NPC
     * @param details The NPC details
     */
    private void startWoodcuttingRoutine(NPCManager.NPCDetails details) {
        new BukkitRunnable() {
            private int cycleCount = 0;
            
            @Override
            public void run() {
                // Check if NPC still exists
                if (npcManager.getNPCDetails(details.getCitizensId()) == null) {
                    cancel();
                    return;
                }
                
                cycleCount++;
                
                // Every 20 ticks (1 second), perform woodcutting animation
                if (cycleCount % 20 == 0) {
                    performWoodcuttingAnimation(details);
                }
                
                // Every 200 ticks (10 seconds), look for nearby trees
                if (cycleCount % 200 == 0) {
                    lookForNearbyTrees(details);
                }
                
                // Every 1000 ticks (50 seconds), rest
                if (cycleCount % 1000 == 0) {
                    performRestAnimation(details);
                }
            }
        }.runTaskTimer(plugin, 20L, 1L); // Start after 1 second, repeat every tick
    }
    
    /**
     * Performs woodcutting animation
     * @param details The NPC details
     */
    private void performWoodcuttingAnimation(NPCManager.NPCDetails details) {
        ModelEngineIntegration modelEngineIntegration = plugin.getModelEngineIntegration();
        Entity targetEntity = details.getMythicMobEntity();
        
        if (targetEntity == null && details.getCitizensNPC() != null) {
            targetEntity = (Entity) details.getCitizensNPC().getEntity();
        }
        
        if (targetEntity != null && details.getModelName() != null && modelEngineIntegration != null) {
            final Entity finalTargetEntity = targetEntity;
            final String modelName = details.getModelName();
            // Play woodcutting animation
            modelEngineIntegration.playAnimation(finalTargetEntity, modelName, "woodcutting");
            
            // Stop animation after 2 seconds
            new BukkitRunnable() {
                @Override
                public void run() {
                    modelEngineIntegration.stopAnimation(finalTargetEntity, modelName, "woodcutting");
                }
            }.runTaskLater(plugin, 40L); // 2 seconds later
        }
    }
    
    /**
     * Makes the NPC look for nearby trees
     * @param details The NPC details
     */
    private void lookForNearbyTrees(NPCManager.NPCDetails details) {
        CitizensIntegration citizensIntegration = plugin.getCitizensIntegration();
        if (citizensIntegration != null && details.getCitizensNPC() != null) {
            NPC npc = details.getCitizensNPC();
            Location npcLocation = npc.getEntity().getLocation();
            
            // Look for trees in a 10 block radius
            for (int x = -10; x <= 10; x++) {
                for (int z = -10; z <= 10; z++) {
                    Location checkLocation = npcLocation.clone().add(x, 0, z);
                    
                    // Check if there's a tree (log) nearby
                    if (checkLocation.getBlock().getType().toString().contains("LOG")) {
                        // Make NPC look at the tree (using Citizens navigation)
                        npc.getNavigator().setTarget(checkLocation);
                        
                        // Move towards the tree
                        npc.getNavigator().setTarget(checkLocation);
                        
                        plugin.getLogger().info("Woodcutter " + details.getName() + " found a tree at " + checkLocation);
                        break;
                    }
                }
            }
        }
    }
    
    /**
     * Performs rest animation
     * @param details The NPC details
     */
    private void performRestAnimation(NPCManager.NPCDetails details) {
        ModelEngineIntegration modelEngineIntegration = plugin.getModelEngineIntegration();
        Entity targetEntity = details.getMythicMobEntity();
        
        if (targetEntity == null && details.getCitizensNPC() != null) {
            targetEntity = (Entity) details.getCitizensNPC().getEntity();
        }
        
        if (targetEntity != null && details.getModelName() != null && modelEngineIntegration != null) {
            final Entity finalTargetEntity = targetEntity;
            final String modelName = details.getModelName();
            // Play rest animation
            modelEngineIntegration.playAnimation(finalTargetEntity, modelName, "rest");
            
            // Stop animation after 5 seconds
            new BukkitRunnable() {
                @Override
                public void run() {
                    modelEngineIntegration.stopAnimation(finalTargetEntity, modelName, "rest");
                }
            }.runTaskLater(plugin, 100L); // 5 seconds later
        }
    }
    
    /**
     * Makes the woodcutter interact with a player
     * @param details The NPC details
     * @param player The player to interact with
     */
    public void handleWoodcutterInteraction(NPCManager.NPCDetails details, Player player) {
        CitizensIntegration citizensIntegration = plugin.getCitizensIntegration();
        if (citizensIntegration != null && details.getCitizensNPC() != null) {
            // Make NPC look at player
            citizensIntegration.makeNPCLookAt(details.getCitizensNPC(), player);
        }
        
        // Play greeting animation
        performWoodcuttingAnimation(details);
        
        plugin.getLogger().info("Woodcutter " + details.getName() + " interacted with player " + player.getName());
    }
}