package com.hewiegui.overmek.config;

import java.util.List;
import net.minecraftforge.common.ForgeConfigSpec;

public final class OverMekConfig {

    public static final ForgeConfigSpec SPEC;
    public static final Common COMMON;

    static {
        ForgeConfigSpec.Builder builder = new ForgeConfigSpec.Builder();
        COMMON = new Common(builder);
        SPEC = builder.build();
    }

    private OverMekConfig() {
    }

    public static boolean isOverclockEnabled() {
        return COMMON.overclockEnabled.get();
    }

    public static boolean isFactoryOverclockEnabled() {
        return COMMON.factoryOverclockEnabled.get();
    }

    public static int getMaxOverclockBonus() {
        return COMMON.maxOverclockBonus.get();
    }

    public static boolean isWarmupEnabled() {
        return COMMON.warmupEnabled.get();
    }

    public static boolean isDebugLoggingEnabled() {
        return COMMON.debugLoggingEnabled.get();
    }

    public static double getOverclockEnergyMultiplier() {
        return COMMON.overclockEnergyMultiplier.get();
    }

    public static double getTierSpeedMultiplier(int tier) {
        return switch (tier) {
            case 0 -> COMMON.basicTierSpeedMultiplier.get();
            case 1 -> COMMON.advancedTierSpeedMultiplier.get();
            case 2 -> COMMON.eliteTierSpeedMultiplier.get();
            case 3 -> COMMON.ultimateTierSpeedMultiplier.get();
            default -> 1.0D;
        };
    }

    public static double getTierEnergyUsageFactor(int tier) {
        return switch (tier) {
            case 0 -> COMMON.basicTierEnergyUsageFactor.get();
            case 1 -> COMMON.advancedTierEnergyUsageFactor.get();
            case 2 -> COMMON.eliteTierEnergyUsageFactor.get();
            case 3 -> COMMON.ultimateTierEnergyUsageFactor.get();
            default -> 1.0D;
        };
    }

    public static double getTierFactorySpeedFactor(int tier) {
        return switch (tier) {
            case 0 -> COMMON.basicTierFactorySpeedFactor.get();
            case 1 -> COMMON.advancedTierFactorySpeedFactor.get();
            case 2 -> COMMON.eliteTierFactorySpeedFactor.get();
            case 3 -> COMMON.ultimateTierFactorySpeedFactor.get();
            default -> 1.0D;
        };
    }

    public static double getTierEnergyCapacityFactor(int tier) {
        return switch (tier) {
            case 0 -> COMMON.basicTierEnergyCapacityFactor.get();
            case 1 -> COMMON.advancedTierEnergyCapacityFactor.get();
            case 2 -> COMMON.eliteTierEnergyCapacityFactor.get();
            case 3 -> COMMON.ultimateTierEnergyCapacityFactor.get();
            default -> 1.0D;
        };
    }

    public static double getTierMaxBonus(int tier, boolean factory) {
        return switch (tier) {
            case 0 -> factory ? COMMON.basicTierFactoryMaxBonus.get() : COMMON.basicTierMaxBonus.get();
            case 1 -> factory ? COMMON.advancedTierFactoryMaxBonus.get() : COMMON.advancedTierMaxBonus.get();
            case 2 -> factory ? COMMON.eliteTierFactoryMaxBonus.get() : COMMON.eliteTierMaxBonus.get();
            case 3 -> factory ? COMMON.ultimateTierFactoryMaxBonus.get() : COMMON.ultimateTierMaxBonus.get();
            default -> 0.0D;
        };
    }

    public static int getTierWarmupTicks(int tier) {
        return switch (tier) {
            case 0 -> COMMON.basicTierWarmupTicks.get();
            case 1 -> COMMON.advancedTierWarmupTicks.get();
            case 2 -> COMMON.eliteTierWarmupTicks.get();
            case 3 -> COMMON.ultimateTierWarmupTicks.get();
            default -> 0;
        };
    }

