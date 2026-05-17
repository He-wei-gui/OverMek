package com.hewiegui.overmek.util;

import com.hewiegui.overmek.config.OverMekConfig;
import java.util.Collections;
import java.util.Map;
import java.util.WeakHashMap;
import mekanism.common.tile.base.TileEntityMekanism;
import net.minecraft.world.item.ItemStack;

public final class MultiblockBoardService {

    private static final Map<TileEntityMekanism, BoardEffectProfile> LAST_GOOD_PROFILE =
        Collections.synchronizedMap(new WeakHashMap<>());

    private MultiblockBoardService() {
    }

    public static void invalidateProfileCache(TileEntityMekanism tile) {
        if (tile != null) {
            LAST_GOOD_PROFILE.remove(tile);
        }
    }

    public static void clearProfileCache() {
        LAST_GOOD_PROFILE.clear();
    }

    public static double getDisplayedFissionEfficiencyMultiplier(TileEntityMekanism tile, ItemStack stack) {
        BoardEffectProfile profile = BoardProfileLoader.getInstalledProfile(CircuitBoardProfileHelper.getSupportProfile(tile), stack);
        return profile.isSupported() ? profile.speedMultiplier() : 1.0D;
    }

    public static double getDisplayedCoolingMultiplier(TileEntityMekanism tile, ItemStack stack) {
        return getDisplayedFissionEfficiencyMultiplier(tile, stack);
    }

    public static double getDisplayedFissionStabilityMultiplier(TileEntityMekanism tile, ItemStack stack) {
        BoardEffectProfile profile = BoardProfileLoader.getInstalledProfile(CircuitBoardProfileHelper.getSupportProfile(tile), stack);
        return profile.isSupported() ? profile.stabilityMultiplier() : 1.0D;
    }

    public static double getEffectiveFissionEfficiencyMultiplier(TileEntityMekanism tile) {
        BoardEffectProfile profile = getInstalledProfile(tile);
        double result = profile.isSupported() ? profile.speedMultiplier() : 1.0D;
        OverMekDebug.logMultiblockEffects(tile, "fission-efficiency", "efficiency=" + result);
        return result;
    }

    public static double getEffectiveFissionStabilityMultiplier(TileEntityMekanism tile) {
        BoardEffectProfile profile = getInstalledProfile(tile);
        double result = profile.isSupported() ? profile.stabilityMultiplier() : 1.0D;
        OverMekDebug.logMultiblockEffects(tile, "fission-stability", "stability=" + result);
        return result;
    }

    public static double getDisplayedPowerGenerationMultiplier(TileEntityMekanism tile, ItemStack stack) {
        BoardEffectProfile profile = BoardProfileLoader.getInstalledProfile(CircuitBoardProfileHelper.getSupportProfile(tile), stack);
        return profile.isSupported() ? profile.generationMultiplier() : 1.0D;
    }

    public static double getDisplayedPowerFuelMultiplier(TileEntityMekanism tile, ItemStack stack) {
        BoardEffectProfile profile = BoardProfileLoader.getInstalledProfile(CircuitBoardProfileHelper.getSupportProfile(tile), stack);
        return profile.isSupported() ? profile.fuelMultiplier() : 1.0D;
    }

    public static double getEffectiveGeneratorGenerationMultiplier(TileEntityMekanism tile) {
        BoardEffectProfile profile = getInstalledProfile(tile);
        double result = profile.isSupported() ? profile.generationMultiplier() : 1.0D;
        OverMekDebug.logMultiblockEffects(tile, "power-generation", "generation=" + result);
        return result;
    }

    public static double getEffectiveGeneratorFuelMultiplier(TileEntityMekanism tile) {
        BoardEffectProfile profile = getInstalledProfile(tile);
        double result = profile.isSupported() ? profile.fuelMultiplier() : 1.0D;
        OverMekDebug.logMultiblockEffects(tile, "power-fuel", "fuel=" + result);
        return result;
    }

    public static double getDisplayedEvaporationThroughputMultiplier(TileEntityMekanism tile, ItemStack stack) {
        BoardEffectProfile profile = BoardProfileLoader.getInstalledProfile(CircuitBoardProfileHelper.getSupportProfile(tile), stack);
        return profile.isSupported() ? profile.throughputMultiplier() : 1.0D;
    }

