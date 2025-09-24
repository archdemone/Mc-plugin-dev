# NPC Chat System - Usage Examples

## 🎯 **What You Can Do Right Now**

### **1. Spawn NPCs (Commands Only)**
```bash
/npc create woodcutter Timber        # Creates a woodcutter named "Timber"
/npc create blacksmith Forge         # Creates a blacksmith named "Forge"
/npc spawn woodcutter               # Spawns a temporary woodcutter
```

### **2. Chat with NPCs (Natural Conversation)**
Once you have NPCs, you can talk to them like real players!

#### **Greetings & Basic Chat**
```
Hello @Timber                        # Greet the woodcutter named Timber
Hi there #1                         # Greet NPC with ID 1
Hey!                                # Talk to nearby NPCs (within 5 blocks)
```

#### **Task Assignment Through Chat**
```
@Timber can you chop some trees for me?     # Assign woodcutting task
@Timber please plant some saplings          # Assign planting task  
@Timber follow me                           # Make NPC follow you
@Forge can you repair my equipment?         # Assign repair task
@Forge help me craft some tools             # Assign smithing task
```

#### **Questions & Help**
```
@Timber what can you do?                    # Ask about capabilities
@Timber help                               # Get help with available tasks
@Timber how are you?                       # General conversation
```

#### **Conversation Flow**
```
You: Hello @Timber
Timber: Greetings, PlayerName! I'm Timber, the local woodcutter. I can help you with chopping trees, planting saplings, or any other wood-related tasks!

You: Can you chop some trees?
Timber: I'll start chopping trees for you! I'll look for nearby trees and get to work.

# NPC automatically starts working...
# After a while, NPC reports progress:
Timber: I've chopped 3 trees so far!

# You can continue the conversation:
You: Thanks Timber!
Timber: Take care, PlayerName! Come back anytime you need help.
```

## 🎮 **Complete Interaction Examples**

### **Example 1: Woodcutter Work Session**
```
# Spawn a woodcutter
/npc create woodcutter Lumberjack

# Start conversation
You: Hello @Lumberjack
Lumberjack: Greetings, PlayerName! I'm Lumberjack, the local woodcutter. I can help you with chopping trees, planting saplings, or any other wood-related tasks!

You: Can you chop some trees for me?
Lumberjack: I'll start chopping trees for you! I'll look for nearby trees and get to work.

# NPC automatically:
# - Looks for nearby trees
# - Moves to trees
# - Plays chopping animations
# - Reports progress every 30 seconds

You: How's it going?
Lumberjack: I've chopped 5 trees so far!

You: Great job! Can you plant some saplings now?
Lumberjack: I'll help you plant saplings! I'll look for suitable spots and plant them.

# NPC switches to planting mode automatically
```

### **Example 2: Blacksmith Service**
```
# Spawn a blacksmith
/npc create blacksmith Hammer

# Start conversation
You: Hi @Hammer
Hammer: Hello there, PlayerName! I'm Hammer, the blacksmith. I can repair your equipment, craft items, or help with any smithing needs!

You: Can you repair my sword?
Hammer: I'll help you repair your equipment! Bring me your damaged items and I'll fix them up.

You: What can you craft for me?
Hammer: I can help you with smithing! What would you like me to craft for you?

You: Can you follow me to my base?
Hammer: I'll follow you around! Just say 'stop following' when you want me to stay put.
```

### **Example 3: Multiple NPCs**
```
# Create multiple NPCs
/npc create woodcutter TreeFeller
/npc create blacksmith IronWorker

# Talk to specific NPCs by name
You: @TreeFeller can you chop some wood?
TreeFeller: I'll start chopping trees for you! I'll look for nearby trees and get to work.

You: @IronWorker can you repair my armor?
IronWorker: I'll help you repair your equipment! Bring me your damaged items and I'll fix them up.

# Or talk to specific NPCs by ID
You: #1 how are you doing?
TreeFeller: I've chopped 8 trees so far!
```

## 🔧 **How the System Works**

### **Chat Detection Methods**
1. **@NPCName** - Mention NPC by name: `@Timber hello`
2. **#NPCID** - Mention NPC by ID: `#1 how are you?`
3. **Nearby NPCs** - Just talk normally when near NPCs: `hello` (within 5 blocks)

### **Task Execution**
When you assign tasks through chat:
1. **NPC acknowledges** the task assignment
2. **Task starts automatically** - NPC begins working
3. **Progress updates** - NPC reports progress every 30 seconds
4. **Animations play** - NPC shows appropriate animations
5. **Smart behavior** - NPC moves around, finds targets, etc.

### **Memory System**
- **NPCs remember you** - They recognize returning players
- **Conversation history** - NPCs remember previous conversations
- **Task tracking** - NPCs remember what tasks they've done for you
- **Context awareness** - NPCs use conversation history for better responses

## 🎨 **Advanced Features**

### **Natural Language Processing**
The system understands various ways to say things:
- "chop trees", "cut wood", "get some lumber"
- "plant saplings", "grow trees", "plant some seeds"
- "follow me", "come with me", "walk with me"
- "repair my gear", "fix my equipment", "mend my tools"

### **Contextual Responses**
NPCs give different responses based on:
- **Previous conversations** - "Hello again, PlayerName!"
- **Current tasks** - "I'm busy chopping trees right now"
- **NPC type** - Woodcutters talk about wood, blacksmiths about metal
- **Time of day** - Different greetings for morning/evening

### **Task Coordination**
- **Multiple tasks** - NPCs can switch between tasks
- **Task priority** - Some tasks override others
- **Task completion** - NPCs report when tasks are done
- **Player proximity** - NPCs stop if player moves too far away

## 🚀 **Getting Started**

1. **Install the plugin** and required dependencies
2. **Create your first NPC**: `/npc create woodcutter MyFirstNPC`
3. **Start talking**: `Hello @MyFirstNPC`
4. **Give tasks**: `@MyFirstNPC can you chop some trees?`
5. **Watch them work** - NPCs will automatically perform tasks with animations and progress updates!

The system is designed to feel natural - just talk to NPCs like you would talk to real players, and they'll understand and help you with their specialized tasks!