    public static int getTierWarmupCooldown(int tier) {
        return switch (tier) {
            case 0 -> COMMON.basicTierWarmupCooldown.get();
            case 1 -> COMMON.advancedTierWarmupCooldown.get();
            case 2 -> COMMON.eliteTierWarmupCooldown.get();
            case 3 -> COMMON.ultimateTierWarmupCooldown.get();
            default -> 1;
        };
    }

    public static List<? extends String> getAllowedMachineClasses() {
        return COMMON.allowedMachineClasses.get();
    }

    public static List<? extends String> getBlockedMachineClasses() {
        return COMMON.blockedMachineClasses.get();
    }

    public static boolean isGeneratorBoardConfigEnabled() {
        return COMMON.generatorBoardProfilesEnabled.get();
    }

    public static double getGeneratorGenerationMultiplier(int tier) {
        return switch (tier) {
            case 0 -> COMMON.basicGeneratorGenerationMultiplier.get();
            case 1 -> COMMON.advancedGeneratorGenerationMultiplier.get();
            case 2 -> COMMON.eliteGeneratorGenerationMultiplier.get();
            case 3 -> COMMON.ultimateGeneratorGenerationMultiplier.get();
            default -> 1.0D;
        };
    }

    public static double getGeneratorFuelConsumptionMultiplier(int tier) {
        return switch (tier) {
            case 0 -> COMMON.basicGeneratorFuelConsumptionMultiplier.get();
            case 1 -> COMMON.advancedGeneratorFuelConsumptionMultiplier.get();
            case 2 -> COMMON.eliteGeneratorFuelConsumptionMultiplier.get();
            case 3 -> COMMON.ultimateGeneratorFuelConsumptionMultiplier.get();
            default -> 1.0D;
        };
    }

    public static int getGeneratorStartupWarmupTicks(int tier) {
        return switch (tier) {
            case 0 -> COMMON.basicGeneratorStartupWarmupTicks.get();
            case 1 -> COMMON.advancedGeneratorStartupWarmupTicks.get();
            case 2 -> COMMON.eliteGeneratorStartupWarmupTicks.get();
            case 3 -> COMMON.ultimateGeneratorStartupWarmupTicks.get();
            default -> 0;
        };
    }

    public static double getGeneratorEnergyCapacityFactor(int tier) {
        return switch (tier) {
            case 0 -> COMMON.basicGeneratorEnergyCapacityFactor.get();
            case 1 -> COMMON.advancedGeneratorEnergyCapacityFactor.get();
            case 2 -> COMMON.eliteGeneratorEnergyCapacityFactor.get();
            case 3 -> COMMON.ultimateGeneratorEnergyCapacityFactor.get();
            default -> 1.0D;
        };
    }

    public static double getFissionEfficiencyMultiplier() {
        return COMMON.fissionEfficiencyMultiplier.get();
    }

    public static double getFissionStabilityMultiplier() {
        return COMMON.fissionStabilityMultiplier.get();
    }

    public static double getFissionBufferMultiplier() {
        return COMMON.fissionBufferMultiplier.get();
    }

    public static int getFissionWarmupTicks() {
        return COMMON.fissionWarmupTicks.get();
    }

    public static int getFissionWarmupCooldown() {
        return COMMON.fissionWarmupCooldown.get();
    }

    public static double getPowerMultiblockGenerationMultiplier() {
        return COMMON.powerMultiblockGenerationMultiplier.get();
    }

    public static double getPowerMultiblockFuelMultiplier() {
        return COMMON.powerMultiblockFuelMultiplier.get();
    }

    public static double getPowerMultiblockBufferMultiplier() {
        return COMMON.powerMultiblockBufferMultiplier.get();
    }

    public static double getMatrixCapacityMultiplier() {
        return COMMON.matrixCapacityMultiplier.get();
    }

