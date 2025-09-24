package dev.archdemone.mcplugin;

/**
 * Simple demonstration class for the Minecraft Plugin Development Workspace
 * 
 * This class demonstrates that the workspace is properly configured and ready
 * for development. When you're ready to develop actual Minecraft plugins:
 * 
 * 1. Enable Minecraft API dependencies in pom.xml
 * 2. Copy templates from templates/minecraft-plugin/ to src/main/java/
 * 3. Start developing your plugin!
 */
public class WorkspaceDemo {
    
    private final String workspaceName;
    private final String version;
    
    public WorkspaceDemo() {
        this.workspaceName = "Minecraft Plugin Development Workspace";
        this.version = "1.0.0-SNAPSHOT";
    }
    
    /**
     * Get the workspace name
     * @return workspace name
     */
    public String getWorkspaceName() {
        return workspaceName;
    }
    
    /**
     * Get the workspace version
     * @return version string
     */
    public String getVersion() {
        return version;
    }
    
    /**
     * Check if the workspace is ready for development
     * @return true if ready
     */
    public boolean isReady() {
        return true;
    }
    
    /**
     * Get welcome message for developers
     * @return welcome message
     */
    public String getWelcomeMessage() {
        return String.format("Welcome to %s v%s! Ready for ChatGPT-assisted development.", 
                           workspaceName, version);
    }
    
    /**
     * Get setup instructions
     * @return array of setup steps
     */
    public String[] getSetupInstructions() {
        return new String[] {
            "1. Review README.md for detailed setup instructions",
            "2. Edit pom.xml to enable Minecraft API dependencies", 
            "3. Copy templates from templates/minecraft-plugin/ to src/main/java/",
            "4. Start developing your Minecraft plugin!",
            "5. Use 'mvn clean package' to build your plugin JAR"
        };
    }
    
    /**
     * Main method for demonstration
     * @param args command line arguments
     */
    public static void main(String[] args) {
        WorkspaceDemo demo = new WorkspaceDemo();
        
        System.out.println("=".repeat(60));
        System.out.println(demo.getWelcomeMessage());
        System.out.println("=".repeat(60));
        System.out.println();
        
        System.out.println("Setup Instructions:");
        for (String instruction : demo.getSetupInstructions()) {
            System.out.println(instruction);
        }
        
        System.out.println();
        System.out.println("Workspace Status: " + (demo.isReady() ? "READY" : "NOT READY"));
        System.out.println("Happy coding! 🚀");
    }
}