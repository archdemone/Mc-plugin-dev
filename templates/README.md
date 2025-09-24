# Plugin Templates

This directory contains ready-to-use templates for Minecraft plugin development. When you're ready to start developing plugins, copy these templates to your main source directory.

## 📁 Available Templates

### `minecraft-plugin/`
Complete Minecraft plugin template with:
- **Main Plugin Class** - Proper initialization and shutdown
- **Command Handler** - With permission checking and tab completion
- **Event Listener** - Player join/leave events with configuration
- **Utility Classes** - Message formatting and common functions
- **Configuration Files** - `plugin.yml` and `config.yml`
- **Unit Tests** - Testing examples with JUnit and Mockito

## 🚀 How to Use Templates

### Step 1: Enable Minecraft API Dependencies
1. Open the main `pom.xml` file
2. Uncomment the Minecraft API dependency you want to use:
   - Spigot API (most common)
   - Paper API (recommended for performance)
   - Bukkit API (older, more compatible)

### Step 2: Copy Template Files
```bash
# Copy all template files to main source directory
cp -r templates/minecraft-plugin/src/* src/

# Or copy specific files as needed
cp templates/minecraft-plugin/src/main/java/dev/archdemone/mcplugin/McPluginDevPlugin.java src/main/java/dev/archdemone/mcplugin/
```

### Step 3: Build and Test
```bash
# Clean and compile
mvn clean compile

# Run tests
mvn test

# Build plugin JAR
mvn package
```

## 🎯 Template Features

### Example Command (`/example`)
- Permission checking
- Tab completion support
- Help system
- Configuration integration
- Multiple subcommands (info, test, config, help)

### Player Event Handling
- Join/leave message customization
- Configuration-based message control
- First-time player detection
- Logging integration

### Configuration System
- YAML-based configuration
- Default values with fallbacks
- Runtime configuration access
- Placeholder support in messages

### Utility Classes
- Message formatting with color codes
- Player validation utilities
- Consistent error handling
- Common plugin patterns

## 🧪 Testing Framework

The templates include comprehensive unit tests:
- **JUnit 5** for test structure
- **Mockito** for Bukkit API mocking
- **Example test patterns** for plugin components
- **Maven Surefire** integration

### Running Tests
```bash
# Run all tests
mvn test

# Run specific test class
mvn test -Dtest=MessageUtilTest

# Run tests with verbose output
mvn test -Dtest.verbose=true
```

## 🔧 Customization

### Changing Package Name
1. Update package declarations in all Java files
2. Update the `main` class path in `plugin.yml`
3. Update test package names

### Adding New Commands
1. Create new command class in `commands/` package
2. Implement `CommandExecutor` and `TabCompleter`
3. Register in main plugin class
4. Add command definition to `plugin.yml`

### Adding New Event Listeners  
1. Create new listener class in `listeners/` package
2. Implement `Listener` interface
3. Add `@EventHandler` methods
4. Register in main plugin class

## 📚 Best Practices

### Code Organization
- Keep related functionality in separate packages
- Use descriptive class and method names
- Add JavaDoc comments for public methods
- Follow Java naming conventions

### Configuration Management
- Provide sensible defaults
- Validate configuration values
- Use meaningful configuration keys
- Document configuration options

### Error Handling
- Use try-catch blocks for external operations
- Log errors with appropriate levels
- Provide helpful error messages to users
- Fail gracefully when possible

### Testing
- Write tests for utility methods
- Mock Bukkit API dependencies
- Test error conditions
- Maintain good test coverage

## 🤖 ChatGPT Development Tips

When working with ChatGPT on these templates:

1. **Be specific** about what you want to modify
2. **Reference existing patterns** in the template code
3. **Ask for explanations** of Minecraft-specific concepts
4. **Test changes immediately** after implementation
5. **Request best practices** for plugin development

### Example Prompts
- "Add a teleport command that takes x, y, z coordinates"
- "Create a listener that prevents block breaking in spawn area"
- "Add configuration option to customize join messages"
- "Implement a cooldown system for the example command"

---

**Ready to start plugin development!** 🎮✨