    public static double getMatrixTransferMultiplier() {
        return COMMON.matrixTransferMultiplier.get();
    }

    public static int getPowerMultiblockWarmupTicks() {
        return COMMON.powerMultiblockWarmupTicks.get();
    }

    public static int getPowerMultiblockWarmupCooldown() {
        return COMMON.powerMultiblockWarmupCooldown.get();
    }

    public static double getEvaporationThroughputMultiplier() {
        return COMMON.evaporationThroughputMultiplier.get();
    }

    public static double getEvaporationBufferMultiplier() {
        return COMMON.evaporationBufferMultiplier.get();
    }

    public static int getEvaporationWarmupTicks() {
        return COMMON.evaporationWarmupTicks.get();
    }

    public static int getEvaporationWarmupCooldown() {
        return COMMON.evaporationWarmupCooldown.get();
    }

    public static double getSpsThroughputMultiplier() {
        return COMMON.spsThroughputMultiplier.get();
    }

    public static double getSpsStabilityMultiplier() {
        return COMMON.spsStabilityMultiplier.get();
    }

    public static double getSpsBufferMultiplier() {
        return COMMON.spsBufferMultiplier.get();
    }

    public static double getSpsPressureMultiplier() {
        return COMMON.spsPressureMultiplier.get();
    }

    public static double getSpsEnergyUsageMultiplier() {
        return COMMON.spsEnergyUsageMultiplier.get();
    }

    public static int getSpsWarmupTicks() {
        return COMMON.spsWarmupTicks.get();
    }

    public static int getSpsWarmupCooldown() {
        return COMMON.spsWarmupCooldown.get();
    }

    public static final class Common {

