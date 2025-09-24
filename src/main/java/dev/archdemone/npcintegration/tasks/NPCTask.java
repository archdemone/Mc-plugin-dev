package dev.archdemone.npcintegration.tasks;

import org.bukkit.entity.Player;

/**
 * Base class for all NPC tasks
 */
public abstract class NPCTask {
    protected final String taskType;
    protected final Player assignedBy;
    protected final TaskConfiguration config;
    protected final long startTime;
    protected int progress;
    protected int repetitionsCompleted;
    protected boolean isCompleted;
    protected boolean isPaused;
    
    public NPCTask(String taskType, Player assignedBy, TaskConfiguration config) {
        this.taskType = taskType;
        this.assignedBy = assignedBy;
        this.config = config;
        this.startTime = System.currentTimeMillis();
        this.progress = 0;
        this.repetitionsCompleted = 0;
        this.isCompleted = false;
        this.isPaused = false;
    }
    
    /**
     * Executes one cycle of the task
     * @return true if task should continue, false if completed
     */
    public abstract boolean executeCycle();
    
    /**
     * Gets the current progress as a percentage (0-100)
     */
    public int getProgressPercentage() {
        int totalCycles = config.getDuration();
        return Math.min(100, (progress * 100) / totalCycles);
    }
    
    /**
     * Gets the time elapsed in milliseconds
     */
    public long getTimeElapsed() {
        return System.currentTimeMillis() - startTime;
    }
    
    /**
     * Gets the time elapsed in ticks
     */
    public int getTicksElapsed() {
        return progress;
    }
    
    /**
     * Checks if the task should continue
     */
    public boolean shouldContinue() {
        if (isCompleted || isPaused) {
            return false;
        }
        
        // Check if we've completed all repetitions
        if (repetitionsCompleted >= config.getRepetitions()) {
            return false;
        }
        
        // Check if we've reached the duration limit
        if (progress >= config.getDuration()) {
            return false;
        }
        
        return true;
    }
    
    /**
     * Marks the task as completed
     */
    public void complete() {
        this.isCompleted = true;
    }
    
    /**
     * Pauses the task
     */
    public void pause() {
        this.isPaused = true;
    }
    
    /**
     * Resumes the task
     */
    public void resume() {
        this.isPaused = false;
    }
    
    /**
     * Gets a status message for the task
     */
    public abstract String getStatusMessage();
    
    /**
     * Gets a completion message for the task
     */
    public abstract String getCompletionMessage();
    
    // Getters
    public String getType() { return taskType; }
    public Player getAssignedBy() { return assignedBy; }
    public TaskConfiguration getConfig() { return config; }
    public long getStartTime() { return startTime; }
    public int getProgress() { return progress; }
    public int getRepetitionsCompleted() { return repetitionsCompleted; }
    public boolean isCompleted() { return isCompleted; }
    public boolean isPaused() { return isPaused; }
}