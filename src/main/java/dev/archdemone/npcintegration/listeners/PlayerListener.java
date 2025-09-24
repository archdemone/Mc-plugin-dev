package dev.archdemone.npcintegration.listeners;

import dev.archdemone.npcintegration.NPCIntegrationPlugin;
import dev.archdemone.npcintegration.managers.NPCManager;
import dev.archdemone.npcintegration.integrations.MythicMobsIntegration;
import dev.archdemone.npcintegration.chat.NPCChatSystem;
import dev.archdemone.npcintegration.chat.EnhancedChatSystem;
import dev.archdemone.npcintegration.utils.MessageUtil;
import net.citizensnpcs.api.npc.NPC;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 * Player event listener for NPC interactions and management
 */
public class PlayerListener implements Listener {
    
    private final NPCIntegrationPlugin plugin;
    private final NPCManager npcManager;
    private final NPCChatSystem chatSystem;
    private final EnhancedChatSystem enhancedChatSystem;
    
    public PlayerListener(NPCIntegrationPlugin plugin) {
        this.plugin = plugin;
        this.npcManager = plugin.getNPCManager();
        this.chatSystem = plugin.getChatSystem();
        this.enhancedChatSystem = plugin.getEnhancedChatSystem();
    }
    
    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        
        // Send welcome message with NPC information
        if (player.hasPermission("npcintegration.admin")) {
            MessageUtil.sendInfo(player, "NPC Integration Plugin is active!");
            
            // Show integration status
            if (plugin.isIntegrationEnabled("Citizens")) {
                MessageUtil.sendInfo(player, "&aCitizens integration: Enabled");
            } else {
                MessageUtil.sendWarning(player, "&eCitizens integration: Disabled");
            }
            
            if (plugin.isIntegrationEnabled("MythicMobs")) {
                MessageUtil.sendInfo(player, "&aMythicMobs integration: Enabled");
            } else {
                MessageUtil.sendWarning(player, "&eMythicMobs integration: Disabled");
            }
            
            if (plugin.isIntegrationEnabled("ModelEngine")) {
                MessageUtil.sendInfo(player, "&aModelEngine integration: Enabled");
            } else {
                MessageUtil.sendWarning(player, "&eModelEngine integration: Disabled");
            }
        }
    }
    
    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        // Clear active conversations when player leaves
        if (enhancedChatSystem != null) {
            enhancedChatSystem.clearConversation(event.getPlayer());
        }
    }
    
    @EventHandler(priority = EventPriority.HIGH)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        String message = event.getMessage();
        
        // Process chat message for NPC interactions using enhanced system
        if (enhancedChatSystem != null && enhancedChatSystem.processChatMessage(player, message)) {
            // Cancel the event so the message doesn't appear in normal chat
            event.setCancelled(true);
            
            // Send the message to nearby players manually (excluding NPC responses)
            for (Player recipient : event.getRecipients()) {
                if (recipient != player) {
                    recipient.sendMessage("<" + player.getDisplayName() + "> " + message);
                }
            }
        }
    }
    
    @EventHandler
    public void onPlayerInteractEntity(PlayerInteractEntityEvent event) {
        Entity entity = event.getRightClicked();
        Player player = event.getPlayer();
        
        // Check if the entity is a Citizens NPC
        if (plugin.getCitizensIntegration() != null) {
            try {
                NPC npc = plugin.getCitizensIntegration().getNPC(entity);
                if (npc != null) {
                    handleNPCInteraction(npc, player);
                    return;
                }
            } catch (NoClassDefFoundError e) {
                // Citizens classes not available, skip NPC interaction
                plugin.getLogger().fine("Citizens classes not available for entity interaction");
            }
        }
        
        // Check if the entity is a MythicMob
        MythicMobsIntegration mythicMobsIntegration = plugin.getMythicMobsIntegration();
        if (mythicMobsIntegration != null && mythicMobsIntegration.isMythicMob(entity)) {
            handleMythicMobInteraction(entity, player);
        }
    }
    
    private void handleNPCInteraction(NPC npc, Player player) {
        NPCManager.NPCDetails details = npcManager.getNPCDetails(npc.getId());
        if (details == null) {
            return;
        }
        
        // Handle different NPC types
        String npcType = details.getType();
        
        switch (npcType.toLowerCase()) {
            case "woodcutter":
                handleWoodcutterInteraction(details, player);
                break;
                
            case "blacksmith":
                handleBlacksmithInteraction(details, player);
                break;
                
            default:
                handleGenericNPCInteraction(details, player);
                break;
        }
    }
    
    private void handleWoodcutterInteraction(NPCManager.NPCDetails details, Player player) {
        MessageUtil.sendInfo(player, "&6[Woodcutter] &eHello there! I'm " + details.getName());
        MessageUtil.sendInfo(player, "&7I can help you with woodcutting tasks and tree planting!");
        
        // Play woodcutting animation if model is available
        if (details.getModelName() != null && plugin.getModelEngineIntegration() != null) {
            Entity targetEntity = details.getMythicMobEntity();
            if (targetEntity == null && details.getCitizensNPC() != null) {
                targetEntity = (Entity) details.getCitizensNPC().getEntity();
            }
            
            if (targetEntity != null) {
                plugin.getModelEngineIntegration().playAnimation(targetEntity, details.getModelName(), "wave");
            }
        }
        
        // Make NPC look at player
        if (plugin.getCitizensIntegration() != null && details.getCitizensNPC() != null) {
            plugin.getCitizensIntegration().makeNPCLookAt(details.getCitizensNPC(), player);
        }
    }
    
    private void handleBlacksmithInteraction(NPCManager.NPCDetails details, Player player) {
        MessageUtil.sendInfo(player, "&6[Blacksmith] &eGreetings! I'm " + details.getName());
        MessageUtil.sendInfo(player, "&7I can help you with smithing and equipment repair!");
        
        // Play blacksmith animation if model is available
        if (details.getModelName() != null && plugin.getModelEngineIntegration() != null) {
            Entity targetEntity = details.getMythicMobEntity();
            if (targetEntity == null && details.getCitizensNPC() != null) {
                targetEntity = (Entity) details.getCitizensNPC().getEntity();
            }
            
            if (targetEntity != null) {
                plugin.getModelEngineIntegration().playAnimation(targetEntity, details.getModelName(), "hammer");
            }
        }
        
        // Make NPC look at player
        if (plugin.getCitizensIntegration() != null && details.getCitizensNPC() != null) {
            plugin.getCitizensIntegration().makeNPCLookAt(details.getCitizensNPC(), player);
        }
    }
    
    private void handleGenericNPCInteraction(NPCManager.NPCDetails details, Player player) {
        MessageUtil.sendInfo(player, "&6[NPC] &eHello! I'm " + details.getName());
        MessageUtil.sendInfo(player, "&7I'm a " + details.getType() + " NPC.");
        
        // Make NPC look at player
        if (plugin.getCitizensIntegration() != null && details.getCitizensNPC() != null) {
            plugin.getCitizensIntegration().makeNPCLookAt(details.getCitizensNPC(), player);
        }
    }
    
    private void handleMythicMobInteraction(Entity entity, Player player) {
        MythicMobsIntegration mythicMobsIntegration = plugin.getMythicMobsIntegration();
        String mobType = mythicMobsIntegration.getMythicMobType(entity);
        
        if (mobType != null) {
            MessageUtil.sendInfo(player, "&6[MythicMob] &eYou interacted with a " + mobType);
            
            // Find associated NPC details
            for (NPCManager.NPCDetails details : npcManager.getAllNPCDetails()) {
                if (entity.equals(details.getMythicMobEntity())) {
                    MessageUtil.sendInfo(player, "&7This is the MythicMob entity for NPC: " + details.getName());
                    break;
                }
            }
        }
    }
}