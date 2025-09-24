package dev.archdemone.npcintegration.tasks;

import dev.archdemone.npcintegration.NPCIntegrationPlugin;
import dev.archdemone.npcintegration.managers.NPCManager;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Runnable for executing planting tasks
 */
public class PlantingTaskRunnable extends BukkitRunnable {
    private final NPCManager.NPCDetails details;
    private final PlantingTask task;
    private final NPCIntegrationPlugin plugin;
    private int cycleCount;
    
    public PlantingTaskRunnable(NPCManager.NPCDetails details, PlantingTask task) {
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
        
        if (cycleCount % 600 == 0) {
            sendProgressUpdate();
        }
    }
    
    private boolean isNPCActive() {
        return details.getCitizensNPC() != null && details.getCitizensNPC().isSpawned();
    }
    
    private void sendProgressUpdate() {
        // Implementation similar to WoodcuttingTaskRunnable
    }
    
    private void completeTask() {
        // Implementation similar to WoodcuttingTaskRunnable
    }
}