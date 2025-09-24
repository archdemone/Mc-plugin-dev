package dev.archdemone.npcintegration.tasks;

import org.bukkit.entity.Player;

/**
 * Farming task implementation
 */
public class FarmingTask extends NPCTask {
    private int cropsHarvested;
    private int cropsPlanted;
    
    public FarmingTask(Player assignedBy, TaskConfiguration config) {
        super("farming", assignedBy, config);
        this.cropsHarvested = 0;
        this.cropsPlanted = 0;
    }
    
    @Override
    public boolean executeCycle() {
        if (!shouldContinue()) {
            return false;
        }
        
        progress++;
        
        // Simulate farming work
        if (progress % 25 == 0) { // Every 1.25 seconds
            cropsHarvested += (int) (Math.random() * 2) + 1; // 1-2 crops per cycle
        }
        
        if (progress % 50 == 0) { // Every 2.5 seconds
            cropsPlanted += (int) (Math.random() * 3) + 1; // 1-3 crops per cycle
        }
        
        // Check if we've completed enough work for this repetition
        if (cropsHarvested >= config.getMinAmount()) {
            repetitionsCompleted++;
            cropsHarvested = 0; // Reset for next repetition
        }
        
        return shouldContinue();
    }
    
    @Override
    public String getStatusMessage() {
        if (repetitionsCompleted >= config.getRepetitions()) {
            return "I've finished farming! Harvested " + cropsHarvested + " crops and planted " + cropsPlanted + " new ones.";
        }
        
        return "I've harvested " + cropsHarvested + " crops and planted " + cropsPlanted + " new ones so far.";
    }
    
    @Override
    public String getCompletionMessage() {
        return "Farming complete! I harvested " + cropsHarvested + " crops and planted " + cropsPlanted + " new ones.";
    }
    
    public int getCropsHarvested() {
        return cropsHarvested;
    }
    
    public int getCropsPlanted() {
        return cropsPlanted;
    }
}