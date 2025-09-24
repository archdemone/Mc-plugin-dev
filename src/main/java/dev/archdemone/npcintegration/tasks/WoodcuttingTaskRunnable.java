package dev.archdemone.npcintegration.tasks;

import dev.archdemone.npcintegration.NPCIntegrationPlugin;
import dev.archdemone.npcintegration.managers.NPCManager;
import dev.archdemone.npcintegration.integrations.CitizensIntegration;
import dev.archdemone.npcintegration.integrations.ModelEngineIntegration;
import dev.archdemone.npcintegration.utils.MessageUtil;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Runnable for executing woodcutting tasks
 */
public class WoodcuttingTaskRunnable extends BukkitRunnable {
    private final NPCManager.NPCDetails details;
    private final WoodcuttingTask task;
    private final NPCIntegrationPlugin plugin;
    private int cycleCount;
    private Location lastTreeLocation;
    
    public WoodcuttingTaskRunnable(NPCManager.NPCDetails details, WoodcuttingTask task) {
        this.details = details;
        this.task = task;
        this.plugin = NPCIntegrationPlugin.getInstance();
        this.cycleCount = 0;
        this.lastTreeLocation = null;
    }
    
    @Override
    public void run() {
        // Check if NPC is still active
        if (!isNPCActive()) {
            cancel();
            return;
        }
        
        cycleCount++;
        
        // Execute task cycle
        if (!task.executeCycle()) {
            // Task completed
            completeTask();
            cancel();
            return;
        }
        
        // Play work effects
        if (cycleCount % task.getConfig().getSoundInterval() == 0) {
            playWorkSounds();
        }
        
        if (cycleCount % task.getConfig().getParticleInterval() == 0) {
            playWorkParticles();
        }
        
        if (cycleCount % task.getConfig().getAnimationInterval() == 0) {
            playWorkAnimation();
        }
        
        // Look for trees and move towards them
        if (cycleCount % 100 == 0) { // Every 5 seconds
            Location treeLocation = findNearbyTree();
            if (treeLocation != null && !treeLocation.equals(lastTreeLocation)) {
                moveToTree(treeLocation);
                lastTreeLocation = treeLocation;
            }
        }
        
        // Send progress updates
        if (cycleCount % 600 == 0) { // Every 30 seconds
            sendProgressUpdate();
        }
    }
    
    private boolean isNPCActive() {
        NPC npc = details.getCitizensNPC();
        return npc != null && npc.isSpawned();
    }
    
    private Location findNearbyTree() {
        NPC npc = details.getCitizensNPC();
        if (npc == null || !npc.isSpawned()) {
            return null;
        }
        
        Location npcLocation = npc.getEntity().getLocation();
        int radius = task.getConfig().getCollectionRadius();
        
        // Search for trees in radius
        for (int x = -radius; x <= radius; x++) {
            for (int z = -radius; z <= radius; z++) {
                for (int y = -5; y <= 10; y++) {
                    Location checkLocation = npcLocation.clone().add(x, y, z);
                    Block block = checkLocation.getBlock();
                    
                    if (isLogBlock(block)) {
                        // Check if there's a tree structure
                        if (hasTreeStructure(checkLocation)) {
                            return checkLocation;
                        }
                    }
                }
            }
        }
        
        return null;
    }
    
    private boolean isLogBlock(Block block) {
        String materialName = block.getType().name().toLowerCase();
        return materialName.contains("log") || materialName.contains("wood");
    }
    
    private boolean hasTreeStructure(Location baseLocation) {
        // Simple check - look for leaves above the log
        for (int y = 1; y <= 10; y++) {
            Block block = baseLocation.clone().add(0, y, 0).getBlock();
            if (block.getType().name().toLowerCase().contains("leaves")) {
                return true;
            }
        }
        return false;
    }
    
    private void moveToTree(Location treeLocation) {
        CitizensIntegration citizensIntegration = plugin.getCitizensIntegration();
        if (citizensIntegration != null && details.getCitizensNPC() != null) {
            NPC npc = details.getCitizensNPC();
            npc.getNavigator().setTarget(treeLocation);
        }
    }
    
