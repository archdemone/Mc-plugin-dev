package dev.archdemone.npcintegration.commands;

import dev.archdemone.npcintegration.NPCIntegrationPlugin;
import dev.archdemone.npcintegration.managers.NPCManager;
import dev.archdemone.npcintegration.utils.MessageUtil;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Main command handler for NPC Integration plugin
 */
public class NPCCommand implements CommandExecutor, TabCompleter {
    
    private final NPCIntegrationPlugin plugin;
    private final NPCManager npcManager;
    
    public NPCCommand(NPCIntegrationPlugin plugin) {
        this.plugin = plugin;
        this.npcManager = plugin.getNPCManager();
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
                
            case "create":
                if (!player.hasPermission("npcintegration.create")) {
                    MessageUtil.sendError(player, MessageUtil.getConfigMessage("error-permission"));
                    return true;
                }
                handleCreateCommand(player, args);
                break;
                
            case "spawn":
                if (!player.hasPermission("npcintegration.spawn")) {
                    MessageUtil.sendError(player, MessageUtil.getConfigMessage("error-permission"));
                    return true;
                }
                handleSpawnCommand(player, args);
                break;
                
            case "remove":
                if (!player.hasPermission("npcintegration.remove")) {
                    MessageUtil.sendError(player, MessageUtil.getConfigMessage("error-permission"));
                    return true;
                }
                handleRemoveCommand(player, args);
                break;
                
            case "list":
                handleListCommand(player);
                break;
                
            case "action":
                handleActionCommand(player, args);
                break;
                
            case "info":
                handleInfoCommand(player, args);
                break;
                
            default:
                MessageUtil.sendError(player, MessageUtil.getConfigMessage("error-invalid-command"));
                break;
        }
        
