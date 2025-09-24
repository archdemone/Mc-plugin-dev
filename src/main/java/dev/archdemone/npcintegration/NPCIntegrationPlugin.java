package dev.archdemone.npcintegration;

import dev.archdemone.npcintegration.integrations.CitizensIntegration;
import dev.archdemone.npcintegration.integrations.MythicMobsIntegration;
import dev.archdemone.npcintegration.integrations.ModelEngineIntegration;
import dev.archdemone.npcintegration.commands.NPCCommand;
import dev.archdemone.npcintegration.commands.EnhancedNPCCommand;
import dev.archdemone.npcintegration.listeners.PlayerListener;
import dev.archdemone.npcintegration.managers.NPCManager;
import dev.archdemone.npcintegration.chat.NPCChatSystem;
import dev.archdemone.npcintegration.chat.NPCTaskManager;
import dev.archdemone.npcintegration.chat.EnhancedChatSystem;
import dev.archdemone.npcintegration.tasks.EnhancedTaskManager;
import dev.archdemone.npcintegration.utils.MessageUtil;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main plugin class for NPC Integration
 * Integrates Citizens, MythicMobs, and ModelEngine for advanced NPC functionality
 */
public class NPCIntegrationPlugin extends JavaPlugin {
    
    private static NPCIntegrationPlugin instance;
    private NPCManager npcManager;
    private CitizensIntegration citizensIntegration;
    private MythicMobsIntegration mythicMobsIntegration;
    private ModelEngineIntegration modelEngineIntegration;
    private NPCChatSystem chatSystem;
    private NPCTaskManager taskManager;
    private EnhancedChatSystem enhancedChatSystem;
    private EnhancedTaskManager enhancedTaskManager;
    
    @Override
    public void onEnable() {
        instance = this;
        
        // Load configuration
        saveDefaultConfig();
        
        // Initialize message utility
        MessageUtil.init(this);
        
        // Initialize integrations
        initializeIntegrations();
        
        // Initialize managers
        npcManager = new NPCManager(this);
        
        // Initialize chat system
        chatSystem = new NPCChatSystem(this);
        taskManager = new NPCTaskManager(this, chatSystem);
        
        // Initialize enhanced systems
        enhancedTaskManager = new EnhancedTaskManager(this);
        enhancedChatSystem = new EnhancedChatSystem(this, enhancedTaskManager);
        
        // Start auto-save for task persistence
        enhancedTaskManager.startAutoSave();
        
        // Register commands
        registerCommands();
        
        // Register listeners
        registerListeners();
        
        getLogger().info("NPC Integration Plugin has been enabled!");
        getLogger().info("Integrations loaded: Citizens=" + (citizensIntegration != null) + 
                       ", MythicMobs=" + (mythicMobsIntegration != null) + 
                       ", ModelEngine=" + (modelEngineIntegration != null));
    }
    
    @Override
    public void onDisable() {
        if (npcManager != null) {
            npcManager.saveAll();
        }
        
        if (enhancedTaskManager != null) {
            enhancedTaskManager.saveAllData();
        }
        
        getLogger().info("NPC Integration Plugin has been disabled!");
    }
    
    private void initializeIntegrations() {
        // Initialize Citizens integration
        if (getServer().getPluginManager().getPlugin("Citizens") != null) {
            citizensIntegration = new CitizensIntegration(this);
            getLogger().info("Citizens integration enabled!");
        } else {
            getLogger().warning("Citizens plugin not found! Citizens integration disabled.");
        }
        
        // Initialize MythicMobs integration
        if (getServer().getPluginManager().getPlugin("MythicMobs") != null) {
            mythicMobsIntegration = new MythicMobsIntegration(this);
            getLogger().info("MythicMobs integration enabled!");
        } else {
            getLogger().warning("MythicMobs plugin not found! MythicMobs integration disabled.");
        }
        
        // Initialize ModelEngine integration
        if (getServer().getPluginManager().getPlugin("ModelEngine") != null) {
            modelEngineIntegration = new ModelEngineIntegration(this);
            getLogger().info("ModelEngine integration enabled!");
        } else {
            getLogger().warning("ModelEngine plugin not found! ModelEngine integration disabled.");
        }
    }
    
    private void registerCommands() {
        // Register enhanced command
        EnhancedNPCCommand enhancedCommand = new EnhancedNPCCommand(this);
        getCommand("npcintegration").setExecutor(enhancedCommand);
        getCommand("npcintegration").setTabCompleter(enhancedCommand);
    }
    
    private void registerListeners() {
        getServer().getPluginManager().registerEvents(new PlayerListener(this), this);
    }
    
    // Getters
    public static NPCIntegrationPlugin getInstance() {
        return instance;
    }
    
    public NPCManager getNPCManager() {
        return npcManager;
    }
    
    public CitizensIntegration getCitizensIntegration() {
        return citizensIntegration;
    }
    
    public MythicMobsIntegration getMythicMobsIntegration() {
        return mythicMobsIntegration;
    }
    
    public ModelEngineIntegration getModelEngineIntegration() {
        return modelEngineIntegration;
    }
    
    public NPCChatSystem getChatSystem() {
        return chatSystem;
    }
    
    public NPCTaskManager getTaskManager() {
        return taskManager;
    }
    
    public EnhancedChatSystem getEnhancedChatSystem() {
        return enhancedChatSystem;
    }
    
    public EnhancedTaskManager getEnhancedTaskManager() {
        return enhancedTaskManager;
    }
    
    public boolean isIntegrationEnabled(String integration) {
        switch (integration.toLowerCase()) {
            case "citizens":
                return citizensIntegration != null;
            case "mythicmobs":
                return mythicMobsIntegration != null;
            case "modelengine":
                return modelEngineIntegration != null;
            default:
                return false;
        }
    }
}