package com.hewiegui.overmek.config;

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

    public static double getTierMultiplier(int tier) {
        return switch (tier) {
            case 0 -> COMMON.basicTierMultiplier.get();
            case 1 -> COMMON.advancedTierMultiplier.get();
            case 2 -> COMMON.eliteTierMultiplier.get();
            case 3 -> COMMON.ultimateTierMultiplier.get();
            default -> 1.0D;
        };
    }

    public static final class Common {

        final ForgeConfigSpec.BooleanValue overclockEnabled;
        final ForgeConfigSpec.BooleanValue factoryOverclockEnabled;
        final ForgeConfigSpec.IntValue maxOverclockBonus;
        final ForgeConfigSpec.DoubleValue basicTierMultiplier;
        final ForgeConfigSpec.DoubleValue advancedTierMultiplier;
        final ForgeConfigSpec.DoubleValue eliteTierMultiplier;
        final ForgeConfigSpec.DoubleValue ultimateTierMultiplier;

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

            builder.push("tierMultipliers");

            basicTierMultiplier = builder
                .comment("Multiplier applied to the basic circuit board overclock count.")
                .defineInRange("basic", 1.0D, 0.0D, 16.0D);

            advancedTierMultiplier = builder
                .comment("Multiplier applied to the advanced circuit board overclock count.")
                .defineInRange("advanced", 1.5D, 0.0D, 16.0D);

            eliteTierMultiplier = builder
                .comment("Multiplier applied to the elite circuit board overclock count.")
                .defineInRange("elite", 2.0D, 0.0D, 16.0D);

            ultimateTierMultiplier = builder
                .comment("Multiplier applied to the ultimate circuit board overclock count.")
                .defineInRange("ultimate", 3.0D, 0.0D, 16.0D);

            builder.pop();
            builder.pop();
        }
    }
}