    public static int getExtraEvaporationPasses(TileEntityMekanism tile) {
        BoardEffectProfile profile = getInstalledProfile(tile);
        return profile.isSupported() ? Math.max(0, (int) Math.round(profile.throughputMultiplier() - 1.0D)) : 0;
    }

    public static double getEffectiveEvaporationThroughputMultiplier(TileEntityMekanism tile) {
        BoardEffectProfile profile = getInstalledProfile(tile);
        double result = profile.isSupported() ? profile.throughputMultiplier() : 1.0D;
        OverMekDebug.logMultiblockEffects(tile, "evaporation-throughput", "throughput=" + result);
        return result;
    }

    public static double getEffectiveHeatDissipationFactor(TileEntityMekanism tile) {
        BoardEffectProfile profile = getInstalledProfile(tile);
        double result = profile.isSupported() ? OverMekConfig.getEvaporationHeatDissipationFactor() : 1.0D;
        OverMekDebug.logMultiblockEffects(tile, "evaporation-heat", "heatDissipationFactor=" + result);
        return result;
    }

    public static double getDisplayedMatrixCapacityMultiplier(TileEntityMekanism tile, ItemStack stack) {
        BoardEffectProfile profile = BoardProfileLoader.getInstalledProfile(CircuitBoardProfileHelper.getSupportProfile(tile), stack);
        return profile.isSupported() ? profile.matrixCapacityMultiplier() : 1.0D;
    }

    public static double getDisplayedMatrixTransferMultiplier(TileEntityMekanism tile, ItemStack stack) {
        BoardEffectProfile profile = BoardProfileLoader.getInstalledProfile(CircuitBoardProfileHelper.getSupportProfile(tile), stack);
        return profile.isSupported() ? profile.matrixTransferMultiplier() : 1.0D;
    }

    public static double getEffectiveMatrixCapacityMultiplier(TileEntityMekanism tile) {
        BoardEffectProfile profile = getInstalledProfile(tile);
        double result = profile.isSupported() ? profile.matrixCapacityMultiplier() : 1.0D;
        OverMekDebug.logMultiblockEffects(tile, "matrix-capacity", "capacity=" + result);
        return result;
    }

    public static double getEffectiveMatrixTransferMultiplier(TileEntityMekanism tile) {
        BoardEffectProfile profile = getInstalledProfile(tile);
        double result = profile.isSupported() ? profile.matrixTransferMultiplier() : 1.0D;
        OverMekDebug.logMultiblockEffects(tile, "matrix-transfer", "transfer=" + result);
        return result;
    }

    public static double getDisplayedSpsThroughputMultiplier(TileEntityMekanism tile, ItemStack stack) {
        BoardEffectProfile profile = BoardProfileLoader.getInstalledProfile(CircuitBoardProfileHelper.getSupportProfile(tile), stack);
        return profile.isSupported() ? profile.throughputMultiplier() : 1.0D;
    }

    public static double getDisplayedSpsStabilityMultiplier(TileEntityMekanism tile, ItemStack stack) {
        BoardEffectProfile profile = BoardProfileLoader.getInstalledProfile(CircuitBoardProfileHelper.getSupportProfile(tile), stack);
        return profile.isSupported() ? profile.stabilityMultiplier() : 1.0D;
    }

    public static double getDisplayedSpsBufferMultiplier(TileEntityMekanism tile, ItemStack stack) {
        BoardEffectProfile profile = BoardProfileLoader.getInstalledProfile(CircuitBoardProfileHelper.getSupportProfile(tile), stack);
        return profile.isSupported() ? profile.bufferMultiplier() : 1.0D;
    }

    public static double getDisplayedSpsPressureMultiplier(TileEntityMekanism tile, ItemStack stack) {
        BoardEffectProfile profile = BoardProfileLoader.getInstalledProfile(CircuitBoardProfileHelper.getSupportProfile(tile), stack);
        return profile.isSupported() ? profile.pressureMultiplier() : 1.0D;
    }

