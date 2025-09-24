# Minecraft Plugin Development Workspace

A complete workspace designed for remote Minecraft plugin development with ChatGPT's code copilot assistance.

## 🚀 Quick Start

This workspace provides everything needed for Minecraft plugin development, including:

- **Maven build system** with proper Minecraft API dependencies
- **IDE configuration** for VS Code with Java extensions
- **Example plugin structure** with commands, listeners, and utilities
- **Unit testing framework** with JUnit 5 and Mockito
- **Development scripts** for building and testing
- **Documentation** and best practices guides

## 📋 Prerequisites

### Required Software
- **Java 17 or higher** - Modern Minecraft plugins require Java 17+
- **Apache Maven 3.6+** - For dependency management and building
- **VS Code** (recommended) or IntelliJ IDEA - For development
- **Git** - For version control

### VS Code Extensions (Recommended)
```bash
# Install these extensions for the best development experience:
code --install-extension vscjava.vscode-java-pack
code --install-extension redhat.java
code --install-extension vscjava.vscode-maven
code --install-extension redhat.vscode-yaml
```

## 🛠️ Setup Instructions

### 1. Clone and Setup Repository
```bash
git clone https://github.com/archdemone/Mc-plugin-dev.git
cd Mc-plugin-dev
```

### 2. Configure Minecraft API Dependencies

**Important:** The workspace is currently configured without Minecraft dependencies due to network restrictions. To enable full functionality:

1. Open `pom.xml`
2. Uncomment your preferred Minecraft API dependency:

**For Spigot API (most common):**
```xml
<dependency>
    <groupId>org.spigotmc</groupId>
    <artifactId>spigot-api</artifactId>
    <version>1.20.4-R0.1-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
```

**For Paper API (recommended for performance):**
```xml
<dependency>
    <groupId>io.papermc.paper</groupId>
    <artifactId>paper-api</artifactId>
    <version>1.20.1-R0.1-SNAPSHOT</version>
    <scope>provided</scope>
</dependency>
```

3. Also uncomment the corresponding repository in the `<repositories>` section

### 3. Build the Project
```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Build plugin JAR
mvn package
```

## 📁 Project Structure

```
src/
├── main/java/dev/archdemone/mcplugin/
│   ├── McPluginDevPlugin.java          # Main plugin class
│   ├── commands/
│   │   └── ExampleCommand.java         # Example command implementation
│   ├── listeners/
│   │   └── PlayerEventListener.java    # Example event listener
│   └── utils/
│       └── MessageUtil.java            # Utility classes
├── main/resources/
│   ├── plugin.yml                      # Plugin metadata
│   └── config.yml                      # Default configuration
└── test/java/                          # Unit tests
    └── dev/archdemone/mcplugin/utils/
        └── MessageUtilTest.java        # Example test class
```

## 🎯 Features

### Example Plugin Components

1. **Main Plugin Class** (`McPluginDevPlugin.java`)
   - Proper initialization and shutdown
   - Command and listener registration
   - Configuration management

2. **Example Command** (`ExampleCommand.java`)
   - Permission checking
   - Tab completion
   - Help system
   - Configuration integration

3. **Event Listener** (`PlayerEventListener.java`)
   - Player join/leave events
   - Configuration-based messages
   - Logging integration

4. **Utility Classes** (`MessageUtil.java`)
   - Message formatting and sending
   - Color code support
   - Player validation utilities

### Development Tools

- **VS Code Configuration**: Pre-configured tasks, settings, and launch configurations
- **Maven Build**: Automated compilation, testing, and packaging
- **Unit Testing**: JUnit 5 with Mockito for comprehensive testing
- **Code Quality**: Configured for Java best practices

## 🔧 Development Commands

### Maven Commands
```bash
# Clean the project
mvn clean

# Compile source code
mvn compile

# Run all tests
mvn test

# Package into JAR
mvn package

# Install to local repository
mvn install
```

### VS Code Tasks
Use `Ctrl+Shift+P` and search for "Tasks: Run Task":
- `maven: clean`
- `maven: compile` 
- `maven: test`
- `maven: package`

## 🧪 Testing

The workspace includes comprehensive unit testing setup:

```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=MessageUtilTest

# Run tests with coverage (if configured)
mvn test jacoco:report
```

## 📝 Configuration

### Plugin Configuration (`config.yml`)
The plugin includes a comprehensive configuration system:

```yaml
# Player messages
enable-welcome-message: true
welcome-message: "Welcome to the server, {player}!"

# Feature toggles
features:
  player-tracking: true
  custom-commands: true
  event-logging: true

# Development settings
development:
  debug-mode: false
  log-level: "INFO"
```

### Plugin Metadata (`plugin.yml`)
Standard Bukkit plugin configuration with:
- Commands and permissions
- API version compatibility
- Author and description information

## 🚀 Deployment

### Building for Production
```bash
# Build optimized JAR
mvn clean package

# The plugin JAR will be in target/mc-plugin-dev-1.0.0-SNAPSHOT.jar
```

### Server Installation
1. Build the plugin JAR using `mvn package`
2. Copy `target/mc-plugin-dev-*.jar` to your server's `plugins/` folder
3. Restart the server or use a plugin manager to load it
4. Configure the plugin by editing `plugins/McPluginDev/config.yml`

## 🤝 Contributing

This workspace is designed for ChatGPT copilot assistance. When working with AI:

1. **Be specific** about requirements and functionality needed
2. **Test frequently** - run builds and tests after each change
3. **Follow patterns** - use the existing code structure as examples
4. **Document changes** - update this README when adding features

### Best Practices for AI-Assisted Development

1. **Start small** - implement one feature at a time
2. **Test immediately** - verify each change works before moving on
3. **Use examples** - reference existing code patterns
4. **Ask for explanations** - understand the code being generated
5. **Validate output** - ensure generated code follows Minecraft plugin conventions

## 📚 Resources

### Minecraft Plugin Development
- [Spigot API Documentation](https://hub.spigotmc.org/javadocs/spigot/)
- [Paper API Documentation](https://docs.papermc.io/)
- [Bukkit Wiki](https://bukkit.fandom.com/wiki/Main_Page)

### Java Development
- [Java 17 Documentation](https://docs.oracle.com/en/java/javase/17/)
- [Maven Documentation](https://maven.apache.org/guides/)
- [JUnit 5 Documentation](https://junit.org/junit5/docs/current/user-guide/)

## 🐛 Troubleshooting

### Common Issues

1. **Dependency Resolution Errors**
   - Ensure you've uncommented the correct Minecraft API dependency in `pom.xml`
   - Check that the corresponding repository is also uncommented
   - Verify network connectivity to Maven repositories

2. **Compilation Errors**
   - Ensure Java 17+ is installed and configured
   - Run `mvn clean compile` to rebuild from scratch
   - Check that all required dependencies are available

3. **Plugin Not Loading**
   - Verify `plugin.yml` syntax is correct
   - Check server logs for specific error messages
   - Ensure the plugin is compatible with your server version

### Getting Help

When seeking assistance from ChatGPT or other developers:
1. Include the full error message
2. Specify your Java and Maven versions
3. Mention your target Minecraft/server version
4. Provide relevant code snippets

## 📄 License

This project is open source and available under the MIT License.

---

**Ready for remote development with ChatGPT's assistance!** 🎮✨
