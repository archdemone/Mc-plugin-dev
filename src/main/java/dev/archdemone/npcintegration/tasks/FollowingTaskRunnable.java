package dev.archdemone.npcintegration.tasks;

import dev.archdemone.npcintegration.NPCIntegrationPlugin;
import dev.archdemone.npcintegration.managers.NPCManager;
import dev.archdemone.npcintegration.integrations.CitizensIntegration;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

/**
 * Runnable for executing following tasks
 */
public class FollowingTaskRunnable extends BukkitRunnable {
    private final NPCManager.NPCDetails details;
    private final FollowingTask task;
    private final NPCIntegrationPlugin plugin;
    private int cycleCount;
    
    public FollowingTaskRunnable(NPCManager.NPCDetails details, FollowingTask task) {
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
        
        // Make NPC follow the player
        if (cycleCount % 40 == 0) { // Every 2 seconds
            followPlayer();
        }
        
        if (cycleCount % 600 == 0) {
            sendProgressUpdate();
        }
    }
    
    private boolean isNPCActive() {
        return details.getCitizensNPC() != null && details.getCitizensNPC().isSpawned();
    }
    
    private void followPlayer() {
        Player targetPlayer = task.getAssignedBy();
        if (targetPlayer == null || !targetPlayer.isOnline()) {
            task.stopFollowing();
            return;
        }
        
        CitizensIntegration citizensIntegration = plugin.getCitizensIntegration();
        if (citizensIntegration != null && details.getCitizensNPC() != null) {
            citizensIntegration.makeNPCFollow(details.getCitizensNPC(), targetPlayer);
        }
    }
    
    private void sendProgressUpdate() {
        // Implementation similar to WoodcuttingTaskRunnable
    }
    
    private void completeTask() {
        // Implementation similar to WoodcuttingTaskRunnable
    }
}