        final ForgeConfigSpec.BooleanValue overclockEnabled;
        final ForgeConfigSpec.BooleanValue factoryOverclockEnabled;
        final ForgeConfigSpec.BooleanValue warmupEnabled;
        final ForgeConfigSpec.BooleanValue debugLoggingEnabled;
        final ForgeConfigSpec.IntValue maxOverclockBonus;
        final ForgeConfigSpec.DoubleValue overclockEnergyMultiplier;
        final ForgeConfigSpec.ConfigValue<List<? extends String>> allowedMachineClasses;
        final ForgeConfigSpec.ConfigValue<List<? extends String>> blockedMachineClasses;
        final ForgeConfigSpec.BooleanValue generatorBoardProfilesEnabled;
        final ForgeConfigSpec.DoubleValue basicTierSpeedMultiplier;
        final ForgeConfigSpec.DoubleValue advancedTierSpeedMultiplier;
        final ForgeConfigSpec.DoubleValue eliteTierSpeedMultiplier;
        final ForgeConfigSpec.DoubleValue ultimateTierSpeedMultiplier;
        final ForgeConfigSpec.DoubleValue basicTierEnergyUsageFactor;
        final ForgeConfigSpec.DoubleValue advancedTierEnergyUsageFactor;
        final ForgeConfigSpec.DoubleValue eliteTierEnergyUsageFactor;
        final ForgeConfigSpec.DoubleValue ultimateTierEnergyUsageFactor;
        final ForgeConfigSpec.DoubleValue basicTierFactorySpeedFactor;
        final ForgeConfigSpec.DoubleValue advancedTierFactorySpeedFactor;
        final ForgeConfigSpec.DoubleValue eliteTierFactorySpeedFactor;
        final ForgeConfigSpec.DoubleValue ultimateTierFactorySpeedFactor;
        final ForgeConfigSpec.DoubleValue basicTierEnergyCapacityFactor;
        final ForgeConfigSpec.DoubleValue advancedTierEnergyCapacityFactor;
        final ForgeConfigSpec.DoubleValue eliteTierEnergyCapacityFactor;
        final ForgeConfigSpec.DoubleValue ultimateTierEnergyCapacityFactor;
        final ForgeConfigSpec.DoubleValue basicTierMaxBonus;
        final ForgeConfigSpec.DoubleValue advancedTierMaxBonus;
        final ForgeConfigSpec.DoubleValue eliteTierMaxBonus;
        final ForgeConfigSpec.DoubleValue ultimateTierMaxBonus;
        final ForgeConfigSpec.DoubleValue basicTierFactoryMaxBonus;
        final ForgeConfigSpec.DoubleValue advancedTierFactoryMaxBonus;
        final ForgeConfigSpec.DoubleValue eliteTierFactoryMaxBonus;
        final ForgeConfigSpec.DoubleValue ultimateTierFactoryMaxBonus;
        final ForgeConfigSpec.IntValue basicTierWarmupTicks;
        final ForgeConfigSpec.IntValue advancedTierWarmupTicks;
        final ForgeConfigSpec.IntValue eliteTierWarmupTicks;
        final ForgeConfigSpec.IntValue ultimateTierWarmupTicks;
        final ForgeConfigSpec.IntValue basicTierWarmupCooldown;
        final ForgeConfigSpec.IntValue advancedTierWarmupCooldown;
        final ForgeConfigSpec.IntValue eliteTierWarmupCooldown;
        final ForgeConfigSpec.IntValue ultimateTierWarmupCooldown;
        final ForgeConfigSpec.DoubleValue basicGeneratorGenerationMultiplier;
        final ForgeConfigSpec.DoubleValue advancedGeneratorGenerationMultiplier;
        final ForgeConfigSpec.DoubleValue eliteGeneratorGenerationMultiplier;
        final ForgeConfigSpec.DoubleValue ultimateGeneratorGenerationMultiplier;
        final ForgeConfigSpec.DoubleValue basicGeneratorFuelConsumptionMultiplier;
        final ForgeConfigSpec.DoubleValue advancedGeneratorFuelConsumptionMultiplier;
        final ForgeConfigSpec.DoubleValue eliteGeneratorFuelConsumptionMultiplier;
        final ForgeConfigSpec.DoubleValue ultimateGeneratorFuelConsumptionMultiplier;
        final ForgeConfigSpec.IntValue basicGeneratorStartupWarmupTicks;
        final ForgeConfigSpec.IntValue advancedGeneratorStartupWarmupTicks;
        final ForgeConfigSpec.IntValue eliteGeneratorStartupWarmupTicks;
        final ForgeConfigSpec.IntValue ultimateGeneratorStartupWarmupTicks;
        final ForgeConfigSpec.DoubleValue basicGeneratorEnergyCapacityFactor;
        final ForgeConfigSpec.DoubleValue advancedGeneratorEnergyCapacityFactor;
        final ForgeConfigSpec.DoubleValue eliteGeneratorEnergyCapacityFactor;
        final ForgeConfigSpec.DoubleValue ultimateGeneratorEnergyCapacityFactor;
        final ForgeConfigSpec.DoubleValue fissionEfficiencyMultiplier;
        final ForgeConfigSpec.DoubleValue fissionStabilityMultiplier;
        final ForgeConfigSpec.DoubleValue fissionBufferMultiplier;
        final ForgeConfigSpec.IntValue fissionWarmupTicks;
        final ForgeConfigSpec.IntValue fissionWarmupCooldown;
        final ForgeConfigSpec.DoubleValue powerMultiblockGenerationMultiplier;
        final ForgeConfigSpec.DoubleValue powerMultiblockFuelMultiplier;
        final ForgeConfigSpec.DoubleValue powerMultiblockBufferMultiplier;
        final ForgeConfigSpec.DoubleValue matrixCapacityMultiplier;
        final ForgeConfigSpec.DoubleValue matrixTransferMultiplier;
        final ForgeConfigSpec.IntValue powerMultiblockWarmupTicks;
        final ForgeConfigSpec.IntValue powerMultiblockWarmupCooldown;
        final ForgeConfigSpec.DoubleValue evaporationThroughputMultiplier;
        final ForgeConfigSpec.DoubleValue evaporationBufferMultiplier;
        final ForgeConfigSpec.IntValue evaporationWarmupTicks;
        final ForgeConfigSpec.IntValue evaporationWarmupCooldown;
        final ForgeConfigSpec.DoubleValue spsThroughputMultiplier;
        final ForgeConfigSpec.DoubleValue spsStabilityMultiplier;
        final ForgeConfigSpec.DoubleValue spsBufferMultiplier;
        final ForgeConfigSpec.DoubleValue spsPressureMultiplier;
        final ForgeConfigSpec.DoubleValue spsEnergyUsageMultiplier;
        final ForgeConfigSpec.IntValue spsWarmupTicks;
        final ForgeConfigSpec.IntValue spsWarmupCooldown;

