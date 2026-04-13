# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Build Commands

```bash
./gradlew build          # Build the mod JAR
./gradlew runClient     # Launch Minecraft with the mod
./gradlew runServer     # Launch a dedicated server with the mod
./gradlew genIntellijRuns  # Generate run configurations for IntelliJ IDEA
./gradlew clean         # Clean build artifacts
./gradlew --refresh-dependencies  # Refresh Gradle cache
```

## Project Overview

This is a Minecraft Forge mod for Minecraft 1.20.1 (Forge 47.4.10) that adds circuit board items and integrates with Mekanism. The mod is in Chinese development context (`com.hewiegui.overmek` package).

## Architecture

### Core Structure
- **Main mod class**: `com.hewiegui.overmek.OverMek` - Entry point annotated with `@Mod`, registers items and creative tabs
- **Registry pattern**: Items use `DeferredRegister<Item>` via `ModItems` and `ModCreativeTabs`
- **Package structure**:
  - `registry/` - Deferred register holders for items and creative tabs
  - `item/` - Item implementations (e.g., `CircuitBoardItem`)
  - `capability/` - Forge capability system for attaching circuit board slots to tile entities
  - `mixin/` - Mixin classes that modify Mekanism's GUI and tile container classes

### Capability System
The mod uses Forge's capability system to attach circuit board functionality to Mekanism tile entities:
- `CircuitBoardHolder` - Capability provider storing circuit board data
- `ICircuitBoardHolder` - Capability interface
- `CircuitBoardSlotInventory` - Inventory implementation for circuit board slots
- `AttachCapabilityHandler` - Event handler that attaches capabilities to `mekanism.common.tile.*` classes

### Mixins
- `MixinGuiMekanism` (client) - Renders a circuit board slot UI element on Mekanism GUIs
- `MixinMekanismTileContainer` - Modifies Mekanism tile container behavior

### Dependencies
- Minecraft 1.20.1 with Forge 47.4.10
- Mekanism 1.20.1-10.4.16.80 (runtime)

## IDE Setup

For IntelliJ IDEA:
1. Import project, select `build.gradle`
2. Run `./gradlew genIntellijRuns`
3. Refresh Gradle project
4. Run configurations will appear for Client and Server
