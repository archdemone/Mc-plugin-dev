package dev.archdemone.mcplugin;

import dev.archdemone.mcplugin.commands.ExampleCommand;
import dev.archdemone.mcplugin.listeners.PlayerEventListener;
import org.bukkit.plugin.java.JavaPlugin;

/**
 * Main plugin class for Minecraft Plugin Development Workspace
 * 
 * This serves as a template and example for remote plugin development
 * with ChatGPT copilot assistance.
 */
public class McPluginDevPlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        // Plugin startup logic
        getLogger().info("McPluginDev has been enabled! Ready for development.");
        
        // Register commands
        registerCommands();
        
        // Register event listeners
        registerEventListeners();
        
        // Save default config
        saveDefaultConfig();
        
        getLogger().info("Plugin initialization complete. Development workspace ready!");
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("McPluginDev has been disabled. Goodbye!");
    }
    
    /**
     * Register all plugin commands
     */
    private void registerCommands() {
        // Register the example command
        ExampleCommand exampleCommand = new ExampleCommand(this);
        getCommand("example").setExecutor(exampleCommand);
        getCommand("example").setTabCompleter(exampleCommand);
        
        getLogger().info("Commands registered successfully.");
    }
    
    /**
     * Register all event listeners
     */
    private void registerEventListeners() {
        getServer().getPluginManager().registerEvents(new PlayerEventListener(this), this);
        getLogger().info("Event listeners registered successfully.");
    }
    
    /**
     * Get the plugin instance
     * @return Plugin instance
     */
    public static McPluginDevPlugin getInstance() {
        return getPlugin(McPluginDevPlugin.class);
    }
}