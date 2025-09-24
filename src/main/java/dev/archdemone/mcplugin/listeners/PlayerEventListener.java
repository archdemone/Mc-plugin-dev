package dev.archdemone.mcplugin.listeners;

import dev.archdemone.mcplugin.McPluginDevPlugin;
import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Example event listener for plugin development workspace
 * 
 * This demonstrates best practices for handling Minecraft events
 */
public class PlayerEventListener implements Listener {
    
    private final McPluginDevPlugin plugin;
    
    public PlayerEventListener(McPluginDevPlugin plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        // Get configuration values
        boolean enableWelcomeMessage = plugin.getConfig().getBoolean("enable-welcome-message", true);
        String welcomeMessage = plugin.getConfig().getString("welcome-message", "Welcome to the server, {player}!");
        
        if (enableWelcomeMessage) {
            // Replace placeholder with player name
            String formattedMessage = welcomeMessage.replace("{player}", event.getPlayer().getName());
            
            // Send colored welcome message
            event.getPlayer().sendMessage(ChatColor.GREEN + formattedMessage);
        }
        
        // Log the join event
        plugin.getLogger().info("Player " + event.getPlayer().getName() + " joined the server.");
        
        // Example: Check if it's the player's first time joining
        if (!event.getPlayer().hasPlayedBefore()) {
            event.getPlayer().sendMessage(ChatColor.GOLD + "Welcome to the server for the first time!");
            plugin.getLogger().info("First time player: " + event.getPlayer().getName());
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Get configuration values
        boolean enableGoodbyeMessage = plugin.getConfig().getBoolean("enable-goodbye-message", true);
        String goodbyeMessage = plugin.getConfig().getString("goodbye-message", "Goodbye, {player}!");
        
        if (enableGoodbyeMessage) {
            // Replace placeholder with player name
            String formattedMessage = goodbyeMessage.replace("{player}", event.getPlayer().getName());
            
            // Set custom quit message
            event.setQuitMessage(ChatColor.YELLOW + formattedMessage);
        }
        
        // Log the quit event
        plugin.getLogger().info("Player " + event.getPlayer().getName() + " left the server.");
    }
}