        return true;
    }
    
    private void sendHelpMessage(Player player) {
        MessageUtil.sendInfo(player, "&6=== NPC Integration Help ===");
        MessageUtil.sendInfo(player, "&e/npc create <type> <name> &7- Create a new NPC");
        MessageUtil.sendInfo(player, "&e/npc spawn <type> &7- Spawn a temporary NPC");
        MessageUtil.sendInfo(player, "&e/npc remove <id> &7- Remove an NPC");
        MessageUtil.sendInfo(player, "&e/npc list &7- List all NPCs");
        MessageUtil.sendInfo(player, "&e/npc action <id> <action> &7- Make NPC perform action");
        MessageUtil.sendInfo(player, "&e/npc info <id> &7- Get NPC information");
        MessageUtil.sendInfo(player, "&7Available types: woodcutter, blacksmith");
        MessageUtil.sendInfo(player, "&7Available actions: look, follow, stop");
    }
    
    private void handleCreateCommand(Player player, String[] args) {
        if (args.length < 3) {
            MessageUtil.sendError(player, "Usage: /npc create <type> <name>");
            return;
        }
        
        String npcType = args[1].toLowerCase();
        String npcName = String.join(" ", Arrays.copyOfRange(args, 2, args.length));
        
        // Validate NPC type
        if (!plugin.getConfig().contains("npc-types." + npcType)) {
            MessageUtil.sendError(player, "Invalid NPC type: " + npcType);
            return;
        }
        
        // Create the NPC
        NPCManager.NPCDetails details = npcManager.createCompleteNPC(player.getLocation(), npcName, npcType);
        
        if (details != null) {
            MessageUtil.sendSuccess(player, MessageUtil.getConfigMessage("npc-created"));
            MessageUtil.sendInfo(player, "NPC ID: " + details.getCitizensId());
        } else {
            MessageUtil.sendError(player, "Failed to create NPC!");
        }
    }
    
    private void handleSpawnCommand(Player player, String[] args) {
        if (args.length < 2) {
            MessageUtil.sendError(player, "Usage: /npc spawn <type>");
            return;
        }
        
        String npcType = args[1].toLowerCase();
        
        // Validate NPC type
        if (!plugin.getConfig().contains("npc-types." + npcType)) {
            MessageUtil.sendError(player, "Invalid NPC type: " + npcType);
            return;
        }
        
        // Spawn temporary NPC
        String tempName = npcType + "_temp_" + System.currentTimeMillis();
        NPCManager.NPCDetails details = npcManager.createCompleteNPC(player.getLocation(), tempName, npcType);
        
        if (details != null) {
            MessageUtil.sendSuccess(player, MessageUtil.getConfigMessage("npc-spawned"));
            MessageUtil.sendInfo(player, "Temporary NPC ID: " + details.getCitizensId());
        } else {
            MessageUtil.sendError(player, "Failed to spawn NPC!");
        }
    }
    
    private void handleRemoveCommand(Player player, String[] args) {
        if (args.length < 2) {
            MessageUtil.sendError(player, "Usage: /npc remove <id>");
            return;
        }
        
        try {
            int npcId = Integer.parseInt(args[1]);
            boolean success = npcManager.removeNPC(npcId);
            
            if (success) {
                MessageUtil.sendSuccess(player, MessageUtil.getConfigMessage("npc-removed"));
            } else {
                MessageUtil.sendError(player, MessageUtil.getConfigMessage("error-npc-not-found"));
            }
        } catch (NumberFormatException e) {
            MessageUtil.sendError(player, "Invalid NPC ID: " + args[1]);
        }
    }
    
    private void handleListCommand(Player player) {
        List<NPCManager.NPCDetails> npcs = new ArrayList<>(npcManager.getAllNPCDetails());
        
        if (npcs.isEmpty()) {
            MessageUtil.sendInfo(player, "No NPCs found.");
            return;
        }
        
        MessageUtil.sendInfo(player, "&6=== NPC List ===");
        for (NPCManager.NPCDetails details : npcs) {
            String info = String.format("&eID: %d &7- &a%s &7(%s)", 
                details.getCitizensId(), details.getName(), details.getType());
            MessageUtil.sendInfo(player, info);
        }
    }
    
    private void handleActionCommand(Player player, String[] args) {
        if (args.length < 3) {
            MessageUtil.sendError(player, "Usage: /npc action <id> <action>");
            return;
        }
        
        try {
            int npcId = Integer.parseInt(args[1]);
            String action = args[2].toLowerCase();
            
            boolean success = npcManager.performNPCAction(npcId, action, player);
            
            if (success) {
                MessageUtil.sendSuccess(player, "NPC performed action: " + action);
            } else {
                MessageUtil.sendError(player, "Failed to perform action or NPC not found!");
            }
        } catch (NumberFormatException e) {
            MessageUtil.sendError(player, "Invalid NPC ID: " + args[1]);
        }
    }
    
    private void handleInfoCommand(Player player, String[] args) {
        if (args.length < 2) {
            MessageUtil.sendError(player, "Usage: /npc info <id>");
            return;
        }
        
        try {
            int npcId = Integer.parseInt(args[1]);
            NPCManager.NPCDetails details = npcManager.getNPCDetails(npcId);
            
            if (details == null) {
                MessageUtil.sendError(player, MessageUtil.getConfigMessage("error-npc-not-found"));
                return;
            }
            
            MessageUtil.sendInfo(player, "&6=== NPC Information ===");
            MessageUtil.sendInfo(player, "&eName: &a" + details.getName());
            MessageUtil.sendInfo(player, "&eType: &a" + details.getType());
            MessageUtil.sendInfo(player, "&eID: &a" + details.getCitizensId());
            MessageUtil.sendInfo(player, "&eLocation: &a" + details.getLocation().getWorld().getName() + 
                " " + details.getLocation().getBlockX() + ", " + details.getLocation().getBlockY() + 
                ", " + details.getLocation().getBlockZ());
            
            if (details.getMythicMobType() != null) {
                MessageUtil.sendInfo(player, "&eMythicMob Type: &a" + details.getMythicMobType());
            }
            
            if (details.getModelName() != null) {
                MessageUtil.sendInfo(player, "&eModel: &a" + details.getModelName());
            }
            
        } catch (NumberFormatException e) {
            MessageUtil.sendError(player, "Invalid NPC ID: " + args[1]);
        }
    }
    
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> completions = new ArrayList<>();
        
        if (args.length == 1) {
            List<String> subCommands = Arrays.asList("help", "create", "spawn", "remove", "list", "action", "info");
            for (String subCommand : subCommands) {
                if (subCommand.startsWith(args[0].toLowerCase())) {
                    completions.add(subCommand);
                }
            }
        } else if (args.length == 2) {
            String subCommand = args[0].toLowerCase();
            
            switch (subCommand) {
                case "create":
                case "spawn":
                    // Add NPC types from config
                    if (plugin.getConfig().contains("npc-types")) {
                        for (String type : plugin.getConfig().getConfigurationSection("npc-types").getKeys(false)) {
                            if (type.startsWith(args[1].toLowerCase())) {
                                completions.add(type);
                            }
                        }
                    }
                    break;
                    
                case "remove":
                case "action":
                case "info":
                    // Add NPC IDs
                    for (NPCManager.NPCDetails details : npcManager.getAllNPCDetails()) {
                        String id = String.valueOf(details.getCitizensId());
                        if (id.startsWith(args[1])) {
                            completions.add(id);
                        }
                    }
                    break;
            }
        } else if (args.length == 3 && "action".equals(args[0].toLowerCase())) {
            // Add action types
            List<String> actions = Arrays.asList("look", "follow", "stop");
            for (String action : actions) {
                if (action.startsWith(args[2].toLowerCase())) {
                    completions.add(action);
                }
            }
        }
        
        return completions;
    }
}