        Common(ForgeConfigSpec.Builder builder) {
            builder.push("overclock");

            overclockEnabled = builder
                .comment("Whether circuit boards can accelerate Mekanism machines at all.")
                .define("enabled", true);

            factoryOverclockEnabled = builder
                .comment("Whether Mekanism factories can benefit from circuit board overclocking.")
                .define("factoryEnabled", true);

            warmupEnabled = builder
                .comment("Whether circuit boards need to warm up under sustained operation before reaching full speed.")
                .define("warmupEnabled", true);

            debugLoggingEnabled = builder
                .comment("Whether verbose OverMek debug logging should be emitted to the console.")
                .define("debugLoggingEnabled", false);

            maxOverclockBonus = builder
                .comment("Caps the effective overclock bonus before it is converted into faster processing.")
                .defineInRange("maxBonus", 10, 0, 64);

            overclockEnergyMultiplier = builder
                .comment("Global multiplier applied to circuit board energy draw after all tier adjustments.")
                .defineInRange("energyMultiplier", 1.5D, 0.0D, 64.0D);

            builder.push("machineFilters");

            allowedMachineClasses = builder
                .comment(
                    "Optional whitelist for machines that can expose and use circuit board slots.",
                    "Leave empty to allow every supported Mekanism processing machine.",
                    "Entries may be full class names, simple class names, or wildcard patterns such as mekanism.common.tile.machine.*"
                )
                .defineListAllowEmpty(List.of("allowList"), List::of, value -> value instanceof String);

            blockedMachineClasses = builder
                .comment(
                    "Optional blacklist for machines that should never expose or use circuit board slots.",
                    "Blacklist entries override the whitelist.",
                    "Entries may be full class names, simple class names, or wildcard patterns."
                )
                .defineListAllowEmpty(List.of("denyList"), List::of, value -> value instanceof String);

            builder.pop();
            builder.push("tierProfiles");

            builder.push("basic");

            basicTierSpeedMultiplier = builder
                .comment("Speed scaling applied to the basic circuit board's base overclock count.")
                .defineInRange("speedMultiplier", 1.0D, 0.0D, 16.0D);

            basicTierEnergyUsageFactor = builder
                .comment("Energy profile applied after speed is calculated. 1.0 keeps total recipe cost roughly neutral.")
                .defineInRange("energyUsageFactor", 1.0D, 0.0D, 16.0D);

            basicTierFactorySpeedFactor = builder
                .comment("Extra speed factor that only applies when the board is installed in a factory.")
                .defineInRange("factorySpeedFactor", 1.0D, 0.0D, 16.0D);

            basicTierEnergyCapacityFactor = builder
                .comment("Energy storage multiplier applied while the basic board is installed.")
                .defineInRange("energyCapacityFactor", 1.5D, 1.0D, 64.0D);

            basicTierMaxBonus = builder
                .comment("Hard cap for the basic board's speed bonus before the global cap applies.")
                .defineInRange("maxBonus", 0.75D, 0.0D, 64.0D);

            basicTierFactoryMaxBonus = builder
                .comment("Factory-only cap for the basic board. Keep it close to maxBonus to preserve its gentle role.")
                .defineInRange("factoryMaxBonus", 0.75D, 0.0D, 64.0D);

            basicTierWarmupTicks = builder
                .comment("Ticks of sustained activity the basic board needs to reach full performance.")
                .defineInRange("warmupTicks", 480, 0, 24_000);

            basicTierWarmupCooldown = builder
                .comment("How many warmup ticks the basic board loses per idle tick.")
                .defineInRange("warmupCooldown", 4, 1, 1_000);

            builder.pop();
            builder.push("advanced");

            advancedTierSpeedMultiplier = builder
                .comment("Speed scaling applied to the advanced circuit board's base overclock count.")
                .defineInRange("speedMultiplier", 1.5D, 0.0D, 16.0D);

            advancedTierEnergyUsageFactor = builder
                .comment("Energy profile applied after speed is calculated. Values above 1.0 trade efficiency for throughput.")
                .defineInRange("energyUsageFactor", 1.1D, 0.0D, 16.0D);

            advancedTierFactorySpeedFactor = builder
                .comment("Extra speed factor that only applies when the board is installed in a factory.")
                .defineInRange("factorySpeedFactor", 1.0D, 0.0D, 16.0D);

            advancedTierEnergyCapacityFactor = builder
                .comment("Energy storage multiplier applied while the advanced board is installed.")
                .defineInRange("energyCapacityFactor", 2.25D, 1.0D, 64.0D);

            advancedTierMaxBonus = builder
                .comment("Hard cap for the advanced board's speed bonus.")
                .defineInRange("maxBonus", 2.5D, 0.0D, 64.0D);

            advancedTierFactoryMaxBonus = builder
                .comment("Factory-only cap for the advanced board.")
                .defineInRange("factoryMaxBonus", 2.5D, 0.0D, 64.0D);

            advancedTierWarmupTicks = builder
                .comment("Ticks of sustained activity the advanced board needs to reach full performance.")
                .defineInRange("warmupTicks", 720, 0, 24_000);

            advancedTierWarmupCooldown = builder
                .comment("How many warmup ticks the advanced board loses per idle tick.")
                .defineInRange("warmupCooldown", 3, 1, 1_000);

            builder.pop();
            builder.push("elite");

            eliteTierSpeedMultiplier = builder
                .comment("Speed scaling applied to the elite circuit board's base overclock count.")
                .defineInRange("speedMultiplier", 2.0D, 0.0D, 16.0D);

            eliteTierEnergyUsageFactor = builder
                .comment("Energy profile applied after speed is calculated. Values below 1.0 make elite boards more efficient per recipe.")
                .defineInRange("energyUsageFactor", 0.8D, 0.0D, 16.0D);

            eliteTierFactorySpeedFactor = builder
                .comment("Extra speed factor that only applies when the board is installed in a factory.")
                .defineInRange("factorySpeedFactor", 1.0D, 0.0D, 16.0D);

            eliteTierEnergyCapacityFactor = builder
                .comment("Energy storage multiplier applied while the elite board is installed.")
                .defineInRange("energyCapacityFactor", 3.5D, 1.0D, 64.0D);

            eliteTierMaxBonus = builder
                .comment("Hard cap for the elite board's speed bonus.")
                .defineInRange("maxBonus", 3.5D, 0.0D, 64.0D);

            eliteTierFactoryMaxBonus = builder
                .comment("Factory-only cap for the elite board.")
                .defineInRange("factoryMaxBonus", 3.5D, 0.0D, 64.0D);

            eliteTierWarmupTicks = builder
                .comment("Ticks of sustained activity the elite board needs to reach full performance.")
                .defineInRange("warmupTicks", 960, 0, 24_000);

            eliteTierWarmupCooldown = builder
                .comment("How many warmup ticks the elite board loses per idle tick.")
                .defineInRange("warmupCooldown", 2, 1, 1_000);

            builder.pop();
            builder.push("ultimate");

            ultimateTierSpeedMultiplier = builder
                .comment("Speed scaling applied to the ultimate circuit board's base overclock count.")
                .defineInRange("speedMultiplier", 3.0D, 0.0D, 16.0D);

            ultimateTierEnergyUsageFactor = builder
                .comment("Energy profile applied after speed is calculated. Lower values make the ultimate board the most efficient.")
                .defineInRange("energyUsageFactor", 0.65D, 0.0D, 16.0D);

            ultimateTierFactorySpeedFactor = builder
                .comment("Extra speed factor that only applies when the board is installed in a factory, giving the ultimate board a factory-focused niche.")
                .defineInRange("factorySpeedFactor", 1.35D, 0.0D, 16.0D);

            ultimateTierEnergyCapacityFactor = builder
                .comment("Energy storage multiplier applied while the ultimate board is installed.")
                .defineInRange("energyCapacityFactor", 5.0D, 1.0D, 64.0D);

            ultimateTierMaxBonus = builder
                .comment("Hard cap for the ultimate board's base speed bonus.")
                .defineInRange("maxBonus", 4.5D, 0.0D, 64.0D);

            ultimateTierFactoryMaxBonus = builder
                .comment("Factory-only cap for the ultimate board, enabling its extra throughput specialization.")
                .defineInRange("factoryMaxBonus", 6.5D, 0.0D, 64.0D);

            ultimateTierWarmupTicks = builder
                .comment("Ticks of sustained activity the ultimate board needs to reach full performance.")
                .defineInRange("warmupTicks", 1200, 0, 24_000);

            ultimateTierWarmupCooldown = builder
                .comment("How many warmup ticks the ultimate board loses per idle tick.")
                .defineInRange("warmupCooldown", 1, 1, 1_000);

            builder.pop();
            builder.pop();

            builder.push("generatorBoards");

            generatorBoardProfilesEnabled = builder
                .comment("Whether circuit board effects should apply to Mekanism single-block generators.")
                .define("enabled", true);

            builder.push("basic");
            basicGeneratorGenerationMultiplier = builder.defineInRange("generationMultiplier", 1.4D, 1.0D, 64.0D);
            basicGeneratorFuelConsumptionMultiplier = builder.defineInRange("fuelConsumptionMultiplier", 1.65D, 1.0D, 64.0D);
            basicGeneratorStartupWarmupTicks = builder.defineInRange("startupWarmupTicks", 100, 0, 24_000);
            basicGeneratorEnergyCapacityFactor = builder.defineInRange("energyCapacityFactor", 1.5D, 1.0D, 64.0D);
            builder.pop();

            builder.push("advanced");
            advancedGeneratorGenerationMultiplier = builder.defineInRange("generationMultiplier", 2.0D, 1.0D, 64.0D);
            advancedGeneratorFuelConsumptionMultiplier = builder.defineInRange("fuelConsumptionMultiplier", 2.4D, 1.0D, 64.0D);
            advancedGeneratorStartupWarmupTicks = builder.defineInRange("startupWarmupTicks", 150, 0, 24_000);
            advancedGeneratorEnergyCapacityFactor = builder.defineInRange("energyCapacityFactor", 2.2D, 1.0D, 64.0D);
            builder.pop();

            builder.push("elite");
            eliteGeneratorGenerationMultiplier = builder.defineInRange("generationMultiplier", 2.6D, 1.0D, 64.0D);
            eliteGeneratorFuelConsumptionMultiplier = builder.defineInRange("fuelConsumptionMultiplier", 3.0D, 1.0D, 64.0D);
            eliteGeneratorStartupWarmupTicks = builder.defineInRange("startupWarmupTicks", 210, 0, 24_000);
            eliteGeneratorEnergyCapacityFactor = builder.defineInRange("energyCapacityFactor", 3.0D, 1.0D, 64.0D);
            builder.pop();

            builder.push("ultimate");
            ultimateGeneratorGenerationMultiplier = builder.defineInRange("generationMultiplier", 3.4D, 1.0D, 64.0D);
            ultimateGeneratorFuelConsumptionMultiplier = builder.defineInRange("fuelConsumptionMultiplier", 4.1D, 1.0D, 64.0D);
            ultimateGeneratorStartupWarmupTicks = builder.defineInRange("startupWarmupTicks", 280, 0, 24_000);
            ultimateGeneratorEnergyCapacityFactor = builder.defineInRange("energyCapacityFactor", 4.25D, 1.0D, 64.0D);
            builder.pop();

            builder.pop();

            builder.push("multiblock");

            builder.push("fission");
            fissionEfficiencyMultiplier = builder.defineInRange("efficiencyMultiplier", 2.2D, 1.0D, 64.0D);
            fissionStabilityMultiplier = builder.defineInRange("stabilityMultiplier", 2.7D, 1.0D, 64.0D);
            fissionBufferMultiplier = builder.defineInRange("bufferMultiplier", 3.8D, 1.0D, 64.0D);
            fissionWarmupTicks = builder.defineInRange("warmupTicks", 260, 0, 24_000);
            fissionWarmupCooldown = builder.defineInRange("warmupCooldown", 2, 1, 1_000);
            builder.pop();

            builder.push("power");
            powerMultiblockGenerationMultiplier = builder.defineInRange("generationMultiplier", 3.0D, 1.0D, 64.0D);
            powerMultiblockFuelMultiplier = builder.defineInRange("fuelMultiplier", 3.6D, 1.0D, 64.0D);
            powerMultiblockBufferMultiplier = builder.defineInRange("bufferMultiplier", 4.2D, 1.0D, 64.0D);
            matrixCapacityMultiplier = builder.defineInRange("matrixCapacityMultiplier", 5.8D, 1.0D, 64.0D);
            matrixTransferMultiplier = builder.defineInRange("matrixTransferMultiplier", 4.2D, 1.0D, 64.0D);
            powerMultiblockWarmupTicks = builder.defineInRange("warmupTicks", 220, 0, 24_000);
            powerMultiblockWarmupCooldown = builder.defineInRange("warmupCooldown", 2, 1, 1_000);
            builder.pop();

            builder.push("evaporation");
            evaporationThroughputMultiplier = builder.defineInRange("throughputMultiplier", 3.8D, 1.0D, 64.0D);
            evaporationBufferMultiplier = builder.defineInRange("bufferMultiplier", 3.2D, 1.0D, 64.0D);
            evaporationWarmupTicks = builder.defineInRange("warmupTicks", 180, 0, 24_000);
            evaporationWarmupCooldown = builder.defineInRange("warmupCooldown", 2, 1, 1_000);
            builder.pop();

            builder.push("sps");
            spsThroughputMultiplier = builder.defineInRange("throughputMultiplier", 4.6D, 1.0D, 64.0D);
            spsStabilityMultiplier = builder.defineInRange("stabilityMultiplier", 2.8D, 1.0D, 64.0D);
            spsBufferMultiplier = builder.defineInRange("bufferMultiplier", 4.8D, 1.0D, 64.0D);
            spsPressureMultiplier = builder.defineInRange("pressureMultiplier", 4.2D, 1.0D, 64.0D);
            spsEnergyUsageMultiplier = builder.defineInRange("energyUsageMultiplier", 0.68D, 0.05D, 64.0D);
            spsWarmupTicks = builder.defineInRange("warmupTicks", 3200, 0, 24_000);
            spsWarmupCooldown = builder.defineInRange("warmupCooldown", 2, 1, 1_000);
            builder.pop();

            builder.pop();
        }
    }
}
