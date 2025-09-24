#!/bin/bash

# Minecraft Plugin Development Workspace Setup Script
set -e

echo "🚀 Setting up Minecraft Plugin Development Workspace..."

# Colors for output
RED='\033[0;31m'
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

print_error() {
    echo -e "${RED}[ERROR]${NC} $1"
}

# Check prerequisites
check_prereqs() {
    print_status "Checking prerequisites..."
    
    # Check Java
    if command -v java &> /dev/null; then
        JAVA_VERSION=$(java -version 2>&1 | awk -F '"' '/version/ {print $2}' | awk -F '.' '{print $1}')
        if [ "$JAVA_VERSION" -ge 17 ] 2>/dev/null; then
            print_success "Java $JAVA_VERSION found"
        else
            print_error "Java 17+ required, found Java $JAVA_VERSION"
            exit 1
        fi
    else
        print_error "Java not found. Please install Java 17+"
        exit 1
    fi
    
    # Check Maven
    if command -v mvn &> /dev/null; then
        MVN_VERSION=$(mvn -version | head -n 1 | awk '{print $3}')
        print_success "Maven $MVN_VERSION found"
    else
        print_error "Maven not found. Please install Maven 3.6+"
        exit 1
    fi
}

# Main setup function
main() {
    echo "=================================================="
    echo "🎮 Minecraft Plugin Development Workspace Setup"
    echo "=================================================="
    echo
    
    check_prereqs
    echo
    
    print_status "Building the project..."
    if mvn clean compile test; then
        print_success "Project built and tested successfully"
    else
        print_warning "Build had issues. Check Maven output above."
    fi
    
    echo
    print_success "Setup complete! 🎉"
    echo
    echo "Next steps:"
    echo "  1. Edit pom.xml to enable Minecraft API dependencies"
    echo "  2. Review README.md for detailed instructions" 
    echo "  3. Run 'mvn clean package' to build your plugin"
    echo
    echo "Happy coding! 🚀"
}

main "$@"