# TinyPNG Compressor Pro

![Build](https://img.shields.io/badge/build-passing-brightgreen)
![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)
![IntelliJ Platform](https://img.shields.io/badge/platform-IntelliJ-orange.svg)

[English](README.md) | 简体中文 | [繁體中文](README_zh-TW.md)

一款强大的 IntelliJ IDEA 插件，将 [TinyPNG](https://tinypng.com) 图片压缩功能无缝集成到您的开发工作流中。直接在 IDE 中压缩 PNG、JPEG、WebP 和 AVIF 图片，支持批量处理、对比预览、格式转换等高级功能。

## ✨ 功能特性

### 🎯 核心功能

- **多格式支持**：压缩 PNG、JPEG、WebP 和 AVIF 图片
- **批量处理**：支持单个文件、多选文件或整个目录
- **智能右键菜单**：在项目视图中右键点击任意图片文件或文件夹即可压缩
- **可视化对比**：并排对比和叠加滑动两种模式查看压缩效果
- **实时统计**：跟踪压缩率、文件大小节省和处理进度

### 🔧 高级功能

- **多 API Key 管理**：配置多个 TinyPNG API 密钥并自动轮换
- **用量监控**：实时跟踪每个密钥的 API 使用量和限额
- **图片缩放**：内置缩放选项（Scale、Fit、Cover、Thumb）
- **格式转换**：在压缩过程中转换图片格式（PNG、JPEG、WebP、AVIF）
- **元数据保留**：可选择性保留版权、位置和创建日期信息
- **灵活输出**：选择替换原文件、保存压缩版本或创建备份

### 📊 用户界面

- **交互式文件树**：通过复选框树形视图选择要压缩的图片
- **进度跟踪**：批量操作时显示实时进度指示器
- **对比面板**：专业的压缩前后对比，支持缩放和叠加模式
- **设置集成**：在 IDE 首选项中提供完整的设置页面

## 📥 安装

### 从 JetBrains Marketplace 安装

1. 打开 **设置/首选项** → **插件**
2. 搜索 "**TinyPNG Compressor Pro**"
3. 点击 **安装**
4. 重启 IDE

### 手动安装

1. 从 [Releases](https://github.com/yourusername/TinyPNG/releases) 下载最新版本
2. 打开 **设置/首选项** → **插件**
3. 点击 ⚙️ 图标 → **从磁盘安装插件...**
4. 选择下载的 `.zip` 文件
5. 重启 IDE

## 🚀 快速开始

### 1. 配置 API Key

使用插件前需要 TinyPNG API 密钥：

1. 在 [tinypng.com/developers](https://tinypng.com/developers) 获取免费 API 密钥
2. 打开 **设置** → **工具** → **TinyPNG**
3. 添加您的 API 密钥
4. 配置首选项（可选）

![设置页面](https://raw.githubusercontent.com/gongshoudao/TinyPNG/refs/heads/main/tiny-png-tools-settings.png)  
*设置页面展示多 API 密钥配置和用量监控*

### 2. 压缩图片

在项目视图中右键点击任意图片文件、多个文件或目录：

![右键菜单](https://raw.githubusercontent.com/gongshoudao/TinyPNG/refs/heads/main/ScreenShot_menu.png)  
*右键菜单显示 TinyPNG 选项*

从右键菜单中选择 **TinyPng** 打开压缩对话框。

### 3. 选择和配置

![主对话框](https://raw.githubusercontent.com/gongshoudao/TinyPNG/refs/heads/main/ScreenShot_main_window.png)  
*主压缩对话框，包含文件树、选项面板和预览*

压缩对话框提供：
- **文件树**：选择要压缩的图片
- **选项面板**：配置缩放、格式转换和元数据设置
- **预览**：查看选中图片的详细信息和预估节省空间

### 4. 查看结果

压缩后查看对比效果：

![对比面板](https://raw.githubusercontent.com/gongshoudao/TinyPNG/refs/heads/main/ScreenShot_main_compare_mode.png)  
*压缩前后对比，支持并排和叠加模式*

功能特性：
- **并排视图**：对比原图和压缩后的版本
- **叠加模式**：拖动滑块查看差异
- **缩放控制**：详细检查质量
- **统计信息**：查看文件大小、压缩率和尺寸

## 📖 详细使用

### API Key 管理

#### 添加多个密钥

插件支持多个 API 密钥并自动轮换：

```
密钥 1: xxxxxxxxxxxxxxxxxxxxxxxxxxxx (480/500 次压缩)
密钥 2: yyyyyyyyyyyyyyyyyyyyyyyyyyyy (125/500 次压缩)
密钥 3: zzzzzzzzzzzzzzzzzzzzzzzzzzzz (50/500 次压缩)
```

当一个密钥达到限额时，插件会自动切换到下一个可用密钥。

#### 监控用量

每个 API 密钥显示：
- 当前压缩次数
- 月度限额
- 可视化进度指示器
- 状态（活动/已达限额）

### 压缩选项

#### 图片缩放

选择四种缩放方法之一：

- **Scale**：按宽度和高度等比例缩放
- **Fit**：适应指定尺寸并保持宽高比
- **Cover**：填充指定尺寸，必要时裁剪
- **Thumb**：使用智能裁剪创建缩略图

配置尺寸：
```
宽度: 1920 px
高度: 1080 px
方法: Fit
```

#### 格式转换

在压缩过程中转换图片格式：
- PNG → JPEG（用于无透明度的照片）
- JPEG → PNG（添加透明度支持）
- 任意格式 → WebP（更好压缩的现代格式）
- 任意格式 → AVIF（压缩率最高的新一代格式）

#### 元数据保留

可选择保留：
- **版权**：版权声明和署名
- **位置**：GPS 坐标和位置数据
- **创建日期**：原始文件创建时间戳

### 输出选项

选择如何处理压缩后的图片：

1. **替换原文件**：用压缩版本覆盖原文件
2. **另存为**：创建新的压缩文件（例如 `image_compressed.png`）
3. **创建备份**：保留原文件并保存压缩版本

## ⚙️ 配置

通过 **设置** → **工具** → **TinyPNG** 访问设置：

### API 密钥
- 添加/删除多个 API 密钥
- 查看每个密钥的用量统计
- 启用/禁用自动密钥轮换

### 默认选项
- **默认缩放方法**：设置所有操作的默认缩放方法
- **自动切换密钥**：达到限额时自动轮换密钥
- **显示通知**：压缩后显示成功/失败通知

### 文件处理
- **保留原文件**：压缩后保留原文件
- **替换原文件**：直接替换原文件（默认）
- **创建备份**：使用 `.backup` 扩展名保存原文件

## 🤝 贡献

欢迎贡献！以下是您可以提供帮助的方式：

### 报告问题

发现 bug？请提交 issue 并包含：
- 问题描述
- 重现步骤
- 预期行为与实际行为
- 截图（如适用）
- IDE 版本和插件版本

### 提交拉取请求

1. Fork 仓库
2. 创建功能分支：`git checkout -b feature/amazing-feature`
3. 进行修改
4. 添加测试（如适用）
5. 提交更改：`git commit -m 'Add amazing feature'`
6. 推送到分支：`git push origin feature/amazing-feature`
7. 开启拉取请求

### 开发设置

```bash
# 克隆仓库
git clone https://github.com/yourusername/TinyPNG.git
cd TinyPNG

# 构建插件
./gradlew build

# 在沙盒 IDE 中运行插件
./gradlew runIde

# 运行测试
./gradlew test
```

## 📄 许可证

本项目采用 Apache License 2.0 许可 - 详见 [LICENSE](LICENSE) 文件。

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

## 🔗 资源

- **TinyPNG 网站**：[tinypng.com](https://tinypng.com)
- **API 文档**：[tinypng.com/developers](https://tinypng.com/developers)
- **插件主页**：[JetBrains Marketplace](https://plugins.jetbrains.com)
- **问题跟踪**：[GitHub Issues](https://github.com/yourusername/TinyPNG/issues)

## 📞 支持

需要帮助？以下是获取支持的方式：

- 📧 **邮箱**：gsd@androidcycle.com
- 🌐 **网站**：[androidcycle.com](https://androidcycle.com)
- 🐛 **Bug 报告**：[GitHub Issues](https://github.com/yourusername/TinyPNG/issues)

## 🙏 致谢

- [TinyPNG](https://tinypng.com) 提供优秀的图片压缩服务
- [Tinify Java SDK](https://github.com/tinify/tinify-java) 提供 Java API 客户端
- JetBrains 提供 IntelliJ Platform SDK

---

**由 Android Cycle 用 ❤️ 制作**
