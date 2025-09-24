#!/bin/bash

# Script to enable Minecraft API dependencies and copy templates
# This automates the process of setting up the workspace for Minecraft development

set -e

echo "🎮 Enabling Minecraft Plugin Development..."

# Colors for output
GREEN='\033[0;32m'
YELLOW='\033[1;33m'
BLUE='\033[0;34m'
NC='\033[0m' # No Color

print_status() {
    echo -e "${BLUE}[INFO]${NC} $1"
}

print_success() {
    echo -e "${GREEN}[SUCCESS]${NC} $1"
}

print_warning() {
    echo -e "${YELLOW}[WARNING]${NC} $1"
}

# Function to show usage
show_usage() {
    echo "Usage: $0 [spigot|paper|bukkit]"
    echo "  spigot  - Use Spigot API (most common)"
    echo "  paper   - Use Paper API (recommended)"
    echo "  bukkit  - Use Bukkit API (older versions)"
    echo ""
    echo "If no argument provided, defaults to spigot"
}

# Default to spigot if no argument provided
API_TYPE=${1:-spigot}

if [[ "$API_TYPE" != "spigot" && "$API_TYPE" != "paper" && "$API_TYPE" != "bukkit" ]]; then
    echo "Error: Invalid API type. Must be spigot, paper, or bukkit"
    show_usage
    exit 1
fi

print_status "Enabling $API_TYPE API..."

# Create backup of pom.xml
cp pom.xml pom.xml.backup
print_status "Created backup: pom.xml.backup"

# Enable the appropriate API in pom.xml
case $API_TYPE in
    "spigot")
        print_status "Configuring Spigot API..."
        
        # Add repository
        sed -i 's|<!-- Spigot Repository -->|<!-- Spigot Repository -->|' pom.xml
        sed -i 's|<!--<repository>|<repository>|g' pom.xml
        sed -i 's|<id>spigot-repo</id>|<id>spigot-repo</id>|g' pom.xml
        sed -i 's|</repository>-->|</repository>|g' pom.xml
        
        # Add dependency
        sed -i 's|<!-- Option 1: Spigot API -->|<!-- Option 1: Spigot API -->|' pom.xml
        sed -i '/Option 1: Spigot API/,/-->/ { s|<!--||g; s|-->||g; }' pom.xml
        ;;
        
    "paper")
        print_status "Configuring Paper API..."
        
        # Add repository
        sed -i 's|<!-- Paper Repository -->|<!-- Paper Repository -->|' pom.xml
        sed -i '/Paper Repository/,/-->/ { s|<!--||g; s|-->||g; }' pom.xml
        
        # Add dependency
        sed -i 's|<!-- Option 2: Paper API (recommended) -->|<!-- Option 2: Paper API (recommended) -->|' pom.xml
        sed -i '/Option 2: Paper API/,/-->/ { s|<!--||g; s|-->||g; }' pom.xml
        ;;
        
    "bukkit")
        print_status "Configuring Bukkit API..."
        
        # Add dependency (Bukkit is in Maven Central)
        sed -i 's|<!-- Option 3: Bukkit API (older, more compatible) -->|<!-- Option 3: Bukkit API (older, more compatible) -->|' pom.xml
        sed -i '/Option 3: Bukkit API/,/-->/ { s|<!--||g; s|-->||g; }' pom.xml
        ;;
esac

print_success "$API_TYPE API enabled in pom.xml"

# Copy template files
print_status "Copying Minecraft plugin templates..."

if [ -d "templates/minecraft-plugin" ]; then
    # Remove the demo files
    rm -f src/main/java/dev/archdemone/mcplugin/WorkspaceDemo.java
    rm -f src/test/java/dev/archdemone/mcplugin/WorkspaceDemoTest.java
    
    # Copy template files
    cp -r templates/minecraft-plugin/src/* src/
    
    print_success "Template files copied to src/"
else
    print_warning "Template directory not found. You'll need to create plugin files manually."
fi

# Test the build
print_status "Testing build..."
if mvn clean compile; then
    print_success "Build successful! Minecraft plugin development is ready."
else
    print_warning "Build failed. Check Maven output above."
    print_warning "You may need to wait for dependencies to download or check network connectivity."
fi

echo ""
print_success "Setup complete! 🎉"
echo ""
echo "Next steps:"
echo "  1. Run 'mvn clean package' to build your plugin"
echo "  2. Edit src/main/java/dev/archdemone/mcplugin/McPluginDevPlugin.java"
echo "  3. Add your plugin features"
echo "  4. Test with 'mvn test'"
echo ""
echo "Your plugin JAR will be in target/ after running 'mvn package'"
echo "Happy coding! 🚀"