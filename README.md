# OverMek

OverMek is a Minecraft Forge 1.20.1 mod that extends Mekanism with installable circuit boards.

## What It Does

- Adds four circuit board items with different tiers.
- Adds a dedicated upgrade slot to supported Mekanism machine containers.
- Applies extra processing progress on the server based on the installed board.

## Environment

- Minecraft 1.20.1
- Forge 47.4.10
- Java 17
- Mekanism 10.4.16.80

## Development Setup

1. Open a terminal in the project root.
2. Run `./gradlew genIntellijRuns`.
3. Import the project into IntelliJ IDEA using `build.gradle`.
4. Refresh Gradle.
5. Use the generated `runClient` or `runServer` configurations.

## Common Commands

```bash
./gradlew build
./gradlew runClient
./gradlew runServer
./gradlew clean
```

The built JAR is written to `build/libs/`.

## Current Architecture

- `item/`: circuit board item definitions and tooltip behavior
- `capability/`: machine-attached circuit board state
- `mixin/`: Mekanism container and machine hooks
- `registry/`: item and creative tab registration

## Notes

- Circuit board state is stored through a Forge capability attached to Mekanism block entities.
- Overclock behavior is currently implemented with reflective access to Mekanism progress fields, so version upgrades should be tested carefully.

## License

All Rights Reserved
