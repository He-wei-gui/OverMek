package com.hewiegui.overmek.util;

import com.hewiegui.overmek.config.OverMekConfig;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.Map;
import net.minecraft.world.item.ItemStack;

public final class BoardProfileLoader {

    private static final BoardEffectProfile[] PROCESSING_PROFILES = new BoardEffectProfile[4];
    private static final BoardEffectProfile[] GENERATOR_PROFILES = new BoardEffectProfile[4];
    private static final Map<CircuitBoardMachineProfile, BoardEffectProfile> MULTIBLOCK_PROFILES = new EnumMap<>(CircuitBoardMachineProfile.class);
    private static volatile boolean loaded;

    static {
        loadDefaults();
    }

    private BoardProfileLoader() {
    }

    public static void reload() {
        Arrays.setAll(PROCESSING_PROFILES, BoardProfileLoader::createProcessingProfile);
        Arrays.setAll(GENERATOR_PROFILES, BoardProfileLoader::createGeneratorProfile);

        MULTIBLOCK_PROFILES.clear();
        MULTIBLOCK_PROFILES.put(CircuitBoardMachineProfile.FISSION, new BoardEffectProfile(
            CircuitBoardChannel.FISSION, 0, OverMekConfig.getFissionEfficiencyMultiplier(), 1.0D, 1.0D, 1.0D, 0.0D, 0.0D, 1.0D, 1.0D,
            OverMekConfig.getFissionBufferMultiplier(), 1.0D, 1.0D, 1.0D, OverMekConfig.getFissionStabilityMultiplier(), 1.0D,
            OverMekConfig.getFissionWarmupTicks(), OverMekConfig.getFissionWarmupCooldown()
        ));
        MULTIBLOCK_PROFILES.put(CircuitBoardMachineProfile.POWER_MULTIBLOCK, new BoardEffectProfile(
            CircuitBoardChannel.POWER_MULTIBLOCK, 0, 1.0D, 1.0D, 1.0D, 1.0D, 0.0D, 0.0D,
            OverMekConfig.getPowerMultiblockGenerationMultiplier(), OverMekConfig.getPowerMultiblockFuelMultiplier(),
            OverMekConfig.getPowerMultiblockBufferMultiplier(), OverMekConfig.getMatrixCapacityMultiplier(), OverMekConfig.getMatrixTransferMultiplier(),
            1.0D, 1.0D, 1.0D, OverMekConfig.getPowerMultiblockWarmupTicks(), OverMekConfig.getPowerMultiblockWarmupCooldown()
        ));
        MULTIBLOCK_PROFILES.put(CircuitBoardMachineProfile.EVAPORATION_MULTIBLOCK, new BoardEffectProfile(
            CircuitBoardChannel.EVAPORATION_MULTIBLOCK, 0, 1.0D, 1.0D, 1.0D, 1.0D, 0.0D, 0.0D,
            1.0D, 1.0D, OverMekConfig.getEvaporationBufferMultiplier(), 1.0D, 1.0D, OverMekConfig.getEvaporationThroughputMultiplier(), 1.0D, 1.0D,
            OverMekConfig.getEvaporationWarmupTicks(), OverMekConfig.getEvaporationWarmupCooldown()
        ));
        MULTIBLOCK_PROFILES.put(CircuitBoardMachineProfile.SPS_MULTIBLOCK, new BoardEffectProfile(
            CircuitBoardChannel.SPS_MULTIBLOCK, 0, 1.0D, OverMekConfig.getSpsEnergyUsageMultiplier(), 1.0D, 1.0D, 0.0D, 0.0D,
            1.0D, 1.0D, OverMekConfig.getSpsBufferMultiplier(), 1.0D, 1.0D, OverMekConfig.getSpsThroughputMultiplier(),
            OverMekConfig.getSpsStabilityMultiplier(), OverMekConfig.getSpsPressureMultiplier(),
            OverMekConfig.getSpsWarmupTicks(), OverMekConfig.getSpsWarmupCooldown()
        ));
        loaded = true;
    }

    public static BoardEffectProfile getProfile(CircuitBoardMachineProfile machineProfile, ItemStack stack) {
        ensureLoaded();
        return switch (machineProfile) {
            case PROCESSING -> getProcessingProfile(CircuitBoardOverclockHelper.getCircuitBoardTier(stack));
            case GENERATOR -> getGeneratorProfile(CircuitBoardOverclockHelper.getCircuitBoardTier(stack));
            case FISSION, POWER_MULTIBLOCK, EVAPORATION_MULTIBLOCK, SPS_MULTIBLOCK ->
                MULTIBLOCK_PROFILES.getOrDefault(machineProfile, BoardEffectProfile.unsupported());
            default -> BoardEffectProfile.unsupported();
        };
    }

    public static BoardEffectProfile getInstalledProfile(MachineSupportProfile supportProfile, ItemStack stack) {
        if (supportProfile == null || !supportProfile.isSupported()) {
            return BoardEffectProfile.unsupported();
        }
        return getProfile(supportProfile.machineProfile(), stack);
    }

    public static BoardEffectProfile getProcessingProfile(int tier) {
        ensureLoaded();
        return tier >= 0 && tier < PROCESSING_PROFILES.length ? PROCESSING_PROFILES[tier] : BoardEffectProfile.unsupported();
    }

    public static BoardEffectProfile getGeneratorProfile(int tier) {
        ensureLoaded();
        return tier >= 0 && tier < GENERATOR_PROFILES.length ? GENERATOR_PROFILES[tier] : BoardEffectProfile.unsupported();
    }

