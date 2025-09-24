package dev.archdemone.mcplugin.commands;

import dev.archdemone.mcplugin.McPluginDevPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * Example command implementation for plugin development workspace
 * 
 * This demonstrates best practices for command handling in Minecraft plugins
 */
public class ExampleCommand implements CommandExecutor, TabCompleter {
    
    private final McPluginDevPlugin plugin;
    
    public ExampleCommand(McPluginDevPlugin plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        // Check if sender is a player
        if (!(sender instanceof Player)) {
            sender.sendMessage(ChatColor.RED + "This command can only be executed by players!");
            return true;
        }
        
        Player player = (Player) sender;
        
        // Check permissions
        if (!player.hasPermission("mcplugindev.example")) {
            player.sendMessage(ChatColor.RED + "You don't have permission to use this command!");
            return true;
        }
        
        // Handle different arguments
        if (args.length == 0) {
            // No arguments - show help
            showHelp(player);
            return true;
        }
        
        switch (args[0].toLowerCase()) {
            case "info":
                showInfo(player);
                break;
            case "test":
                runTest(player);
                break;
            case "config":
                showConfig(player);
                break;
            case "help":
            default:
                showHelp(player);
                break;
        }
        
        return true;
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            // First argument completions
            completions.add("info");
            completions.add("test");
            completions.add("config");
            completions.add("help");
        }
        
        // Filter completions based on what the player has typed
        List<String> filteredCompletions = new ArrayList<>();
        String partialArg = args[args.length - 1].toLowerCase();
        
        for (String completion : completions) {
            if (completion.toLowerCase().startsWith(partialArg)) {
                filteredCompletions.add(completion);
            }
        }
        
        return filteredCompletions;
    }
    
    private void showHelp(Player player) {
        player.sendMessage(ChatColor.GOLD + "=== McPluginDev Example Command ===");
        player.sendMessage(ChatColor.YELLOW + "/example info " + ChatColor.WHITE + "- Show plugin information");
        player.sendMessage(ChatColor.YELLOW + "/example test " + ChatColor.WHITE + "- Run a test function");
        player.sendMessage(ChatColor.YELLOW + "/example config " + ChatColor.WHITE + "- Show config values");
        player.sendMessage(ChatColor.YELLOW + "/example help " + ChatColor.WHITE + "- Show this help message");
    }
    
    private void showInfo(Player player) {
        player.sendMessage(ChatColor.GREEN + "Plugin: " + plugin.getDescription().getName());
        player.sendMessage(ChatColor.GREEN + "Version: " + plugin.getDescription().getVersion());
        player.sendMessage(ChatColor.GREEN + "Author: " + plugin.getDescription().getAuthors().toString());
        player.sendMessage(ChatColor.GREEN + "Description: " + plugin.getDescription().getDescription());
    }
    
    private void runTest(Player player) {
        player.sendMessage(ChatColor.BLUE + "Running test function...");
        
        // Example of accessing config
        String testValue = plugin.getConfig().getString("test-message", "Default test message");
        player.sendMessage(ChatColor.BLUE + "Test message from config: " + testValue);
        
        // Example of server interaction
        int onlinePlayers = plugin.getServer().getOnlinePlayers().size();
        player.sendMessage(ChatColor.BLUE + "Online players: " + onlinePlayers);
        
        player.sendMessage(ChatColor.GREEN + "Test completed successfully!");
    }
    
    private void showConfig(Player player) {
        player.sendMessage(ChatColor.AQUA + "=== Configuration Values ===");
        
        // Show some config values
        boolean enableFeature = plugin.getConfig().getBoolean("enable-feature", true);
        String testMessage = plugin.getConfig().getString("test-message", "Default message");
        int maxValue = plugin.getConfig().getInt("max-value", 100);
        
        player.sendMessage(ChatColor.AQUA + "Enable Feature: " + enableFeature);
        player.sendMessage(ChatColor.AQUA + "Test Message: " + testMessage);
        player.sendMessage(ChatColor.AQUA + "Max Value: " + maxValue);
    }
}