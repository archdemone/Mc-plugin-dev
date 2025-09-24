# Development Workspace Setup Guide

This guide will help you set up the Minecraft Plugin Development Workspace for remote development with ChatGPT's code copilot.

## 🎯 Workspace Goals

This workspace is designed to:
- Enable **remote plugin development** away from your main PC
- Work seamlessly with **ChatGPT's code copilot**
- Provide a **complete development environment** 
- Include **best practices** and **examples**
- Support **rapid prototyping** and **testing**

## 🚀 Quick Setup (5 minutes)

### Step 1: Environment Check
```bash
# Check Java version (need 17+)
java -version

# Check Maven version (need 3.6+)
mvn -version

# Check Git
git --version
```

### Step 2: Enable Minecraft API
1. Open `pom.xml` in your editor
2. Find the commented dependency sections around line 30
3. Uncomment your preferred option:

**For most users (Spigot API):**
```xml
<!-- Uncomment this repository -->
<repository>
    <id>spigot-repo</id>
    <url>https://hub.spigotmc.org/nexus/content/repositories/snapshots/</url>
</repository>

<!-- Uncomment this dependency -->
<dependency>
    <groupId>org.spigotmc</groupId>
    <artifactId>spigot-api</artifactId>
    <version>1.20.4-R0.1-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
```

### Step 3: Test the Setup
```bash
# This should complete successfully
mvn clean compile test package

# You should see "BUILD SUCCESS"
```

## 🛠️ IDE Setup

### VS Code (Recommended)

1. **Install Extensions:**
```bash
code --install-extension vscjava.vscode-java-pack
code --install-extension redhat.java
code --install-extension vscjava.vscode-maven
```

2. **Open the Project:**
```bash
code .
```

3. **Verify Setup:**
   - Java project should be recognized
   - Maven tasks should be available in the command palette
   - IntelliSense should work in Java files

### IntelliJ IDEA

1. Open IntelliJ IDEA
2. Choose "Open" and select the project folder
3. Wait for Maven import to complete
4. Verify the project SDK is set to Java 17+

## 🎮 Development Server Setup (Optional)

For testing your plugins, you'll need a Minecraft server:

### Option 1: Paper Server (Recommended)
```bash
# Create server directory
mkdir test-server
cd test-server

# Download Paper 1.20.1 (adjust version as needed)
wget https://api.papermc.io/v2/projects/paper/versions/1.20.1/builds/latest/downloads/paper-1.20.1-latest.jar

# Rename for easier use
mv paper-*.jar server.jar

# Create start script
echo 'java -Xmx2G -Xms1G -jar server.jar nogui' > start.sh
chmod +x start.sh

# Accept EULA
echo 'eula=true' > eula.txt

# Start server (first time will generate world)
./start.sh
```

### Option 2: Spigot Server
```bash
# Create server directory
mkdir test-server
cd test-server

# Build Spigot using BuildTools
wget https://hub.spigotmc.org/jenkins/job/BuildTools/lastSuccessfulBuild/artifact/target/BuildTools.jar
java -jar BuildTools.jar --rev 1.20.1

# Start server
java -Xmx2G -Xms1G -jar spigot-*.jar nogui
```

## 🧪 Development Workflow

### 1. Code → Build → Test Cycle
```bash
# Make changes to your plugin code
# Then build and test:
mvn clean compile test

# If tests pass, package the plugin:
mvn package

# The JAR will be in target/mc-plugin-dev-*.jar
```

### 2. Plugin Testing
```bash
# Copy plugin to test server
cp target/mc-plugin-dev-*.jar test-server/plugins/

# Restart server or reload plugins
# Check server console for loading messages
```

### 3. Debugging
- Use `getLogger().info()` for debug output
- Check server logs in `test-server/logs/latest.log`
- Use Java debugger with IDE for complex issues

## 🤖 Working with ChatGPT Copilot

### Effective Prompts

**Good prompts:**
- "Add a command that teleports players to spawn"  
- "Create a listener that prevents block breaking in certain regions"
- "Implement a configuration option to disable welcome messages"

**Include context:**
- Mention you're working with this Minecraft plugin workspace
- Reference existing code patterns when possible
- Specify Minecraft version compatibility needs

### Code Review Process

1. **Ask for explanations:** "What does this code do?"
2. **Request improvements:** "How can this be optimized?"
3. **Validate patterns:** "Is this following Minecraft plugin best practices?"
4. **Test suggestions:** Always test generated code immediately

### Common Tasks

**Adding a new command:**
1. Create command class in `src/main/java/dev/archdemone/mcplugin/commands/`
2. Register in main plugin class
3. Add to `plugin.yml`
4. Write tests

**Adding an event listener:**
1. Create listener class in `src/main/java/dev/archdemone/mcplugin/listeners/`
2. Register in main plugin class
3. Test with server events

**Adding configuration options:**
1. Add to `src/main/resources/config.yml`
2. Access via `getConfig().getString()` etc.
3. Document in README

## 🐛 Troubleshooting

### Build Issues

**Maven dependency errors:**
```bash
# Clear local repository and retry
rm -rf ~/.m2/repository/org/spigotmc
rm -rf ~/.m2/repository/io/papermc
mvn clean install
```

**Java version issues:**
```bash
# Check JAVA_HOME
echo $JAVA_HOME

# Set if needed (adjust path)
export JAVA_HOME=/usr/lib/jvm/java-17-openjdk
```

### Runtime Issues

**Plugin not loading:**
- Check `plugin.yml` syntax
- Verify main class path is correct
- Check server compatibility

**Commands not working:**
- Ensure commands are registered in main class
- Check permissions in `plugin.yml`
- Verify command executor is set

### Development Environment

**VS Code Java issues:**
- Reload window: Ctrl+Shift+P → "Developer: Reload Window"
- Clean workspace: Ctrl+Shift+P → "Java: Clean Workspace"
- Check Java extension is enabled

**Maven not recognized:**
- Ensure Maven is in PATH
- Restart terminal/IDE after installation
- Use full path to mvn if needed

## 📞 Getting Help

### Community Resources
- [r/admincraft](https://reddit.com/r/admincraft) - Server administration
- [SpigotMC Forums](https://www.spigotmc.org/forums/) - Plugin development
- [Paper Discord](https://discord.gg/papermc) - Paper-specific help

### Documentation
- This workspace README.md
- Inline code comments
- JavaDoc in source files

### ChatGPT Best Practices
1. Be specific about what you want to achieve
2. Provide error messages when asking for help
3. Reference existing code patterns
4. Ask for step-by-step explanations
5. Always test suggested changes

---

**You're ready to start developing!** The workspace is configured and ready for ChatGPT-assisted Minecraft plugin development. 🚀