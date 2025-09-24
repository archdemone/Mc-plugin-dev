# NPC Integration Plugin

A comprehensive Minecraft plugin that integrates with **Citizens**, **MythicMobs**, and **ModelEngine** to create advanced NPCs with custom behaviors, models, and abilities.

## Features

### 🔧 Multi-Plugin Integration
- **Citizens Integration**: Create and manage NPCs with custom professions, equipment, and behaviors
- **MythicMobs Integration**: Add custom mob abilities, stats, and AI behaviors
- **ModelEngine Integration**: Apply custom 3D models and animations to NPCs

### 🎭 NPC Types
- **Woodcutter**: Specialized NPC with woodcutting animations and tree-finding behavior
- **Blacksmith**: NPC with smithing animations and equipment repair capabilities
- **Extensible**: Easy to add new NPC types through configuration

### 🎮 Commands
- `/npc create <type> <name>` - Create a new NPC with all integrations
- `/npc spawn <type>` - Spawn a temporary NPC
- `/npc remove <id>` - Remove an NPC
- `/npc list` - List all created NPCs
- `/npc action <id> <action>` - Make NPC perform actions (look, follow, stop)
- `/npc info <id>` - Get detailed NPC information

### 🎨 Advanced Features
- **Custom Animations**: ModelEngine animations for different NPC activities
- **Smart Behavior**: NPCs can perform contextual actions based on their environment
- **Persistent Storage**: NPC data is saved and restored between server restarts
- **Permission System**: Granular permissions for different NPC operations

## Requirements

### Required Plugins
- **Citizens** (2.0.30+)
- **MythicMobs** (5.2.1+)
- **ModelEngine** (4.2.0+)

### Server Requirements
- **Minecraft**: 1.20.1+
- **Java**: 17+
- **Spigot/Paper**: Latest recommended

## Installation

1. **Download Dependencies**:
   ```bash
   # Install Citizens
   # Download from: https://www.spigotmc.org/resources/citizens.13811/
   
   # Install MythicMobs
   # Download from: https://www.spigotmc.org/resources/mythicmobs.5702/
   
   # Install ModelEngine
   # Download from: https://www.spigotmc.org/resources/model-engine.104514/
   ```

2. **Build the Plugin**:
   ```bash
   git clone <repository-url>
   cd npc-integration
   mvn clean package
   ```

3. **Install**:
   - Copy the generated JAR file to your server's `plugins` folder
   - Restart your server
   - Configure the plugin using the generated `config.yml`

## Configuration

### Basic Configuration
```yaml
# General Settings
general:
  debug: false
  auto-save-interval: 300
  max-npcs-per-player: 10

# NPC Types
npc-types:
  woodcutter:
    profession: "fletcher"
    model: "woodcutter"
    mythicmob-type: "Woodcutter"
    abilities:
      - "woodcutting"
      - "tree-planting"
    inventory:
      - "wooden_axe"
      - "sapling"
```

### Citizens Configuration
The plugin automatically configures Citizens NPCs with:
- **Profession**: Set based on NPC type (fletcher for woodcutter, armorer for blacksmith)
- **Skin**: Applied based on profession
- **Equipment**: Configured from the inventory list
- **Behaviors**: Look close, pathfinding, and interaction traits

### MythicMobs Configuration
Create MythicMobs configurations for your NPC types:

**Woodcutter.yml**:
```yaml
Woodcutter:
  Type: PLAYER
  Display: '&6Woodcutter'
  Health: 20
  Damage: 0
  Options:
    MovementSpeed: 0.25
    FollowRange: 16
  Skills:
  - skill{s=WoodcuttingAnimation} @target ~onTimer:100
```

### ModelEngine Configuration
Create custom models for your NPCs:

**woodcutter.bbmodel**:
- Create your custom woodcutter model
- Add animations: `idle`, `woodcutting`, `rest`, `wave`
- Export and place in ModelEngine's models folder

## Usage Examples

### Creating a Woodcutter NPC
```bash
# Create a woodcutter named "Timber"
/npc create woodcutter Timber

# The NPC will be created with:
# - Citizens: Fletcher profession with wooden axe
# - MythicMobs: Custom woodcutter mob with abilities
# - ModelEngine: Custom woodcutter model with animations
```

### Interacting with NPCs
```bash
# Make NPC look at you
/npc action 1 look

# Make NPC follow you
/npc action 1 follow

# Make NPC stop following
/npc action 1 stop

# Get NPC information
/npc info 1
```

### Programming Integration
```java
// Get the plugin instance
NPCIntegrationPlugin plugin = NPCIntegrationPlugin.getInstance();

// Create a woodcutter NPC
WoodcutterExample woodcutterExample = new WoodcutterExample(plugin);
NPCManager.NPCDetails details = woodcutterExample.createWoodcutterNPC(location, "Lumberjack");

// Access individual integrations
CitizensIntegration citizens = plugin.getCitizensIntegration();
MythicMobsIntegration mythicMobs = plugin.getMythicMobsIntegration();
ModelEngineIntegration modelEngine = plugin.getModelEngineIntegration();
```

## API Documentation

### CitizensIntegration
- `createNPC(Location, String, String)` - Create a Citizens NPC
- `removeNPC(int)` - Remove NPC by ID
- `makeNPCLookAt(NPC, Player)` - Make NPC look at player
- `makeNPCFollow(NPC, Player)` - Make NPC follow player

### MythicMobsIntegration
- `spawnMythicMob(Location, String, int)` - Spawn MythicMob with level
- `isMythicMob(Entity)` - Check if entity is MythicMob
- `getMythicMobType(Entity)` - Get MythicMob type name
- `removeMythicMob(Entity)` - Remove MythicMob entity

### ModelEngineIntegration
- `applyModel(Entity, String)` - Apply custom model to entity
- `playAnimation(Entity, String, String)` - Play model animation
- `stopAnimation(Entity, String, String)` - Stop model animation
- `hasModel(Entity, String)` - Check if entity has model

### NPCManager
- `createCompleteNPC(Location, String, String)` - Create NPC with all integrations
- `removeNPC(int)` - Remove NPC completely
- `getNPCDetails(int)` - Get NPC information
- `performNPCAction(int, String, Player)` - Make NPC perform action

## Troubleshooting

### Common Issues

**NPC not appearing**:
- Check if all required plugins are installed and enabled
- Verify Citizens NPC is spawned correctly
- Check MythicMobs mob type exists in config

**Animations not working**:
- Verify ModelEngine model is registered
- Check animation names match model animations
- Ensure entity has model applied

**NPC not responding**:
- Check Citizens NPC traits are properly configured
- Verify MythicMobs AI is working
- Check plugin permissions

### Debug Mode
Enable debug mode in config:
```yaml
general:
  debug: true
```

This will provide detailed logging of all NPC operations.

## Contributing

1. Fork the repository
2. Create a feature branch
3. Make your changes
4. Add tests if applicable
5. Submit a pull request

## License

This project is licensed under the MIT License - see the LICENSE file for details.

## Support

- **Issues**: Report bugs and request features on GitHub
- **Discord**: Join our Discord server for community support
- **Documentation**: Check the wiki for detailed guides

## Acknowledgments

- **Citizens Team** - For the excellent NPC framework
- **MythicMobs Team** - For the powerful mob customization system
- **ModelEngine Team** - For the amazing custom model support
- **Spigot Community** - For inspiration and feedback

---

**Note**: This plugin is designed to work with existing, well-established plugins to ensure reliability and compatibility. By leveraging their APIs, we can focus on creating innovative NPC behaviors rather than reinventing the wheel.