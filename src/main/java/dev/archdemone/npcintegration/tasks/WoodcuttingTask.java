package dev.archdemone.npcintegration.tasks;

import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Woodcutting task implementation
 */
public class WoodcuttingTask extends NPCTask {
    public int treesChopped;
    public int logsCollected;
    
    public WoodcuttingTask(Player assignedBy, TaskConfiguration config) {
        super("woodcutting", assignedBy, config);
        this.treesChopped = 0;
        this.logsCollected = 0;
    }
    
    @Override
    public boolean executeCycle() {
        if (!shouldContinue()) {
            return false;
        }
        
        progress++;
        
        // Simulate woodcutting work
        if (progress % 30 == 0) { // Every 1.5 seconds
            treesChopped++;
            logsCollected += (int) (Math.random() * 3) + 2; // 2-4 logs per tree
        }
        
        // Check if we've completed enough work for this repetition
        if (logsCollected >= config.getMinAmount()) {
            repetitionsCompleted++;
            logsCollected = 0; // Reset for next repetition
        }
        
        return shouldContinue();
    }
    
    @Override
    public String getStatusMessage() {
        if (repetitionsCompleted >= config.getRepetitions()) {
            return "I've finished woodcutting! Chopped " + treesChopped + " trees total.";
        }
        
        return "I've chopped " + treesChopped + " trees and collected " + logsCollected + " logs so far.";
    }
    
    @Override
    public String getCompletionMessage() {
        return "Woodcutting complete! I chopped " + treesChopped + " trees in total.";
    }
    
    public int getTreesChopped() {
        return treesChopped;
    }
    
    public int getLogsCollected() {
        return logsCollected;
    }
}