    public static double getDisplayedSpsEnergyUsageMultiplier(TileEntityMekanism tile, ItemStack stack) {
        BoardEffectProfile profile = BoardProfileLoader.getInstalledProfile(CircuitBoardProfileHelper.getSupportProfile(tile), stack);
        return profile.isSupported() ? profile.scaleValue(profile.energyUsageFactor(), CircuitBoardOverclockHelper.getDisplayedWarmupRatio(tile, stack)) : 1.0D;
    }

    public static double getEffectiveSpsThroughputMultiplier(TileEntityMekanism tile) {
        BoardEffectProfile profile = getInstalledProfile(tile);
        double result = profile.isSupported() ? profile.throughputMultiplier() : 1.0D;
        OverMekDebug.logMultiblockEffects(tile, "sps-throughput", "throughput=" + result);
        return result;
    }

    public static double getEffectiveSpsStabilityMultiplier(TileEntityMekanism tile) {
        BoardEffectProfile profile = getInstalledProfile(tile);
        double result = profile.isSupported() ? profile.stabilityMultiplier() : 1.0D;
        OverMekDebug.logMultiblockEffects(tile, "sps-stability", "stability=" + result);
        return result;
    }

    public static double getEffectiveSpsBufferMultiplier(TileEntityMekanism tile) {
        BoardEffectProfile profile = getInstalledProfile(tile);
        double result = profile.isSupported() ? profile.bufferMultiplier() : 1.0D;
        OverMekDebug.logMultiblockEffects(tile, "sps-buffer", "buffer=" + result);
        return result;
    }

    public static double getEffectiveSpsPressureMultiplier(TileEntityMekanism tile) {
        BoardEffectProfile profile = getInstalledProfile(tile);
        double result = profile.isSupported() ? profile.pressureMultiplier() : 1.0D;
        OverMekDebug.logMultiblockEffects(tile, "sps-pressure", "pressure=" + result);
        return result;
    }

    public static double getEffectiveSpsEnergyUsageMultiplier(TileEntityMekanism tile) {
        BoardEffectProfile profile = getInstalledProfile(tile);
        double result = profile.isSupported() ? profile.scaleValue(profile.energyUsageFactor(), CircuitBoardOverclockHelper.getWarmupRatio(tile)) : 1.0D;
        OverMekDebug.logMultiblockEffects(tile, "sps-energy", "energy=" + result);
        return result;
    }

    public static double getDisplayedBufferMultiplier(TileEntityMekanism tile, ItemStack stack) {
        BoardEffectProfile profile = BoardProfileLoader.getInstalledProfile(CircuitBoardProfileHelper.getSupportProfile(tile), stack);
        if (!profile.isSupported()) {
            return 1.0D;
        }
        return CircuitBoardMultiblockHelper.isInductionMatrix(tile) ? profile.matrixCapacityMultiplier() : profile.bufferMultiplier();
    }

    public static double getEffectiveBufferMultiplier(TileEntityMekanism tile) {
        BoardEffectProfile profile = getInstalledProfile(tile);
        if (!profile.isSupported()) {
            OverMekDebug.logMultiblockEffects(tile, "buffer", "buffer=1.0");
            return 1.0D;
        }
        double result = CircuitBoardMultiblockHelper.isInductionMatrix(tile) ? profile.matrixCapacityMultiplier() : profile.bufferMultiplier();
        OverMekDebug.logMultiblockEffects(tile, "buffer", "buffer=" + result);
        return result;
    }

    public static double getWarmupRatio(ICircuitBoardMultiblockData data) {
        TileEntityMekanism owner = data == null ? null : data.overmek$getOwnerTile();
        return owner == null ? 0.0D : CircuitBoardOverclockHelper.getWarmupRatio(owner);
    }

    private static BoardEffectProfile getInstalledProfile(TileEntityMekanism tile) {
        if (tile == null) {
            return BoardEffectProfile.unsupported();
        }
        BoardEffectProfile profile = BoardProfileLoader.getInstalledProfile(
            CircuitBoardProfileHelper.getSupportProfile(tile),
            CircuitBoardOverclockHelper.getInstalledStack(tile)
        );
        if (profile.isSupported()) {
            LAST_GOOD_PROFILE.put(tile, profile);
            return profile;
        }
        BoardEffectProfile cached = LAST_GOOD_PROFILE.get(tile);
        return cached != null ? cached : profile;
    }
}
