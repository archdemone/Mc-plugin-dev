package dev.archdemone.npcintegration.tasks;

import java.util.*;

/**
 * Queue system for managing multiple NPC tasks
 */
public class TaskQueue {
    private final Queue<NPCTask> tasks;
    private NPCTask currentTask;
    private final int maxQueueSize;
    
    public TaskQueue() {
        this.tasks = new LinkedList<>();
        this.currentTask = null;
        this.maxQueueSize = 10; // Maximum 10 tasks in queue
    }
    
    /**
     * Adds a task to the queue
     */
    public boolean addTask(NPCTask task) {
        if (tasks.size() >= maxQueueSize) {
            return false; // Queue is full
        }
        tasks.offer(task);
        return true;
    }
    
    /**
     * Gets the next task from the queue
     */
    public NPCTask getNextTask() {
        return tasks.poll();
    }
    
    /**
     * Peeks at the next task without removing it
     */
    public NPCTask peekNextTask() {
        return tasks.peek();
    }
    
    /**
     * Gets the current active task
     */
    public NPCTask getCurrentTask() {
        return currentTask;
    }
    
    /**
     * Sets the current active task
     */
    public void setCurrentTask(NPCTask currentTask) {
        this.currentTask = currentTask;
    }
    
    /**
     * Checks if there's an active task
     */
    public boolean hasActiveTask() {
        return currentTask != null;
    }
    
    /**
     * Checks if the queue is empty
     */
    public boolean isEmpty() {
        return tasks.isEmpty();
    }
    
    /**
     * Gets the size of the queue
     */
    public int size() {
        return tasks.size();
    }
    
    /**
     * Clears the queue
     */
    public void clear() {
        tasks.clear();
        currentTask = null;
    }
    
    /**
     * Gets all tasks in the queue (for display purposes)
     */
    public List<NPCTask> getAllTasks() {
        List<NPCTask> allTasks = new ArrayList<>();
        if (currentTask != null) {
            allTasks.add(currentTask);
        }
        allTasks.addAll(tasks);
        return allTasks;
    }
    
    /**
     * Removes a specific task from the queue
     */
    public boolean removeTask(NPCTask task) {
        return tasks.remove(task);
    }
    
    /**
     * Gets task status summary
     */
    public String getStatusSummary() {
        StringBuilder summary = new StringBuilder();
        summary.append("Current: ").append(currentTask != null ? currentTask.getType() : "None");
        summary.append(", Queued: ").append(tasks.size());
        return summary.toString();
    }
}