# TinyPNG Compressor for IntelliJ

![Build](https://img.shields.io/badge/build-passing-brightgreen)
![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)
![IntelliJ Platform](https://img.shields.io/badge/platform-IntelliJ-orange.svg)

English | [简体中文](README_zh-CN.md) | [繁體中文](README_zh-TW.md)


A powerful IntelliJ IDEA plugin that seamlessly integrates [TinyPNG](https://tinypng.com) image compression into your development workflow. Compress PNG, JPEG, WebP, and AVIF images directly from your IDE with advanced features including batch processing, before/after comparison, format conversion, and intelligent API key management.

## ✨ Features

### 🎯 Core Capabilities

- **Multiple Format Support**: Compress PNG, JPEG, WebP, and AVIF images
- **Batch Processing**: Compress single files, multiple selections, or entire directories
- **Smart Context Menu**: Right-click on any image file or folder in the Project View
- **Visual Comparison**: Side-by-side and overlay comparison modes to review compression quality
- **Real-time Statistics**: Track compression ratio, file size savings, and processing progress

### 🔧 Advanced Features

- **Multi-API Key Management**: Configure multiple TinyPNG API keys with automatic rotation
- **Usage Monitoring**: Real-time tracking of API usage and limits for each key
- **Image Resizing**: Built-in resize options (Scale, Fit, Cover, Thumb) during compression
- **Format Conversion**: Convert images between PNG, JPEG, WebP, and AVIF formats
- **Metadata Preservation**: Optionally preserve copyright, location, and creation date information
- **Flexible Output**: Choose to replace originals, save compressed versions, or create backups

### 📊 User Interface

- **Interactive File Tree**: Select which images to compress with checkbox-based tree view
- **Progress Tracking**: Real-time progress indicators for batch operations
- **Comparison Panel**: Professional before/after comparison with zoom and overlay modes
- **Settings Integration**: Comprehensive settings page in IDE preferences

## 📥 Installation

### From JetBrains Marketplace

1. Open **Settings/Preferences** → **Plugins**
2. Search for "**TinyPNG Compressor for IntelliJ**"
3. Click **Install**
4. Restart your IDE

### Manual Installation

1. Download the latest release from [Releases](https://github.com/yourusername/TinyPNG/releases)
2. Open **Settings/Preferences** → **Plugins**
3. Click the ⚙️ icon → **Install Plugin from Disk...**
4. Select the downloaded `.zip` file
5. Restart your IDE

## 🚀 Quick Start

### 1. Configure API Key

Before using the plugin, you'll need a TinyPNG API key:

1. Get a free API key at [tinypng.com/developers](https://tinypng.com/developers)
2. Open **Settings** → **Tools** → **TinyPNG**
3. Add your API key(s)
4. Configure your preferences (optional)

![Settings Page](https://raw.githubusercontent.com/gongshoudao/TinyPNG/refs/heads/main/tiny-png-tools-settings.png)  
*Settings page showing multi-API key configuration and usage monitoring*

### 2. Compress Images

Right-click on any image file, multiple files, or a directory in the Project View:

![Context Menu](https://raw.githubusercontent.com/gongshoudao/TinyPNG/refs/heads/main/ScreenShot_menu.png)  
*Right-click context menu showing TinyPNG option*

Select **TinyPng** from the context menu to open the compression dialog.

### 3. Select and Configure

![Main Dialog](https://raw.githubusercontent.com/gongshoudao/TinyPNG/refs/heads/main/ScreenShot_main_window.png)  
*Main compression dialog with file tree, options panel, and preview*

The compression dialog provides:
- **File Tree**: Select which images to compress
- **Options Panel**: Configure resize, format conversion, and metadata settings
- **Preview**: See selected image details and estimated savings

### 4. Review Results

After compression, review the before/after comparison:

![Comparison Panel](https://raw.githubusercontent.com/gongshoudao/TinyPNG/refs/heads/main/ScreenShot_main_compare_mode.png)  
*Before/after comparison with side-by-side and overlay modes*

Features:
- **Side-by-side view**: Compare original and compressed versions
- **Overlay mode**: Drag slider to reveal differences
- **Zoom controls**: Inspect quality in detail
- **Statistics**: View file size, compression ratio, and dimensions

## 📖 Detailed Usage

### API Key Management

#### Adding Multiple Keys

The plugin supports multiple API keys with automatic rotation:

```
Key 1: xxxxxxxxxxxxxxxxxxxxxxxxxxxx (480/500 compressions)
Key 2: yyyyyyyyyyyyyyyyyyyyyyyyyyyy (125/500 compressions)
Key 3: zzzzzzzzzzzzzzzzzzzzzzzzzzzz (50/500 compressions)
```

When one key reaches its limit, the plugin automatically switches to the next available key.

#### Monitoring Usage

Each API key displays:
- Current compression count
- Monthly limit
- Visual progress indicator
- Status (Active/Limit Reached)

### Compression Options

#### Image Resizing

Choose from four resize methods:

- **Scale**: Proportionally resize by width and height
- **Fit**: Fit within specified dimensions while maintaining aspect ratio
- **Cover**: Fill specified dimensions, cropping if necessary
- **Thumb**: Create a thumbnail with smart cropping

Configure dimensions:
```
Width: 1920 px
Height: 1080 px
Method: Fit
```

#### Format Conversion

Convert images during compression:
- PNG → JPEG (for photos without transparency)
- JPEG → PNG (to add transparency support)
- Any format → WebP (modern format with better compression)
- Any format → AVIF (next-gen format with superior compression)

#### Metadata Preservation

Optionally preserve:
- **Copyright**: Copyright notices and credits
- **Location**: GPS coordinates and location data
- **Creation Date**: Original file creation timestamp

### Output Options

Choose how to handle compressed images:

1. **Replace Original**: Overwrites the original file with the compressed version
2. **Save As**: Creates a new compressed file (e.g., `image_compressed.png`)
3. **Create Backup**: Keeps original and saves compressed version

## ⚙️ Configuration

Access settings via **Settings** → **Tools** → **TinyPNG**:

### API Keys
- Add/remove multiple API keys
- View usage statistics for each key
- Enable/disable automatic key rotation

### Default Options
- **Default Resize Method**: Set the default resize method for all operations
- **Auto-switch Keys**: Automatically rotate keys when limit is reached
- **Show Notifications**: Display success/failure notifications after compression

### File Handling
- **Preserve Original Files**: Keep original files after compression
- **Replace Original**: Directly replace original files (default)
- **Create Backup**: Save original files with `.backup` extension

## 🤝 Contributing

Contributions are welcome! Here's how you can help:

### Reporting Issues

Found a bug? Please open an issue with:
- Description of the problem
- Steps to reproduce
- Expected vs actual behavior
- Screenshots (if applicable)
- IDE version and plugin version

### Submitting Pull Requests

1. Fork the repository
2. Create a feature branch: `git checkout -b feature/amazing-feature`
3. Make your changes
4. Add tests if applicable
5. Commit your changes: `git commit -m 'Add amazing feature'`
6. Push to the branch: `git push origin feature/amazing-feature`
7. Open a Pull Request

### Development Setup

```bash
# Clone the repository
git clone https://github.com/yourusername/TinyPNG.git
cd TinyPNG

# Build the plugin
./gradlew build

# Run the plugin in a sandbox IDE
./gradlew runIde

# Run tests
./gradlew test
```

## 📄 License

This project is licensed under the Apache License 2.0 - see the [LICENSE](LICENSE) file for details.

```
Copyright 2025 Hugo

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```

## 🔗 Resources

- **TinyPNG Website**: [tinypng.com](https://tinypng.com)
- **API Documentation**: [tinypng.com/developers](https://tinypng.com/developers)
- **Plugin Homepage**: [JetBrains Marketplace](https://plugins.jetbrains.com)
- **Issue Tracker**: [GitHub Issues](https://github.com/yourusername/TinyPNG/issues)

## 📞 Support

Need help? Here's how to get support:

- 📧 **Email**: gsd@androidcycle.com
- 🌐 **Website**: [androidcycle.com](https://androidcycle.com)
- 🐛 **Bug Reports**: [GitHub Issues](https://github.com/yourusername/TinyPNG/issues)

## 🙏 Acknowledgments

- [TinyPNG](https://tinypng.com) for their excellent image compression service
- [Tinify Java SDK](https://github.com/tinify/tinify-java) for the Java API client
- JetBrains for the IntelliJ Platform SDK

---

**Made with ❤️ by Android Cycle**
