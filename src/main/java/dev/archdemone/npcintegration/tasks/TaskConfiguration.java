package dev.archdemone.npcintegration.tasks;

import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.Particle;

import java.util.*;

/**
 * Configuration class for NPC tasks
 * Defines how tasks should be executed, including timing, materials, and effects
 */
public class TaskConfiguration {
    
    // Task execution settings
    private int duration; // Duration in ticks (20 ticks = 1 second)
    private int repetitions; // How many times to repeat the task
    private int delayBetweenRepetitions; // Delay between repetitions in ticks
    private boolean canInterrupt; // Whether this task can be interrupted
    
    // Material and tool requirements
    private Material requiredTool;
    private Material[] preferredToolMaterials; // Ordered by preference (best first)
    private Map<Material, Float> toolEfficiency; // Tool material -> efficiency multiplier
    
    // Logging and collection settings
    private Material[] targetMaterials; // What materials to collect/work with
    private int minAmount; // Minimum amount to collect
    private int maxAmount; // Maximum amount to collect
    private int collectionRadius; // Radius to search for materials
    
    // Audio and visual effects
    private Sound[] workSounds; // Sounds to play during work
    private Sound[] completionSounds; // Sounds to play when task completes
    private Particle[] workParticles; // Particles to show during work
    private Particle[] completionParticles; // Particles to show on completion
    private int soundInterval; // How often to play work sounds (in ticks)
    private int particleInterval; // How often to show particles (in ticks)
    
    // Animation settings
    private String animationName; // Animation to play during task
    private int animationInterval; // How often to play animation (in ticks)
    
    public TaskConfiguration() {
        // Default values
        this.duration = 6000; // 5 minutes default
        this.repetitions = 1;
        this.delayBetweenRepetitions = 0;
        this.canInterrupt = true;
        this.requiredTool = null;
        this.preferredToolMaterials = new Material[]{Material.DIAMOND_AXE, Material.IRON_AXE, Material.STONE_AXE, Material.WOODEN_AXE};
        this.toolEfficiency = new HashMap<>();
        this.targetMaterials = new Material[0];
        this.minAmount = 1;
        this.maxAmount = 10;
        this.collectionRadius = 15;
        this.workSounds = new Sound[]{Sound.ENTITY_PLAYER_ATTACK_SWEEP};
        this.completionSounds = new Sound[]{Sound.ENTITY_PLAYER_LEVELUP};
        this.workParticles = new Particle[]{Particle.BLOCK_CRACK};
        this.completionParticles = new Particle[]{Particle.VILLAGER_HAPPY};
        this.soundInterval = 40; // Every 2 seconds
        this.particleInterval = 10; // Every 0.5 seconds
        this.animationName = "working";
        this.animationInterval = 20; // Every second
        
        // Set up default tool efficiency
        setupDefaultToolEfficiency();
    }
    
    private void setupDefaultToolEfficiency() {
        toolEfficiency.put(Material.DIAMOND_AXE, 2.0f);
        toolEfficiency.put(Material.IRON_AXE, 1.5f);
        toolEfficiency.put(Material.STONE_AXE, 1.2f);
        toolEfficiency.put(Material.WOODEN_AXE, 1.0f);
        toolEfficiency.put(Material.GOLDEN_AXE, 1.8f);
        toolEfficiency.put(Material.NETHERITE_AXE, 2.5f);
    }
    
