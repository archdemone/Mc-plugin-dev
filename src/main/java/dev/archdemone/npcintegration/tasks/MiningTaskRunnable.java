package dev.archdemone.npcintegration.tasks;

import dev.archdemone.npcintegration.NPCIntegrationPlugin;
import dev.archdemone.npcintegration.managers.NPCManager;
import org.bukkit.*;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Runnable for executing mining tasks
 */
public class MiningTaskRunnable extends BukkitRunnable {
    private final NPCManager.NPCDetails details;
    private final MiningTask task;
    private final NPCIntegrationPlugin plugin;
    private int cycleCount;
    
    public MiningTaskRunnable(NPCManager.NPCDetails details, MiningTask task) {
        this.details = details;
        this.task = task;
        this.plugin = NPCIntegrationPlugin.getInstance();
        this.cycleCount = 0;
    }
    
    @Override
    public void run() {
        if (!isNPCActive()) {
            cancel();
            return;
        }
        
        cycleCount++;
        
        if (!task.executeCycle()) {
            completeTask();
            cancel();
            return;
        }
        
        // Play effects
        if (cycleCount % task.getConfig().getSoundInterval() == 0) {
            playWorkSounds();
        }
        
        if (cycleCount % task.getConfig().getParticleInterval() == 0) {
            playWorkParticles();
        }
        
        if (cycleCount % 600 == 0) {
            sendProgressUpdate();
        }
    }
    
    private boolean isNPCActive() {
        return details.getCitizensNPC() != null && details.getCitizensNPC().isSpawned();
    }
    
    private void playWorkSounds() {
        // Implementation similar to WoodcuttingTaskRunnable
    }
    
    private void playWorkParticles() {
        // Implementation similar to WoodcuttingTaskRunnable
    }
    
    private void sendProgressUpdate() {
        // Implementation similar to WoodcuttingTaskRunnable
    }
    
    private void completeTask() {
        // Implementation similar to WoodcuttingTaskRunnable
    }
}