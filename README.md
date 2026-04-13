# OverMek

一个 Minecraft Forge 1.20.1 模组，为 Mekanism 添加电路板物品和功能扩展。

## English

OverMek is a Minecraft Forge 1.20.1 mod that adds circuit board items and extends functionality for Mekanism.

### Setup

1. Open command line and navigate to the extracted folder
2. Run `./gradlew genIntellijRuns` (IntelliJ IDEA)
3. Open IDEA, import project, select `build.gradle`
4. Refresh Gradle project
5. Run configurations will appear for Client and Server

### Build

```bash
./gradlew build
```

The compiled JAR will be in `build/libs/`.

### Commands

```bash
./gradlew build          # Build mod JAR
./gradlew runClient     # Launch Minecraft with mod
./gradlew runServer     # Launch dedicated server with mod
./gradlew clean         # Clean build artifacts
```

---

## 中文

OverMek 是一个 Minecraft Forge 1.20.1 模组，为 Mekanism 添加电路板物品和功能扩展。

### 环境要求

- Minecraft 1.20.1
- Forge 47.4.10
- Java 17

### 开发环境搭建

1. 打开命令行，进入项目文件夹
2. 运行 `./gradlew genIntellijRuns` (IntelliJ IDEA)
3. 打开 IDEA，导入项目，选择 `build.gradle`
4. 刷新 Gradle 项目
5. 即可看到 Client 和 Server 运行配置

### 构建

```bash
./gradlew build
```

编译后的 JAR 文件位于 `build/libs/`。

### 常用命令

```bash
./gradlew build          # 构建模组 JAR
./gradlew runClient     # 启动带模组的 Minecraft 客户端
./gradlew runServer     # 启动带模组的专用服务器
./gradlew clean         # 清理构建产物
```

## 模组内容

- **基础电路板** - 电路板物品，tier 0
- **高级电路板** - 电路板物品，tier 1
- **精英电路板** - 电路板物品，tier 2
- **终极电路板** - 电路板物品，tier 3

通过 Forge Capability 系统与 Mekanism 机器集成。

## 许可证

All Rights Reserved
