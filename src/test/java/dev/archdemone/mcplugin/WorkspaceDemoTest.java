package dev.archdemone.mcplugin;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Test class for WorkspaceDemo
 * 
 * This demonstrates the testing setup and verifies the workspace is working correctly
 */
class WorkspaceDemoTest {
    
    private WorkspaceDemo demo;
    
    @BeforeEach
    void setUp() {
        demo = new WorkspaceDemo();
    }
    
    @Test
    void testWorkspaceName() {
        assertEquals("Minecraft Plugin Development Workspace", demo.getWorkspaceName());
    }
    
    @Test
    void testVersion() {
        assertEquals("1.0.0-SNAPSHOT", demo.getVersion());
    }
    
    @Test
    void testIsReady() {
        assertTrue(demo.isReady());
    }
    
    @Test
    void testWelcomeMessage() {
        String message = demo.getWelcomeMessage();
        assertNotNull(message);
        assertTrue(message.contains("Welcome"));
        assertTrue(message.contains("ChatGPT"));
    }
    
    @Test
    void testSetupInstructions() {
        String[] instructions = demo.getSetupInstructions();
        assertNotNull(instructions);
        assertTrue(instructions.length > 0);
        
        // Check that first instruction mentions README
        assertTrue(instructions[0].toLowerCase().contains("readme"));
    }
    
    @Test
    void testMainMethod() {
        // Test that main method runs without exceptions
        assertDoesNotThrow(() -> WorkspaceDemo.main(new String[]{}));
    }
}