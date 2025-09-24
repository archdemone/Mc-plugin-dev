package dev.archdemone.npcintegration.utils;

import dev.archdemone.npcintegration.NPCIntegrationPlugin;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

/**
 * Utility class for handling messages and chat formatting
 */
public class MessageUtil {
    
    private static NPCIntegrationPlugin plugin;
    private static String prefix;
    
    public static void init(NPCIntegrationPlugin pluginInstance) {
        plugin = pluginInstance;
        prefix = ChatColor.translateAlternateColorCodes('&', 
            plugin.getConfig().getString("messages.prefix", "&8[&6NPC Integration&8] "));
    }
    
    public static void sendMessage(CommandSender sender, String message) {
        String formattedMessage = ChatColor.translateAlternateColorCodes('&', prefix + message);
        sender.sendMessage(formattedMessage);
    }
    
    public static void sendError(CommandSender sender, String message) {
        String formattedMessage = ChatColor.translateAlternateColorCodes('&', prefix + "&c" + message);
        sender.sendMessage(formattedMessage);
    }
    
    public static void sendSuccess(CommandSender sender, String message) {
        String formattedMessage = ChatColor.translateAlternateColorCodes('&', prefix + "&a" + message);
        sender.sendMessage(formattedMessage);
    }
    
    public static void sendWarning(CommandSender sender, String message) {
        String formattedMessage = ChatColor.translateAlternateColorCodes('&', prefix + "&e" + message);
        sender.sendMessage(formattedMessage);
    }
    
    public static void sendInfo(CommandSender sender, String message) {
        String formattedMessage = ChatColor.translateAlternateColorCodes('&', prefix + "&b" + message);
        sender.sendMessage(formattedMessage);
    }
    
    public static String getConfigMessage(String path) {
        return plugin.getConfig().getString("messages." + path, "Message not found: " + path);
    }
    
    public static String colorize(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    
    public static String stripColor(String message) {
        return ChatColor.stripColor(message);
    }
}