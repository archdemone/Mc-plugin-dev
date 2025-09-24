package dev.archdemone.npcintegration.tasks;

import org.bukkit.entity.Player;

/**
 * Following task implementation
 */
public class FollowingTask extends NPCTask {
    private int stepsTaken;
    private boolean isFollowing;
    
    public FollowingTask(Player assignedBy, TaskConfiguration config) {
        super("following", assignedBy, config);
        this.stepsTaken = 0;
        this.isFollowing = true;
    }
    
    @Override
    public boolean executeCycle() {
        if (!shouldContinue()) {
            return false;
        }
        
        progress++;
        
        // Simulate following behavior
        if (progress % 20 == 0) { // Every second
            if (isFollowing) {
                stepsTaken++;
            }
        }
        
        // Following tasks don't have repetitions, they run until stopped
        // Check if we should stop (player too far, etc.)
        if (progress >= config.getDuration()) {
            isFollowing = false;
            return false;
        }
        
        return shouldContinue();
    }
    
    @Override
    public String getStatusMessage() {
        if (isFollowing) {
            return "I'm following you! I've taken " + stepsTaken + " steps so far.";
        } else {
            return "I've stopped following. I took " + stepsTaken + " steps total.";
        }
    }
    
    @Override
    public String getCompletionMessage() {
        return "I've finished following you. I took " + stepsTaken + " steps total.";
    }
    
    public int getStepsTaken() {
        return stepsTaken;
    }
    
    public boolean isFollowing() {
        return isFollowing;
    }
    
    public void stopFollowing() {
        this.isFollowing = false;
    }
}