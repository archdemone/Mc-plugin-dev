package dev.archdemone.npcintegration.tasks;

import org.bukkit.entity.Player;

/**
 * Repairing task implementation
 */
public class RepairingTask extends NPCTask {
    private int itemsRepaired;
    private int itemsWaiting;
    
    public RepairingTask(Player assignedBy, TaskConfiguration config) {
        super("repairing", assignedBy, config);
        this.itemsRepaired = 0;
        this.itemsWaiting = 0;
    }
    
    @Override
    public boolean executeCycle() {
        if (!shouldContinue()) {
            return false;
        }
        
        progress++;
        
        // Simulate repairing work
        if (progress % 60 == 0) { // Every 3 seconds
            if (itemsWaiting > 0) {
                itemsRepaired++;
                itemsWaiting--;
            }
        }
        
        // Check if we've completed enough work for this repetition
        if (itemsRepaired >= config.getMinAmount()) {
            repetitionsCompleted++;
            itemsRepaired = 0; // Reset for next repetition
        }
        
        return shouldContinue();
    }
    
    @Override
    public String getStatusMessage() {
        if (repetitionsCompleted >= config.getRepetitions()) {
            return "I've finished repairing! Fixed " + itemsRepaired + " items total.";
        }
        
        return "I've repaired " + itemsRepaired + " items. " + itemsWaiting + " items waiting for repair.";
    }
    
    @Override
    public String getCompletionMessage() {
        return "Repairing complete! I fixed " + itemsRepaired + " items.";
    }
    
    public int getItemsRepaired() {
        return itemsRepaired;
    }
    
    public int getItemsWaiting() {
        return itemsWaiting;
    }
    
    public void addItemToRepair() {
        this.itemsWaiting++;
    }
}