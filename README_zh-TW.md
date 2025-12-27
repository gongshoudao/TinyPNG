# TinyPNG Compressor Pro

![Build](https://img.shields.io/badge/build-passing-brightgreen)
![License](https://img.shields.io/badge/license-Apache%202.0-blue.svg)
![IntelliJ Platform](https://img.shields.io/badge/platform-IntelliJ-orange.svg)

[English](README.md) | [简体中文](README_zh-CN.md) | 繁體中文

一款強大的 IntelliJ IDEA 外掛程式，將 [TinyPNG](https://tinypng.com) 圖片壓縮功能無縫整合到您的開發工作流程中。直接在 IDE 中壓縮 PNG、JPEG、WebP 和 AVIF 圖片，支援批次處理、對比預覽、格式轉換等進階功能。

## ✨ 功能特性

### 🎯 核心功能

- **多格式支援**：壓縮 PNG、JPEG、WebP 和 AVIF 圖片
- **批次處理**：支援單一檔案、多選檔案或整個目錄
- **智慧右鍵選單**：在專案檢視中右鍵點選任意圖片檔案或資料夾即可壓縮
- **視覺化對比**：並排對比和覆蓋滑動兩種模式檢視壓縮效果
- **即時統計**：追蹤壓縮率、檔案大小節省和處理進度

### 🔧 進階功能

- **多 API Key 管理**：配置多個 TinyPNG API 金鑰並自動輪換
- **用量監控**：即時追蹤每個金鑰的 API 使用量和限額
- **圖片縮放**：內建縮放選項（Scale、Fit、Cover、Thumb）
- **格式轉換**：在壓縮過程中轉換圖片格式（PNG、JPEG、WebP、AVIF）
- **中繼資料保留**：可選擇性保留版權、位置和建立日期資訊
- **彈性輸出**：選擇取代原檔案、儲存壓縮版本或建立備份

### 📊 使用者介面

- **互動式檔案樹**：透過核取方塊樹狀檢視選擇要壓縮的圖片
- **進度追蹤**：批次操作時顯示即時進度指示器
- **對比面板**：專業的壓縮前後對比，支援縮放和覆蓋模式
- **設定整合**：在 IDE 偏好設定中提供完整的設定頁面

## 📥 安裝

### 從 JetBrains Marketplace 安裝

1. 開啟 **設定/偏好設定** → **外掛程式**
2. 搜尋 "**TinyPNG Compressor Pro**"
3. 點選 **安裝**
4. 重新啟動 IDE

### 手動安裝

1. 從 [Releases](https://github.com/yourusername/TinyPNG/releases) 下載最新版本
2. 開啟 **設定/偏好設定** → **外掛程式**
3. 點選 ⚙️ 圖示 → **從磁碟安裝外掛程式...**
4. 選擇下載的 `.zip` 檔案
5. 重新啟動 IDE

## 🚀 快速開始

### 1. 配置 API Key

使用外掛程式前需要 TinyPNG API 金鑰：

1. 在 [tinypng.com/developers](https://tinypng.com/developers) 取得免費 API 金鑰
2. 開啟 **設定** → **工具** → **TinyPNG**
3. 新增您的 API 金鑰
4. 配置偏好設定（選用）

![設定頁面](https://raw.githubusercontent.com/gongshoudao/TinyPNG/refs/heads/main/tiny-png-tools-settings.png)  
*設定頁面展示多 API 金鑰配置和用量監控*

### 2. 壓縮圖片

在專案檢視中右鍵點選任意圖片檔案、多個檔案或目錄：

![右鍵選單](https://raw.githubusercontent.com/gongshoudao/TinyPNG/refs/heads/main/ScreenShot_menu.png)  
*右鍵選單顯示 TinyPNG 選項*

從右鍵選單中選擇 **TinyPng** 開啟壓縮對話方塊。

### 3. 選擇和配置

![主對話方塊](https://raw.githubusercontent.com/gongshoudao/TinyPNG/refs/heads/main/ScreenShot_main_window.png)  
*主壓縮對話方塊，包含檔案樹、選項面板和預覽*

壓縮對話方塊提供：
- **檔案樹**：選擇要壓縮的圖片
- **選項面板**：配置縮放、格式轉換和中繼資料設定
- **預覽**：檢視選中圖片的詳細資訊和預估節省空間

### 4. 檢視結果

壓縮後檢視對比效果：

![對比面板](https://raw.githubusercontent.com/gongshoudao/TinyPNG/refs/heads/main/ScreenShot_main_compare_mode.png)  
*壓縮前後對比，支援並排和覆蓋模式*

功能特性：
- **並排檢視**：對比原圖和壓縮後的版本
- **覆蓋模式**：拖曳滑桿檢視差異
- **縮放控制**：詳細檢查品質
- **統計資訊**：檢視檔案大小、壓縮率和尺寸

## 📖 詳細使用

### API Key 管理

#### 新增多個金鑰

外掛程式支援多個 API 金鑰並自動輪換：

```
金鑰 1: xxxxxxxxxxxxxxxxxxxxxxxxxxxx (480/500 次壓縮)
金鑰 2: yyyyyyyyyyyyyyyyyyyyyyyyyyyy (125/500 次壓縮)
金鑰 3: zzzzzzzzzzzzzzzzzzzzzzzzzzzz (50/500 次壓縮)
```

當一個金鑰達到限額時，外掛程式會自動切換到下一個可用金鑰。

#### 監控用量

每個 API 金鑰顯示：
- 目前壓縮次數
- 月度限額
- 視覺化進度指示器
- 狀態（活動/已達限額）

### 壓縮選項

#### 圖片縮放

選擇四種縮放方法之一：

- **Scale**：按寬度和高度等比例縮放
- **Fit**：適應指定尺寸並保持寬高比
- **Cover**：填充指定尺寸，必要時裁剪
- **Thumb**：使用智慧裁剪建立縮圖

配置尺寸：
```
寬度: 1920 px
高度: 1080 px
方法: Fit
```

#### 格式轉換

在壓縮過程中轉換圖片格式：
- PNG → JPEG（用於無透明度的照片）
- JPEG → PNG（新增透明度支援）
- 任意格式 → WebP（更好壓縮的現代格式）
- 任意格式 → AVIF（壓縮率最高的新一代格式）

#### 中繼資料保留

可選擇保留：
- **版權**：版權聲明和署名
- **位置**：GPS 座標和位置資料
- **建立日期**：原始檔案建立時間戳記

### 輸出選項

選擇如何處理壓縮後的圖片：

1. **取代原檔案**：用壓縮版本覆蓋原檔案
2. **另存新檔**：建立新的壓縮檔案（例如 `image_compressed.png`）
3. **建立備份**：保留原檔案並儲存壓縮版本

## ⚙️ 配置

透過 **設定** → **工具** → **TinyPNG** 存取設定：

### API 金鑰
- 新增/刪除多個 API 金鑰
- 檢視每個金鑰的用量統計
- 啟用/停用自動金鑰輪換

### 預設選項
- **預設縮放方法**：設定所有操作的預設縮放方法
- **自動切換金鑰**：達到限額時自動輪換金鑰
- **顯示通知**：壓縮後顯示成功/失敗通知

### 檔案處理
- **保留原檔案**：壓縮後保留原檔案
- **取代原檔案**：直接取代原檔案（預設）
- **建立備份**：使用 `.backup` 副檔名儲存原檔案

## 🤝 貢獻

歡迎貢獻！以下是您可以提供協助的方式：

### 回報問題

發現 bug？請提交 issue 並包含：
- 問題描述
- 重現步驟
- 預期行為與實際行為
- 截圖（如適用）
- IDE 版本和外掛程式版本

### 提交拉取請求

1. Fork 儲存庫
2. 建立功能分支：`git checkout -b feature/amazing-feature`
3. 進行修改
4. 新增測試（如適用）
5. 提交變更：`git commit -m 'Add amazing feature'`
6. 推送到分支：`git push origin feature/amazing-feature`
7. 開啟拉取請求

### 開發設定

```bash
# 複製儲存庫
git clone https://github.com/yourusername/TinyPNG.git
cd TinyPNG

# 建置外掛程式
./gradlew build

# 在沙盒 IDE 中執行外掛程式
./gradlew runIde

# 執行測試
./gradlew test
```

## 📄 授權條款

本專案採用 Apache License 2.0 授權 - 詳見 [LICENSE](LICENSE) 檔案。

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

## 🔗 資源

- **TinyPNG 網站**：[tinypng.com](https://tinypng.com)
- **API 文件**：[tinypng.com/developers](https://tinypng.com/developers)
- **外掛程式首頁**：[JetBrains Marketplace](https://plugins.jetbrains.com)
- **問題追蹤**：[GitHub Issues](https://github.com/yourusername/TinyPNG/issues)

## 📞 支援

需要協助？以下是取得支援的方式：

- 📧 **電子郵件**：gsd@androidcycle.com
- 🌐 **網站**：[androidcycle.com](https://androidcycle.com)
- 🐛 **Bug 回報**：[GitHub Issues](https://github.com/yourusername/TinyPNG/issues)

## 🙏 致謝

- [TinyPNG](https://tinypng.com) 提供優秀的圖片壓縮服務
- [Tinify Java SDK](https://github.com/tinify/tinify-java) 提供 Java API 客戶端
- JetBrains 提供 IntelliJ Platform SDK

---

**由 Android Cycle 用 ❤️ 製作**