    // Getters and Setters
    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }
    
    public int getRepetitions() { return repetitions; }
    public void setRepetitions(int repetitions) { this.repetitions = repetitions; }
    
    public int getDelayBetweenRepetitions() { return delayBetweenRepetitions; }
    public void setDelayBetweenRepetitions(int delayBetweenRepetitions) { this.delayBetweenRepetitions = delayBetweenRepetitions; }
    
    public boolean canInterrupt() { return canInterrupt; }
    public void setCanInterrupt(boolean canInterrupt) { this.canInterrupt = canInterrupt; }
    
    public Material getRequiredTool() { return requiredTool; }
    public void setRequiredTool(Material requiredTool) { this.requiredTool = requiredTool; }
    
    public Material[] getPreferredToolMaterials() { return preferredToolMaterials; }
    public void setPreferredToolMaterials(Material[] preferredToolMaterials) { this.preferredToolMaterials = preferredToolMaterials; }
    
    public Map<Material, Float> getToolEfficiency() { return toolEfficiency; }
    public void setToolEfficiency(Map<Material, Float> toolEfficiency) { this.toolEfficiency = toolEfficiency; }
    
    public Material[] getTargetMaterials() { return targetMaterials; }
    public void setTargetMaterials(Material[] targetMaterials) { this.targetMaterials = targetMaterials; }
    
    public int getMinAmount() { return minAmount; }
    public void setMinAmount(int minAmount) { this.minAmount = minAmount; }
    
    public int getMaxAmount() { return maxAmount; }
    public void setMaxAmount(int maxAmount) { this.maxAmount = maxAmount; }
    
    public int getCollectionRadius() { return collectionRadius; }
    public void setCollectionRadius(int collectionRadius) { this.collectionRadius = collectionRadius; }
    
    public Sound[] getWorkSounds() { return workSounds; }
    public void setWorkSounds(Sound[] workSounds) { this.workSounds = workSounds; }
    
    public Sound[] getCompletionSounds() { return completionSounds; }
    public void setCompletionSounds(Sound[] completionSounds) { this.completionSounds = completionSounds; }
    
    public Particle[] getWorkParticles() { return workParticles; }
    public void setWorkParticles(Particle[] workParticles) { this.workParticles = workParticles; }
    
    public Particle[] getCompletionParticles() { return completionParticles; }
    public void setCompletionParticles(Particle[] completionParticles) { this.completionParticles = completionParticles; }
    
    public int getSoundInterval() { return soundInterval; }
    public void setSoundInterval(int soundInterval) { this.soundInterval = soundInterval; }
    
    public int getParticleInterval() { return particleInterval; }
    public void setParticleInterval(int particleInterval) { this.particleInterval = particleInterval; }
    
    public String getAnimationName() { return animationName; }
    public void setAnimationName(String animationName) { this.animationName = animationName; }
    
    public int getAnimationInterval() { return animationInterval; }
    public void setAnimationInterval(int animationInterval) { this.animationInterval = animationInterval; }
    
    /**
     * Gets the efficiency multiplier for a specific tool material
     */
    public float getToolEfficiency(Material toolMaterial) {
        return toolEfficiency.getOrDefault(toolMaterial, 1.0f);
    }
    
    /**
     * Creates a woodcutting task configuration
     */
    public static TaskConfiguration createWoodcuttingConfig() {
        TaskConfiguration config = new TaskConfiguration();
        config.setDuration(6000); // 5 minutes
        config.setRepetitions(3);
        config.setDelayBetweenRepetitions(200); // 10 seconds between repetitions
        config.setRequiredTool(Material.WOODEN_AXE);
        config.setTargetMaterials(new Material[]{
            Material.OAK_LOG, Material.BIRCH_LOG, Material.SPRUCE_LOG, 
            Material.JUNGLE_LOG, Material.ACACIA_LOG, Material.DARK_OAK_LOG,
            Material.MANGROVE_LOG, Material.CHERRY_LOG
        });
        config.setMinAmount(5);
        config.setMaxAmount(20);
        config.setCollectionRadius(20);
        config.setWorkSounds(new Sound[]{Sound.ENTITY_PLAYER_ATTACK_SWEEP, Sound.BLOCK_WOOD_HIT});
        config.setCompletionSounds(new Sound[]{Sound.ENTITY_PLAYER_LEVELUP, Sound.ENTITY_VILLAGER_YES});
        config.setWorkParticles(new Particle[]{Particle.BLOCK_CRACK, Particle.SWEEP_ATTACK});
        config.setCompletionParticles(new Particle[]{Particle.VILLAGER_HAPPY, Particle.BLOCK_CRACK});
        config.setSoundInterval(30);
        config.setParticleInterval(8);
        config.setAnimationName("woodcutting");
        config.setAnimationInterval(15);
        return config;
    }
    
    /**
     * Creates a mining task configuration
     */
    public static TaskConfiguration createMiningConfig() {
        TaskConfiguration config = new TaskConfiguration();
        config.setDuration(8000); // 6.67 minutes
        config.setRepetitions(2);
        config.setDelayBetweenRepetitions(300); // 15 seconds between repetitions
        config.setRequiredTool(Material.WOODEN_PICKAXE);
        config.setPreferredToolMaterials(new Material[]{
            Material.NETHERITE_PICKAXE, Material.DIAMOND_PICKAXE, Material.IRON_PICKAXE, 
            Material.STONE_PICKAXE, Material.WOODEN_PICKAXE
        });
        config.setTargetMaterials(new Material[]{
            Material.COAL_ORE, Material.IRON_ORE, Material.GOLD_ORE, Material.REDSTONE_ORE,
            Material.LAPIS_ORE, Material.DIAMOND_ORE, Material.EMERALD_ORE
        });
        config.setMinAmount(3);
        config.setMaxAmount(15);
        config.setCollectionRadius(25);
        config.setWorkSounds(new Sound[]{Sound.ENTITY_PLAYER_ATTACK_SWEEP, Sound.BLOCK_STONE_HIT});
        config.setCompletionSounds(new Sound[]{Sound.ENTITY_PLAYER_LEVELUP, Sound.ENTITY_VILLAGER_YES});
        config.setWorkParticles(new Particle[]{Particle.BLOCK_CRACK, Particle.SWEEP_ATTACK});
        config.setCompletionParticles(new Particle[]{Particle.VILLAGER_HAPPY, Particle.BLOCK_CRACK});
        config.setSoundInterval(35);
        config.setParticleInterval(12);
        config.setAnimationName("mining");
        config.setAnimationInterval(18);
        return config;
    }
    
    /**
     * Creates a farming task configuration
     */
    public static TaskConfiguration createFarmingConfig() {
        TaskConfiguration config = new TaskConfiguration();
        config.setDuration(4000); // 3.33 minutes
        config.setRepetitions(5);
        config.setDelayBetweenRepetitions(100); // 5 seconds between repetitions
        config.setRequiredTool(null); // No tool required for basic farming
        config.setTargetMaterials(new Material[]{
            Material.WHEAT, Material.CARROTS, Material.POTATOES, Material.BEETROOTS
        });
        config.setMinAmount(10);
        config.setMaxAmount(30);
        config.setCollectionRadius(12);
        config.setWorkSounds(new Sound[]{Sound.BLOCK_CROP_BREAK, Sound.ENTITY_VILLAGER_WORK_FARMER});
        config.setCompletionSounds(new Sound[]{Sound.ENTITY_PLAYER_LEVELUP, Sound.ENTITY_VILLAGER_YES});
        config.setWorkParticles(new Particle[]{Particle.BLOCK_CRACK, Particle.COMPOSTER});
        config.setCompletionParticles(new Particle[]{Particle.VILLAGER_HAPPY, Particle.BLOCK_CRACK});
        config.setSoundInterval(25);
        config.setParticleInterval(6);
        config.setAnimationName("farming");
        config.setAnimationInterval(12);
        return config;
    }
}