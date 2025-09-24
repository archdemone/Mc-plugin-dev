package dev.archdemone.npcintegration.tasks;

import org.bukkit.entity.Player;

/**
 * Planting task implementation
 */
public class PlantingTask extends NPCTask {
    private int saplingsPlanted;
    private int treesGrown;
    
    public PlantingTask(Player assignedBy, TaskConfiguration config) {
        super("planting", assignedBy, config);
        this.saplingsPlanted = 0;
        this.treesGrown = 0;
    }
    
    @Override
    public boolean executeCycle() {
        if (!shouldContinue()) {
            return false;
        }
        
        progress++;
        
        // Simulate planting work
        if (progress % 30 == 0) { // Every 1.5 seconds
            saplingsPlanted++;
        }
        
        // Simulate tree growth (much slower)
        if (progress % 200 == 0 && Math.random() < 0.1) { // Every 10 seconds, 10% chance
            treesGrown++;
        }
        
        // Check if we've completed enough work for this repetition
        if (saplingsPlanted >= config.getMinAmount()) {
            repetitionsCompleted++;
            saplingsPlanted = 0; // Reset for next repetition
        }
        
        return shouldContinue();
    }
    
    @Override
    public String getStatusMessage() {
        if (repetitionsCompleted >= config.getRepetitions()) {
            return "I've finished planting! Planted " + saplingsPlanted + " saplings and helped " + treesGrown + " trees grow.";
        }
        
        return "I've planted " + saplingsPlanted + " saplings and helped " + treesGrown + " trees grow so far.";
    }
    
    @Override
    public String getCompletionMessage() {
        return "Planting complete! I planted " + saplingsPlanted + " saplings and helped " + treesGrown + " trees grow.";
    }
    
    public int getSaplingsPlanted() {
        return saplingsPlanted;
    }
    
    public int getTreesGrown() {
        return treesGrown;
    }
}