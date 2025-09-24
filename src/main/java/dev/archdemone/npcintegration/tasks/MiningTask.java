package dev.archdemone.npcintegration.tasks;

import org.bukkit.Material;
import org.bukkit.entity.Player;

/**
 * Mining task implementation
 */
public class MiningTask extends NPCTask {
    public int oresMined;
    public int blocksBroken;
    
    public MiningTask(Player assignedBy, TaskConfiguration config) {
        super("mining", assignedBy, config);
        this.oresMined = 0;
        this.blocksBroken = 0;
    }
    
    @Override
    public boolean executeCycle() {
        if (!shouldContinue()) {
            return false;
        }
        
        progress++;
        
        // Simulate mining work
        if (progress % 40 == 0) { // Every 2 seconds
            blocksBroken++;
            if (Math.random() < 0.3) { // 30% chance to find ore
                oresMined++;
            }
        }
        
        // Check if we've completed enough work for this repetition
        if (oresMined >= config.getMinAmount()) {
            repetitionsCompleted++;
            oresMined = 0; // Reset for next repetition
        }
        
        return shouldContinue();
    }
    
    @Override
    public String getStatusMessage() {
        if (repetitionsCompleted >= config.getRepetitions()) {
            return "I've finished mining! Found " + oresMined + " ores from " + blocksBroken + " blocks.";
        }
        
        return "I've mined " + blocksBroken + " blocks and found " + oresMined + " ores so far.";
    }
    
    @Override
    public String getCompletionMessage() {
        return "Mining complete! I found " + oresMined + " ores from " + blocksBroken + " blocks.";
    }
    
    public int getOresMined() {
        return oresMined;
    }
    
    public int getBlocksBroken() {
        return blocksBroken;
    }
}