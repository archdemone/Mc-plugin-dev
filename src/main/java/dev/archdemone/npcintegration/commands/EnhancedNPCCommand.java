package dev.archdemone.npcintegration.commands;

import dev.archdemone.npcintegration.NPCIntegrationPlugin;
import dev.archdemone.npcintegration.managers.NPCManager;
import dev.archdemone.npcintegration.tasks.EnhancedTaskManager;
import dev.archdemone.npcintegration.tasks.TaskQueue;
import dev.archdemone.npcintegration.tasks.NPCTask;
import dev.archdemone.npcintegration.utils.MessageUtil;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.*;

/**
 * Enhanced command system for managing NPC tasks and features
 */
public class EnhancedNPCCommand implements CommandExecutor, TabCompleter {
    
    private final NPCIntegrationPlugin plugin;
    private final NPCManager npcManager;
    private final EnhancedTaskManager taskManager;
    
    public EnhancedNPCCommand(NPCIntegrationPlugin plugin) {
        this.plugin = plugin;
        this.npcManager = plugin.getNPCManager();
        this.taskManager = plugin.getEnhancedTaskManager();
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            MessageUtil.sendError(sender, "This command can only be used by players!");
            return true;
        }
        
        Player player = (Player) sender;
        
        if (args.length == 0) {
            sendHelpMessage(player);
            return true;
        }
        
        String subCommand = args[0].toLowerCase();
        
        switch (subCommand) {
            case "help":
                sendHelpMessage(player);
                break;
                
            case "status":
                handleStatusCommand(player, args);
                break;
                
            case "tasks":
                handleTasksCommand(player, args);
                break;
                
            case "give":
                handleGiveCommand(player, args);
                break;
                
            case "clear":
                handleClearCommand(player, args);
                break;
                
            case "reload":
                handleReloadCommand(player);
                break;
                
            case "save":
                handleSaveCommand(player);
                break;
                
            case "list":
                handleListCommand(player);
                break;
                
            default:
                MessageUtil.sendError(player, "Unknown subcommand. Use /npcintegration help for available commands.");
                break;
        }
        
