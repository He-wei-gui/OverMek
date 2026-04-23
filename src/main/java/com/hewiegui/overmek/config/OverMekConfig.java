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

    public static double getTierMaxBonus(int tier, boolean factory) {
        return switch (tier) {
            case 0 -> factory ? COMMON.basicTierFactoryMaxBonus.get() : COMMON.basicTierMaxBonus.get();
            case 1 -> factory ? COMMON.advancedTierFactoryMaxBonus.get() : COMMON.advancedTierMaxBonus.get();
            case 2 -> factory ? COMMON.eliteTierFactoryMaxBonus.get() : COMMON.eliteTierMaxBonus.get();
            case 3 -> factory ? COMMON.ultimateTierFactoryMaxBonus.get() : COMMON.ultimateTierMaxBonus.get();
            default -> 0.0D;
        };
    }

    public static List<? extends String> getAllowedMachineClasses() {
        return COMMON.allowedMachineClasses.get();
    }

    public static List<? extends String> getBlockedMachineClasses() {
        return COMMON.blockedMachineClasses.get();
    }

    public static final class Common {

        final ForgeConfigSpec.BooleanValue overclockEnabled;
        final ForgeConfigSpec.BooleanValue factoryOverclockEnabled;
        final ForgeConfigSpec.IntValue maxOverclockBonus;
        final ForgeConfigSpec.ConfigValue<List<? extends String>> allowedMachineClasses;
        final ForgeConfigSpec.ConfigValue<List<? extends String>> blockedMachineClasses;
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
        final ForgeConfigSpec.DoubleValue basicTierMaxBonus;
        final ForgeConfigSpec.DoubleValue advancedTierMaxBonus;
        final ForgeConfigSpec.DoubleValue eliteTierMaxBonus;
        final ForgeConfigSpec.DoubleValue ultimateTierMaxBonus;
        final ForgeConfigSpec.DoubleValue basicTierFactoryMaxBonus;
        final ForgeConfigSpec.DoubleValue advancedTierFactoryMaxBonus;
        final ForgeConfigSpec.DoubleValue eliteTierFactoryMaxBonus;
        final ForgeConfigSpec.DoubleValue ultimateTierFactoryMaxBonus;

        Common(ForgeConfigSpec.Builder builder) {
            builder.push("overclock");

            overclockEnabled = builder
                .comment("Whether circuit boards can accelerate Mekanism machines at all.")
                .define("enabled", true);

            factoryOverclockEnabled = builder
                .comment("Whether Mekanism factories can benefit from circuit board overclocking.")
                .define("factoryEnabled", true);

            maxOverclockBonus = builder
                .comment("Caps the effective overclock bonus before it is converted into faster processing.")
                .defineInRange("maxBonus", 10, 0, 64);

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

            basicTierMaxBonus = builder
                .comment("Hard cap for the basic board's speed bonus before the global cap applies.")
                .defineInRange("maxBonus", 0.75D, 0.0D, 64.0D);

            basicTierFactoryMaxBonus = builder
                .comment("Factory-only cap for the basic board. Keep it close to maxBonus to preserve its gentle role.")
                .defineInRange("factoryMaxBonus", 0.75D, 0.0D, 64.0D);

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

            advancedTierMaxBonus = builder
                .comment("Hard cap for the advanced board's speed bonus.")
                .defineInRange("maxBonus", 2.5D, 0.0D, 64.0D);

            advancedTierFactoryMaxBonus = builder
                .comment("Factory-only cap for the advanced board.")
                .defineInRange("factoryMaxBonus", 2.5D, 0.0D, 64.0D);

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

            eliteTierMaxBonus = builder
                .comment("Hard cap for the elite board's speed bonus.")
                .defineInRange("maxBonus", 3.5D, 0.0D, 64.0D);

            eliteTierFactoryMaxBonus = builder
                .comment("Factory-only cap for the elite board.")
                .defineInRange("factoryMaxBonus", 3.5D, 0.0D, 64.0D);

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

            ultimateTierMaxBonus = builder
                .comment("Hard cap for the ultimate board's base speed bonus.")
                .defineInRange("maxBonus", 4.5D, 0.0D, 64.0D);

            ultimateTierFactoryMaxBonus = builder
                .comment("Factory-only cap for the ultimate board, enabling its extra throughput specialization.")
                .defineInRange("factoryMaxBonus", 6.5D, 0.0D, 64.0D);

            builder.pop();
            builder.pop();
        }
    }
}
