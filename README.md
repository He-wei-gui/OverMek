# OverMek

OverMek is a Minecraft Forge `1.20.1` mod that adds installable circuit boards to Mekanism machines.

This repository currently targets an initial public release focused on the core OverMek board system itself. Experimental compatibility work for external addons such as Mekanism: More Machine is not included in the release build at this stage.

## Target Environment

- Minecraft `1.20.1`
- Forge `47.4.18`
- Java `17`
- Mekanism `10.4.16.80`

## Current Release Scope

OverMek currently supports six machine profiles:

- `processing`
- `generator`
- `fission`
- `power_multiblock`
- `evaporation_multiblock`
- `sps_multiblock`

The current release keeps the board system intentionally bounded:

- no KubeJS integration yet
- no Mekanism: More Machine compatibility in the release build
- no Super Factory Manager integration

## Included Circuit Boards

OverMek currently ships eight board items.

The standard four boards now use an internal material progression built around circuit substrates and circuit matrices, while the four multiblock boards still craft directly from finished late-game components.

Standard boards for single-block machines and factories:

- Basic Circuit Board
- Advanced Circuit Board
- Elite Circuit Board
- Ultimate Circuit Board

Late-game multiblock boards:

- Fission Reactor Circuit Board
- Power Multiblock Circuit Board
- Evaporation Tower Circuit Board
- SPS Circuit Board

## Supported Behavior

### Standard processing machines

Supported Mekanism processing machines and factories can gain:

- faster processing
- machine warmup
- higher energy use under load
- larger internal energy capacity
- GUI tooltip and warmup feedback

### Single-block generators

Supported Mekanism generators use the standard board line, but with generator-specific behavior:

- increased FE generation
- increased fuel consumption
- larger internal energy capacity
- startup warmup

### Multiblocks

Current multiblock support is split by profile:

- Fission Reactor: cooling efficiency, thermal stability, buffer, warmup
- Fusion Reactor / Turbine / Induction Matrix: generation or transfer focused multiblock behavior
- Thermal Evaporation Tower: throughput, buffer, warmup
- SPS: throughput, stability, pressure, buffer, warmup

Multiblock board ownership is resolved through a shared host strategy so that different parts of the same structure read the same installed board state.

## Project Structure

- [src/main/java/com/hewiegui/overmek/capability](E:/Code/Java/OverMek/forge-1.20.1-47.4.10-mdk/src/main/java/com/hewiegui/overmek/capability): board state storage and capability attachment
- [src/main/java/com/hewiegui/overmek/config](E:/Code/Java/OverMek/forge-1.20.1-47.4.10-mdk/src/main/java/com/hewiegui/overmek/config): Forge common config definitions
- [src/main/java/com/hewiegui/overmek/item](E:/Code/Java/OverMek/forge-1.20.1-47.4.10-mdk/src/main/java/com/hewiegui/overmek/item): board item behavior and tooltip content
- [src/main/java/com/hewiegui/overmek/mixin](E:/Code/Java/OverMek/forge-1.20.1-47.4.10-mdk/src/main/java/com/hewiegui/overmek/mixin): Mekanism integration hooks
- [src/main/java/com/hewiegui/overmek/registry](E:/Code/Java/OverMek/forge-1.20.1-47.4.10-mdk/src/main/java/com/hewiegui/overmek/registry): item and creative tab registration
- [src/main/java/com/hewiegui/overmek/util](E:/Code/Java/OverMek/forge-1.20.1-47.4.10-mdk/src/main/java/com/hewiegui/overmek/util): profile loading, host resolution, display state, board services

Important runtime rule files:

- [BoardProfileLoader.java](E:/Code/Java/OverMek/forge-1.20.1-47.4.10-mdk/src/main/java/com/hewiegui/overmek/util/BoardProfileLoader.java)
- [CircuitBoardProfileHelper.java](E:/Code/Java/OverMek/forge-1.20.1-47.4.10-mdk/src/main/java/com/hewiegui/overmek/util/CircuitBoardProfileHelper.java)
- [OverMekConfig.java](E:/Code/Java/OverMek/forge-1.20.1-47.4.10-mdk/src/main/java/com/hewiegui/overmek/config/OverMekConfig.java)

## Development Setup

1. Open a terminal in the project root.
2. Run `./gradlew genIntellijRuns`.
3. Import the project into IntelliJ IDEA from `build.gradle`.
4. Refresh Gradle.
5. Use the generated `runClient` or `runServer` configurations.

## Common Commands

```bash
./gradlew build
./gradlew runClient
./gradlew runServer
./gradlew clean
```

The built mod jar is written to:

- [build/libs/overmek-1.0.1.jar](E:/Code/Java/OverMek/forge-1.20.1-47.4.10-mdk/build/libs/overmek-1.0.1.jar)

## Local Dev Notes

- JEI is included as a dev runtime dependency for recipe viewing.
- Forge certificate checks are disabled through `gradle.properties` for this local environment.
- Test jars that are not part of the release should not be left in `run/mods` when validating the base mod.
- Local-only files under `.claude/` are not part of the release.

## Configuration

OverMek uses a Forge common config file:

- `run/config/overmek-common.toml`

The config controls:

- board multipliers
- warmup timing
- generator behavior
- multiblock behavior
- allow/deny machine filters
- debug logging toggles

## Known Limits

- External addon compatibility is not part of the initial release build yet.
- No KubeJS rule layer is included yet.
- Manual in-game smoke testing is still the main validation path; there is no automated GameTest suite yet.
- The codebase still has one deprecation warning around `ModLoadingContext.get()` in [OverMek.java](E:/Code/Java/OverMek/forge-1.20.1-47.4.10-mdk/src/main/java/com/hewiegui/overmek/OverMek.java).

## License

All Rights Reserved
