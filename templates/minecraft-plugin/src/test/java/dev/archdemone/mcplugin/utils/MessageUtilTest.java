package dev.archdemone.mcplugin.utils;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

/**
 * Test class for MessageUtil utility methods
 */
class MessageUtilTest {
    
    @Mock
    private CommandSender mockSender;
    
    @Mock
    private Player mockPlayer;
    
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }
    
    @Test
    void testFormatMessage() {
        String input = "&aHello &bWorld!";
        String expected = ChatColor.GREEN + "Hello " + ChatColor.AQUA + "World!";
        String result = MessageUtil.formatMessage(input);
        
        assertEquals(expected, result);
    }
    
    @Test
    void testIsPlayerWithPlayer() {
        assertTrue(MessageUtil.isPlayer(mockPlayer));
    }
    
    @Test
    void testIsPlayerWithNonPlayer() {
        assertFalse(MessageUtil.isPlayer(mockSender));
    }
    
    @Test
    void testGetPlayerWithPlayer() {
        assertEquals(mockPlayer, MessageUtil.getPlayer(mockPlayer));
    }
    
    @Test
    void testGetPlayerWithNonPlayer() {
        assertNull(MessageUtil.getPlayer(mockSender));
    }
}