        return true;
    }
    
    private void sendHelpMessage(Player player) {
        MessageUtil.sendInfo(player, "&6=== NPC Integration Enhanced Commands ===");
        MessageUtil.sendInfo(player, "&e/npcintegration help &7- Show this help message");
        MessageUtil.sendInfo(player, "&e/npcintegration status [npc] &7- Show NPC task status");
        MessageUtil.sendInfo(player, "&e/npcintegration tasks [npc] &7- List all tasks for an NPC");
        MessageUtil.sendInfo(player, "&e/npcintegration give <npc> <tool> &7- Give a tool to an NPC");
        MessageUtil.sendInfo(player, "&e/npcintegration clear [npc] &7- Clear all tasks for an NPC");
        MessageUtil.sendInfo(player, "&e/npcintegration reload &7- Reload the plugin");
        MessageUtil.sendInfo(player, "&e/npcintegration save &7- Save all data");
        MessageUtil.sendInfo(player, "&e/npcintegration list &7- List all NPCs");
        MessageUtil.sendInfo(player, "&7");
        MessageUtil.sendInfo(player, "&7Chat with NPCs using natural language:");
        MessageUtil.sendInfo(player, "&7- 'Can you chop some wood for me?'");
        MessageUtil.sendInfo(player, "&7- 'I need you to mine some ore'");
        MessageUtil.sendInfo(player, "&7- 'Please plant some saplings'");
        MessageUtil.sendInfo(player, "&7- 'Follow me' or 'Stop following'");
        MessageUtil.sendInfo(player, "&7- 'Give me an axe' (when near NPC)");
    }
    
    private void handleStatusCommand(Player player, String[] args) {
        if (args.length < 2) {
            MessageUtil.sendError(player, "Usage: /npcintegration status <npc_id>");
            return;
        }
        
        try {
            int npcId = Integer.parseInt(args[1]);
            NPCManager.NPCDetails details = npcManager.getNPCDetails(npcId);
            
            if (details == null) {
                MessageUtil.sendError(player, "NPC with ID " + npcId + " not found!");
                return;
            }
            
            TaskQueue queue = taskManager.getTaskQueue(npcId);
            if (queue == null || queue.isEmpty()) {
                MessageUtil.sendInfo(player, "&6[" + details.getName() + "] &7has no active tasks.");
                return;
            }
            
            MessageUtil.sendInfo(player, "&6=== " + details.getName() + " Status ===");
            MessageUtil.sendInfo(player, "&eType: &7" + details.getType());
            MessageUtil.sendInfo(player, "&eQueue Status: &7" + queue.getStatusSummary());
            
            if (queue.hasActiveTask()) {
                NPCTask currentTask = queue.getCurrentTask();
                MessageUtil.sendInfo(player, "&eCurrent Task: &7" + currentTask.getType());
                MessageUtil.sendInfo(player, "&eProgress: &7" + currentTask.getProgressPercentage() + "%");
                MessageUtil.sendInfo(player, "&eStatus: &7" + currentTask.getStatusMessage());
            }
            
            if (!queue.isEmpty()) {
                MessageUtil.sendInfo(player, "&eQueued Tasks: &7" + queue.size());
            }
            
        } catch (NumberFormatException e) {
            MessageUtil.sendError(player, "Invalid NPC ID. Must be a number.");
        }
    }
    
    private void handleTasksCommand(Player player, String[] args) {
        if (args.length < 2) {
            MessageUtil.sendError(player, "Usage: /npcintegration tasks <npc_id>");
            return;
        }
        
        try {
            int npcId = Integer.parseInt(args[1]);
            NPCManager.NPCDetails details = npcManager.getNPCDetails(npcId);
            
            if (details == null) {
                MessageUtil.sendError(player, "NPC with ID " + npcId + " not found!");
                return;
            }
            
            TaskQueue queue = taskManager.getTaskQueue(npcId);
            if (queue == null || queue.isEmpty()) {
                MessageUtil.sendInfo(player, "&6[" + details.getName() + "] &7has no tasks.");
                return;
            }
            
            MessageUtil.sendInfo(player, "&6=== " + details.getName() + " Tasks ===");
            
            List<NPCTask> allTasks = queue.getAllTasks();
            for (int i = 0; i < allTasks.size(); i++) {
                NPCTask task = allTasks.get(i);
                String status = (task == queue.getCurrentTask()) ? "&a[ACTIVE]" : "&7[QUEUED]";
                MessageUtil.sendInfo(player, "&e" + (i + 1) + ". &7" + task.getType() + " " + status);
                MessageUtil.sendInfo(player, "&7   Progress: " + task.getProgressPercentage() + "%");
                MessageUtil.sendInfo(player, "&7   Status: " + task.getStatusMessage());
            }
            
        } catch (NumberFormatException e) {
            MessageUtil.sendError(player, "Invalid NPC ID. Must be a number.");
        }
    }
    
    private void handleGiveCommand(Player player, String[] args) {
        if (args.length < 3) {
            MessageUtil.sendError(player, "Usage: /npcintegration give <npc_id> <tool_type>");
            return;
        }
        
        try {
            int npcId = Integer.parseInt(args[1]);
            NPCManager.NPCDetails details = npcManager.getNPCDetails(npcId);
            
            if (details == null) {
                MessageUtil.sendError(player, "NPC with ID " + npcId + " not found!");
                return;
            }
            
            String toolType = args[2].toUpperCase();
            org.bukkit.Material toolMaterial;
            
            try {
                toolMaterial = org.bukkit.Material.valueOf(toolType);
            } catch (IllegalArgumentException e) {
                MessageUtil.sendError(player, "Invalid tool type: " + toolType);
                return;
            }
            
            if (taskManager.giveToolToNPC(details, player, toolMaterial)) {
                MessageUtil.sendSuccess(player, "Successfully gave " + toolType + " to " + details.getName() + "!");
            }
            
        } catch (NumberFormatException e) {
            MessageUtil.sendError(player, "Invalid NPC ID. Must be a number.");
        }
    }
    
    private void handleClearCommand(Player player, String[] args) {
        if (args.length < 2) {
            MessageUtil.sendError(player, "Usage: /npcintegration clear <npc_id>");
            return;
        }
        
        try {
            int npcId = Integer.parseInt(args[1]);
            NPCManager.NPCDetails details = npcManager.getNPCDetails(npcId);
            
            if (details == null) {
                MessageUtil.sendError(player, "NPC with ID " + npcId + " not found!");
                return;
            }
            
            taskManager.clearAllTasks(details);
            MessageUtil.sendSuccess(player, "Cleared all tasks for " + details.getName() + "!");
            
        } catch (NumberFormatException e) {
            MessageUtil.sendError(player, "Invalid NPC ID. Must be a number.");
        }
    }
    
    private void handleReloadCommand(Player player) {
        if (!player.hasPermission("npcintegration.admin")) {
            MessageUtil.sendError(player, "You don't have permission to reload the plugin!");
            return;
        }
        
        plugin.reloadConfig();
        MessageUtil.sendSuccess(player, "Plugin configuration reloaded!");
    }
    
    private void handleSaveCommand(Player player) {
        if (!player.hasPermission("npcintegration.admin")) {
            MessageUtil.sendError(player, "You don't have permission to save data!");
            return;
        }
        
        taskManager.saveAllData();
        MessageUtil.sendSuccess(player, "All NPC data saved!");
    }
    
    private void handleListCommand(Player player) {
        List<NPCManager.NPCDetails> allNPCs = new ArrayList<>(npcManager.getAllNPCDetails());
        
        if (allNPCs.isEmpty()) {
            MessageUtil.sendInfo(player, "No NPCs found.");
            return;
        }
        
        MessageUtil.sendInfo(player, "&6=== Available NPCs ===");
        
        for (NPCManager.NPCDetails details : allNPCs) {
            TaskQueue queue = taskManager.getTaskQueue(details.getCitizensId());
            String taskStatus = (queue != null && queue.hasActiveTask()) ? "&a[Working]" : "&7[Idle]";
            
            MessageUtil.sendInfo(player, "&e#" + details.getCitizensId() + " &7- " + details.getName() + 
                " (" + details.getType() + ") " + taskStatus);
        }
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            List<String> subCommands = Arrays.asList("help", "status", "tasks", "give", "clear", "reload", "save", "list");
            for (String subCommand : subCommands) {
                if (subCommand.toLowerCase().startsWith(args[0].toLowerCase())) {
                    completions.add(subCommand);
                }
            }
        } else if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            
            if (subCommand.equals("status") || subCommand.equals("tasks") || subCommand.equals("give") || subCommand.equals("clear")) {
                // Add NPC IDs
                for (NPCManager.NPCDetails details : npcManager.getAllNPCDetails()) {
                    completions.add(String.valueOf(details.getCitizensId()));
                }
            }
        } else if (args.length == 3 && args[0].toLowerCase().equals("give")) {
            // Add tool types
            List<String> tools = Arrays.asList(
                "WOODEN_AXE", "STONE_AXE", "IRON_AXE", "GOLDEN_AXE", "DIAMOND_AXE", "NETHERITE_AXE",
                "WOODEN_PICKAXE", "STONE_PICKAXE", "IRON_PICKAXE", "GOLDEN_PICKAXE", "DIAMOND_PICKAXE", "NETHERITE_PICKAXE",
                "WOODEN_HOE", "STONE_HOE", "IRON_HOE", "GOLDEN_HOE", "DIAMOND_HOE", "NETHERITE_HOE",
                "WOODEN_SHOVEL", "STONE_SHOVEL", "IRON_SHOVEL", "GOLDEN_SHOVEL", "DIAMOND_SHOVEL", "NETHERITE_SHOVEL"
            );
            
            for (String tool : tools) {
                if (tool.toLowerCase().startsWith(args[2].toLowerCase())) {
                    completions.add(tool);
                }
            }
        }
        
        return completions;
    }
}