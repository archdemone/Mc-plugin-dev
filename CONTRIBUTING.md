# Contributing to Minecraft Plugin Development Workspace

Thank you for your interest in improving this workspace! This guide will help you contribute effectively.

## 🎯 Project Goals

This workspace is designed to:
- Enable remote Minecraft plugin development
- Work seamlessly with ChatGPT's code copilot
- Provide best practices and examples
- Support rapid prototyping and learning

## 🤝 How to Contribute

### Reporting Issues
1. Check existing issues first
2. Use the issue templates when available
3. Provide detailed steps to reproduce problems
4. Include your Java/Maven versions and OS

### Suggesting Features
1. Open an issue with the `enhancement` label
2. Describe the use case and benefit
3. Consider backward compatibility
4. Provide examples if possible

### Code Contributions
1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Make your changes
4. Test thoroughly
5. Commit with clear messages
6. Push to your fork
7. Open a Pull Request

## 🧪 Testing Guidelines

### Before Submitting
- [ ] All tests pass (`mvn test`)
- [ ] Code compiles without warnings (`mvn compile`)
- [ ] New features include tests
- [ ] Documentation is updated

### Testing Checklist
- [ ] Basic workspace functionality works
- [ ] Templates are valid and complete
- [ ] Scripts execute without errors
- [ ] Documentation is accurate
- [ ] CI/CD pipeline passes

### Test Commands
```bash
# Run all tests
mvn clean test

# Test workspace demo
mvn compile
java -cp target/classes dev.archdemone.mcplugin.WorkspaceDemo

# Test setup script
./scripts/setup.sh

# Test Minecraft enable script (requires network)
./scripts/enable-minecraft.sh spigot
```

## 📝 Code Style

### Java Code
- Use 4-space indentation
- Follow Google Java Style Guide
- Add JavaDoc for public methods
- Use meaningful variable names
- Keep methods focused and small

### Documentation
- Use clear, concise language
- Include code examples where helpful
- Update README.md for significant changes
- Add comments for complex logic

### Commit Messages
Use conventional commit format:
```
type(scope): description

feat(templates): add new command template
fix(scripts): resolve path issue in setup script
docs(readme): update installation instructions
```

## 🏗️ Development Setup

### Prerequisites
- Java 17+
- Maven 3.6+
- Git
- Your favorite IDE

### Local Development
```bash
# Clone your fork
git clone https://github.com/YOUR-USERNAME/Mc-plugin-dev.git
cd Mc-plugin-dev

# Run tests
mvn clean test

# Make changes and test frequently
mvn compile test
```

### Testing with Minecraft API
```bash
# Enable Minecraft dependencies
./scripts/enable-minecraft.sh spigot

# Test compilation
mvn clean compile
```

## 📁 Project Structure

```
├── src/
│   ├── main/java/           # Working demo code
│   └── test/java/           # Unit tests
├── templates/               # Plugin templates
│   └── minecraft-plugin/    # Complete plugin template
├── scripts/                 # Setup and utility scripts
├── .github/workflows/       # CI/CD configuration
├── .vscode/                 # VS Code settings
└── docs/                    # Additional documentation
```

## 🔄 CI/CD Pipeline

The project uses GitHub Actions for:
- **Testing** on Java 17 and 21
- **Building** artifacts
- **Validating** templates and scripts
- **Checking** code quality

All PRs must pass CI checks before merging.

## 📚 Areas for Contribution

### High Priority
- [ ] Additional plugin templates
- [ ] More comprehensive tests
- [ ] Better error handling in scripts
- [ ] Performance optimizations

### Medium Priority
- [ ] IDE integration improvements
- [ ] Additional utility classes
- [ ] More examples and tutorials
- [ ] Docker development environment

### Nice to Have
- [ ] GUI setup tool
- [ ] Plugin marketplace integration
- [ ] Advanced debugging tools
- [ ] Multi-language support

## 🤖 Working with ChatGPT

When contributing with AI assistance:

1. **Understand the code** before making changes
2. **Test all changes** thoroughly
3. **Review AI suggestions** critically
4. **Follow existing patterns** in the codebase
5. **Ask for explanations** when unsure

### Good AI Prompts for Contributions
- "Add unit tests for the MessageUtil class"
- "Create a template for a GUI-based command"
- "Improve error handling in the setup script"
- "Add documentation for the plugin configuration system"

## 📞 Getting Help

- **GitHub Issues** - For bugs and feature requests
- **Discussions** - For questions and general help
- **Discord** - Real-time chat (link in README)

## 📄 License

By contributing, you agree that your contributions will be licensed under the same license as the project (MIT License).

---

**Thank you for helping make this workspace better!** 🚀