    private void playWorkSounds() {
        NPC npc = details.getCitizensNPC();
        if (npc == null || !npc.isSpawned()) return;
        
        Location npcLocation = npc.getEntity().getLocation();
        Sound[] sounds = task.getConfig().getWorkSounds();
        
        if (sounds.length > 0) {
            Sound sound = sounds[(int) (Math.random() * sounds.length)];
            npcLocation.getWorld().playSound(npcLocation, sound, 1.0f, 1.0f);
        }
    }
    
    private void playWorkParticles() {
        NPC npc = details.getCitizensNPC();
        if (npc == null || !npc.isSpawned()) return;
        
        Location npcLocation = npc.getEntity().getLocation();
        Particle[] particles = task.getConfig().getWorkParticles();
        
        if (particles.length > 0) {
            Particle particle = particles[(int) (Math.random() * particles.length)];
            npcLocation.getWorld().spawnParticle(particle, 
                npcLocation.add(0, 1, 0), 10, 0.5, 0.5, 0.5, 0.1);
        }
    }
    
    private void playWorkAnimation() {
        ModelEngineIntegration modelEngineIntegration = plugin.getModelEngineIntegration();
        if (modelEngineIntegration != null && details.getModelName() != null) {
            Entity targetEntity = details.getMythicMobEntity();
            if (targetEntity == null && details.getCitizensNPC() != null) {
                targetEntity = details.getCitizensNPC().getEntity();
            }
            
            if (targetEntity != null) {
                final Entity finalTargetEntity = targetEntity;
                final String modelName = details.getModelName();
                modelEngineIntegration.playAnimation(finalTargetEntity, modelName, 
                    task.getConfig().getAnimationName());
                
                // Stop animation after 2 seconds
                new BukkitRunnable() {
                    @Override
                    public void run() {
                        modelEngineIntegration.stopAnimation(finalTargetEntity, modelName, 
                            task.getConfig().getAnimationName());
                    }
                }.runTaskLater(plugin, 40L);
            }
        }
    }
    
    private void sendProgressUpdate() {
        NPC npc = details.getCitizensNPC();
        if (npc == null || !npc.isSpawned()) return;
        
        String message = task.getStatusMessage();
        Location npcLocation = npc.getEntity().getLocation();
        
        // Send to nearby players
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (player.getLocation().distance(npcLocation) <= 15) {
                MessageUtil.sendInfo(player, "&6[" + details.getName() + "] &e" + message);
            }
        }
    }
    
    private void completeTask() {
        NPC npc = details.getCitizensNPC();
        if (npc == null || !npc.isSpawned()) return;
        
        // Play completion effects
        Location npcLocation = npc.getEntity().getLocation();
        
        // Completion sounds
        Sound[] sounds = task.getConfig().getCompletionSounds();
        if (sounds.length > 0) {
            Sound sound = sounds[(int) (Math.random() * sounds.length)];
            npcLocation.getWorld().playSound(npcLocation, sound, 1.0f, 1.0f);
        }
        
        // Completion particles
        Particle[] particles = task.getConfig().getCompletionParticles();
        if (particles.length > 0) {
            Particle particle = particles[(int) (Math.random() * particles.length)];
            npcLocation.getWorld().spawnParticle(particle, 
                npcLocation.add(0, 1, 0), 20, 1.0, 1.0, 1.0, 0.1);
        }
        
        // Send completion message
        String message = task.getCompletionMessage();
        for (Player player : plugin.getServer().getOnlinePlayers()) {
            if (player.getLocation().distance(npcLocation) <= 15) {
                MessageUtil.sendInfo(player, "&6[" + details.getName() + "] &a" + message);
            }
        }
        
        // Move to next task in queue
        plugin.getEnhancedTaskManager().startNextTask(details, 
            plugin.getEnhancedTaskManager().getTaskQueue(details.getCitizensId()));
    }
}