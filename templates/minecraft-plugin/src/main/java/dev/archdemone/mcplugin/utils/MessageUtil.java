package dev.archdemone.mcplugin.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 * Utility class for handling messages and formatting
 * 
 * This demonstrates common utility patterns in Minecraft plugin development
 */
public class MessageUtil {
    
    // Color constants for consistent messaging
    public static final String PREFIX = ChatColor.DARK_GRAY + "[" + ChatColor.BLUE + "McPluginDev" + ChatColor.DARK_GRAY + "] ";
    public static final String SUCCESS = ChatColor.GREEN.toString();
    public static final String ERROR = ChatColor.RED.toString();
    public static final String WARNING = ChatColor.YELLOW.toString();
    public static final String INFO = ChatColor.AQUA.toString();
    
    /**
     * Send a formatted message with plugin prefix
     * @param sender The command sender to send the message to
     * @param message The message to send
     */
    public static void sendMessage(CommandSender sender, String message) {
        sender.sendMessage(PREFIX + message);
    }
    
    /**
     * Send a success message
     * @param sender The command sender to send the message to
     * @param message The success message
     */
    public static void sendSuccess(CommandSender sender, String message) {
        sendMessage(sender, SUCCESS + message);
    }
    
    /**
     * Send an error message
     * @param sender The command sender to send the message to
     * @param message The error message
     */
    public static void sendError(CommandSender sender, String message) {
        sendMessage(sender, ERROR + message);
    }
    
    /**
     * Send a warning message
     * @param sender The command sender to send the message to
     * @param message The warning message
     */
    public static void sendWarning(CommandSender sender, String message) {
        sendMessage(sender, WARNING + message);
    }
    
    /**
     * Send an info message
     * @param sender The command sender to send the message to
     * @param message The info message
     */
    public static void sendInfo(CommandSender sender, String message) {
        sendMessage(sender, INFO + message);
    }
    
    /**
     * Format a message with color codes
     * @param message The message to format
     * @return The formatted message
     */
    public static String formatMessage(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
    
    /**
     * Check if the sender is a player
     * @param sender The command sender to check
     * @return True if the sender is a player, false otherwise
     */
    public static boolean isPlayer(CommandSender sender) {
        return sender instanceof Player;
    }
    
    /**
     * Get a player from a command sender, or null if not a player
     * @param sender The command sender
     * @return The player, or null if not a player
     */
    public static Player getPlayer(CommandSender sender) {
        return isPlayer(sender) ? (Player) sender : null;
    }
    
    /**
     * Send a message only if the sender is a player
     * @param sender The command sender
     * @param message The message to send
     * @return True if the message was sent, false if sender is not a player
     */
    public static boolean sendPlayerMessage(CommandSender sender, String message) {
        if (isPlayer(sender)) {
            sendMessage(sender, message);
            return true;
        }
        return false;
    }
}