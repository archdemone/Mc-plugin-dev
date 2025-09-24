package dev.archdemone.npcintegration.tasks;

import org.bukkit.entity.Player;

/**
 * Smithing task implementation
 */
public class SmithingTask extends NPCTask {
    private int itemsCrafted;
    private int itemsRequested;
    
    public SmithingTask(Player assignedBy, TaskConfiguration config) {
        super("smithing", assignedBy, config);
        this.itemsCrafted = 0;
        this.itemsRequested = 0;
    }
    
    @Override
    public boolean executeCycle() {
        if (!shouldContinue()) {
            return false;
        }
        
        progress++;
        
        // Simulate smithing work
        if (progress % 45 == 0) { // Every 2.25 seconds
            if (itemsRequested > 0) {
                itemsCrafted++;
                itemsRequested--;
            }
        }
        
        // Check if we've completed enough work for this repetition
        if (itemsCrafted >= config.getMinAmount()) {
            repetitionsCompleted++;
            itemsCrafted = 0; // Reset for next repetition
        }
        
        return shouldContinue();
    }
    
    @Override
    public String getStatusMessage() {
        if (repetitionsCompleted >= config.getRepetitions()) {
            return "I've finished smithing! Crafted " + itemsCrafted + " items total.";
        }
        
        return "I've crafted " + itemsCrafted + " items. " + itemsRequested + " items requested.";
    }
    
    @Override
    public String getCompletionMessage() {
        return "Smithing complete! I crafted " + itemsCrafted + " items.";
    }
    
    public int getItemsCrafted() {
        return itemsCrafted;
    }
    
    public int getItemsRequested() {
        return itemsRequested;
    }
    
    public void requestItem() {
        this.itemsRequested++;
    }
}