    public static BoardEffectProfile getMultiblockProfile(CircuitBoardMachineProfile machineProfile) {
        ensureLoaded();
        return MULTIBLOCK_PROFILES.getOrDefault(machineProfile, BoardEffectProfile.unsupported());
    }

    private static void ensureLoaded() {
        if (!loaded) {
            loadDefaults();
        }
    }

    private static void loadDefaults() {
        PROCESSING_PROFILES[0] = new BoardEffectProfile(CircuitBoardChannel.STANDARD, 0, 1.0D, 1.0D, 1.0D, 1.5D, 0.75D, 0.75D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 480, 4);
        PROCESSING_PROFILES[1] = new BoardEffectProfile(CircuitBoardChannel.STANDARD, 1, 1.5D, 1.1D, 1.0D, 2.25D, 2.5D, 2.5D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 720, 3);
        PROCESSING_PROFILES[2] = new BoardEffectProfile(CircuitBoardChannel.STANDARD, 2, 2.0D, 0.8D, 1.0D, 3.5D, 3.5D, 3.5D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 960, 2);
        PROCESSING_PROFILES[3] = new BoardEffectProfile(CircuitBoardChannel.STANDARD, 3, 3.0D, 0.65D, 1.35D, 5.0D, 4.5D, 6.5D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1200, 1);

        GENERATOR_PROFILES[0] = new BoardEffectProfile(CircuitBoardChannel.STANDARD, 0, 1.0D, 1.0D, 1.0D, 1.5D, 0.0D, 0.0D, 1.4D, 1.65D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 100, 1);
        GENERATOR_PROFILES[1] = new BoardEffectProfile(CircuitBoardChannel.STANDARD, 1, 1.0D, 1.0D, 1.0D, 2.2D, 0.0D, 0.0D, 2.0D, 2.4D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 150, 1);
        GENERATOR_PROFILES[2] = new BoardEffectProfile(CircuitBoardChannel.STANDARD, 2, 1.0D, 1.0D, 1.0D, 3.0D, 0.0D, 0.0D, 2.6D, 3.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 210, 1);
        GENERATOR_PROFILES[3] = new BoardEffectProfile(CircuitBoardChannel.STANDARD, 3, 1.0D, 1.0D, 1.0D, 4.25D, 0.0D, 0.0D, 3.4D, 4.1D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 280, 1);

        MULTIBLOCK_PROFILES.clear();
        MULTIBLOCK_PROFILES.put(CircuitBoardMachineProfile.FISSION, new BoardEffectProfile(CircuitBoardChannel.FISSION, 0, 2.2D, 1.0D, 1.0D, 1.0D, 0.0D, 0.0D, 1.0D, 1.0D, 3.8D, 1.0D, 1.0D, 1.0D, 2.7D, 1.0D, 260, 2));
        MULTIBLOCK_PROFILES.put(CircuitBoardMachineProfile.POWER_MULTIBLOCK, new BoardEffectProfile(CircuitBoardChannel.POWER_MULTIBLOCK, 0, 1.0D, 1.0D, 1.0D, 1.0D, 0.0D, 0.0D, 3.0D, 3.6D, 4.2D, 5.8D, 4.2D, 1.0D, 1.0D, 1.0D, 220, 2));
        MULTIBLOCK_PROFILES.put(CircuitBoardMachineProfile.EVAPORATION_MULTIBLOCK, new BoardEffectProfile(CircuitBoardChannel.EVAPORATION_MULTIBLOCK, 0, 1.0D, 1.0D, 1.0D, 1.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 5.0D, 1.0D, 1.0D, 1200, 1));
        MULTIBLOCK_PROFILES.put(CircuitBoardMachineProfile.SPS_MULTIBLOCK, new BoardEffectProfile(CircuitBoardChannel.SPS_MULTIBLOCK, 0, 1.0D, 0.68D, 1.0D, 1.0D, 0.0D, 0.0D, 1.0D, 1.0D, 4.8D, 1.0D, 1.0D, 4.6D, 2.8D, 4.2D, 3200, 2));
        loaded = true;
    }

    private static BoardEffectProfile createProcessingProfile(int tier) {
        return new BoardEffectProfile(
            CircuitBoardChannel.STANDARD,
            tier,
            OverMekConfig.getTierSpeedMultiplier(tier),
            OverMekConfig.getTierEnergyUsageFactor(tier),
            OverMekConfig.getTierFactorySpeedFactor(tier),
            OverMekConfig.getTierEnergyCapacityFactor(tier),
            OverMekConfig.getTierMaxBonus(tier, false),
            OverMekConfig.getTierMaxBonus(tier, true),
            1.0D,
            1.0D,
            1.0D,
            1.0D,
            1.0D,
            1.0D,
            1.0D,
            1.0D,
            OverMekConfig.getTierWarmupTicks(tier),
            OverMekConfig.getTierWarmupCooldown(tier)
        );
    }

    private static BoardEffectProfile createGeneratorProfile(int tier) {
        return new BoardEffectProfile(
            CircuitBoardChannel.STANDARD,
            tier,
            1.0D,
            1.0D,
            1.0D,
            OverMekConfig.getGeneratorEnergyCapacityFactor(tier),
            0.0D,
            0.0D,
            OverMekConfig.getGeneratorGenerationMultiplier(tier),
            OverMekConfig.getGeneratorFuelConsumptionMultiplier(tier),
            1.0D,
            1.0D,
            1.0D,
            1.0D,
            1.0D,
            1.0D,
            OverMekConfig.getGeneratorStartupWarmupTicks(tier),
            1
        );
    }
}
