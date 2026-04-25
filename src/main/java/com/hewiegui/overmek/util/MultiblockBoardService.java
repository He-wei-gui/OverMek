package com.hewiegui.overmek.util;

import mekanism.common.tile.base.TileEntityMekanism;
import net.minecraft.world.item.ItemStack;

public final class MultiblockBoardService {

    private MultiblockBoardService() {
    }

    public static double getDisplayedFissionEfficiencyMultiplier(TileEntityMekanism tile, ItemStack stack) {
        BoardEffectProfile profile = BoardProfileLoader.getInstalledProfile(CircuitBoardProfileHelper.getSupportProfile(tile), stack);
        return profile.isSupported() ? profile.scaleMultiplier(profile.speedMultiplier(), CircuitBoardOverclockHelper.getDisplayedWarmupRatio(tile, stack)) : 1.0D;
    }

    public static double getDisplayedCoolingMultiplier(TileEntityMekanism tile, ItemStack stack) {
        return getDisplayedFissionEfficiencyMultiplier(tile, stack);
    }

    public static double getDisplayedFissionStabilityMultiplier(TileEntityMekanism tile, ItemStack stack) {
        BoardEffectProfile profile = BoardProfileLoader.getInstalledProfile(CircuitBoardProfileHelper.getSupportProfile(tile), stack);
        return profile.isSupported() ? profile.scaleMultiplier(profile.stabilityMultiplier(), CircuitBoardOverclockHelper.getDisplayedWarmupRatio(tile, stack)) : 1.0D;
    }

    public static double getEffectiveFissionEfficiencyMultiplier(TileEntityMekanism tile) {
        BoardEffectProfile profile = getInstalledProfile(tile);
        return profile.isSupported() ? profile.scaleMultiplier(profile.speedMultiplier(), CircuitBoardOverclockHelper.getWarmupRatio(tile)) : 1.0D;
    }

    public static double getEffectiveFissionStabilityMultiplier(TileEntityMekanism tile) {
        BoardEffectProfile profile = getInstalledProfile(tile);
        return profile.isSupported() ? profile.scaleMultiplier(profile.stabilityMultiplier(), CircuitBoardOverclockHelper.getWarmupRatio(tile)) : 1.0D;
    }

    public static double getDisplayedPowerGenerationMultiplier(TileEntityMekanism tile, ItemStack stack) {
        BoardEffectProfile profile = BoardProfileLoader.getInstalledProfile(CircuitBoardProfileHelper.getSupportProfile(tile), stack);
        return profile.isSupported() ? profile.scaleMultiplier(profile.generationMultiplier(), CircuitBoardOverclockHelper.getDisplayedWarmupRatio(tile, stack)) : 1.0D;
    }

    public static double getDisplayedPowerFuelMultiplier(TileEntityMekanism tile, ItemStack stack) {
        BoardEffectProfile profile = BoardProfileLoader.getInstalledProfile(CircuitBoardProfileHelper.getSupportProfile(tile), stack);
        return profile.isSupported() ? profile.scaleMultiplier(profile.fuelMultiplier(), CircuitBoardOverclockHelper.getDisplayedWarmupRatio(tile, stack)) : 1.0D;
    }

    public static double getEffectiveGeneratorGenerationMultiplier(TileEntityMekanism tile) {
        BoardEffectProfile profile = getInstalledProfile(tile);
        return profile.isSupported() ? profile.scaleMultiplier(profile.generationMultiplier(), CircuitBoardOverclockHelper.getWarmupRatio(tile)) : 1.0D;
    }

    public static double getEffectiveGeneratorFuelMultiplier(TileEntityMekanism tile) {
        BoardEffectProfile profile = getInstalledProfile(tile);
        return profile.isSupported() ? profile.scaleMultiplier(profile.fuelMultiplier(), CircuitBoardOverclockHelper.getWarmupRatio(tile)) : 1.0D;
    }

    public static double getDisplayedEvaporationThroughputMultiplier(TileEntityMekanism tile, ItemStack stack) {
        BoardEffectProfile profile = BoardProfileLoader.getInstalledProfile(CircuitBoardProfileHelper.getSupportProfile(tile), stack);
        return profile.isSupported() ? profile.scaleMultiplier(profile.throughputMultiplier(), CircuitBoardOverclockHelper.getDisplayedWarmupRatio(tile, stack)) : 1.0D;
    }

    public static int getExtraEvaporationPasses(TileEntityMekanism tile) {
        BoardEffectProfile profile = getInstalledProfile(tile);
        return profile.isSupported() ? Math.max(0, (int) Math.round(profile.scaleMultiplier(profile.throughputMultiplier(), CircuitBoardOverclockHelper.getWarmupRatio(tile)) - 1.0D)) : 0;
    }

    public static double getDisplayedMatrixCapacityMultiplier(TileEntityMekanism tile, ItemStack stack) {
        BoardEffectProfile profile = BoardProfileLoader.getInstalledProfile(CircuitBoardProfileHelper.getSupportProfile(tile), stack);
        return profile.isSupported() ? profile.matrixCapacityMultiplier() : 1.0D;
    }

    public static double getDisplayedMatrixTransferMultiplier(TileEntityMekanism tile, ItemStack stack) {
        BoardEffectProfile profile = BoardProfileLoader.getInstalledProfile(CircuitBoardProfileHelper.getSupportProfile(tile), stack);
        return profile.isSupported() ? profile.scaleMultiplier(profile.matrixTransferMultiplier(), CircuitBoardOverclockHelper.getDisplayedWarmupRatio(tile, stack)) : 1.0D;
    }

    public static double getEffectiveMatrixCapacityMultiplier(TileEntityMekanism tile) {
        BoardEffectProfile profile = getInstalledProfile(tile);
        return profile.isSupported() ? profile.matrixCapacityMultiplier() : 1.0D;
    }

    public static double getEffectiveMatrixTransferMultiplier(TileEntityMekanism tile) {
        BoardEffectProfile profile = getInstalledProfile(tile);
        return profile.isSupported() ? profile.scaleMultiplier(profile.matrixTransferMultiplier(), CircuitBoardOverclockHelper.getWarmupRatio(tile)) : 1.0D;
    }

    public static double getDisplayedSpsThroughputMultiplier(TileEntityMekanism tile, ItemStack stack) {
        BoardEffectProfile profile = BoardProfileLoader.getInstalledProfile(CircuitBoardProfileHelper.getSupportProfile(tile), stack);
        return profile.isSupported() ? profile.scaleMultiplier(profile.throughputMultiplier(), CircuitBoardOverclockHelper.getDisplayedWarmupRatio(tile, stack)) : 1.0D;
    }

    public static double getDisplayedSpsStabilityMultiplier(TileEntityMekanism tile, ItemStack stack) {
        BoardEffectProfile profile = BoardProfileLoader.getInstalledProfile(CircuitBoardProfileHelper.getSupportProfile(tile), stack);
        return profile.isSupported() ? profile.scaleMultiplier(profile.stabilityMultiplier(), CircuitBoardOverclockHelper.getDisplayedWarmupRatio(tile, stack)) : 1.0D;
    }

    public static double getDisplayedSpsBufferMultiplier(TileEntityMekanism tile, ItemStack stack) {
        BoardEffectProfile profile = BoardProfileLoader.getInstalledProfile(CircuitBoardProfileHelper.getSupportProfile(tile), stack);
        return profile.isSupported() ? profile.bufferMultiplier() : 1.0D;
    }

    public static double getDisplayedSpsPressureMultiplier(TileEntityMekanism tile, ItemStack stack) {
        BoardEffectProfile profile = BoardProfileLoader.getInstalledProfile(CircuitBoardProfileHelper.getSupportProfile(tile), stack);
        return profile.isSupported() ? profile.scaleMultiplier(profile.pressureMultiplier(), CircuitBoardOverclockHelper.getDisplayedWarmupRatio(tile, stack)) : 1.0D;
    }

    public static double getDisplayedSpsEnergyUsageMultiplier(TileEntityMekanism tile, ItemStack stack) {
        BoardEffectProfile profile = BoardProfileLoader.getInstalledProfile(CircuitBoardProfileHelper.getSupportProfile(tile), stack);
        return profile.isSupported() ? profile.scaleValue(profile.energyUsageFactor(), CircuitBoardOverclockHelper.getDisplayedWarmupRatio(tile, stack)) : 1.0D;
    }

    public static double getEffectiveSpsThroughputMultiplier(TileEntityMekanism tile) {
        BoardEffectProfile profile = getInstalledProfile(tile);
        return profile.isSupported() ? profile.scaleMultiplier(profile.throughputMultiplier(), CircuitBoardOverclockHelper.getWarmupRatio(tile)) : 1.0D;
    }

    public static double getEffectiveSpsStabilityMultiplier(TileEntityMekanism tile) {
        BoardEffectProfile profile = getInstalledProfile(tile);
        return profile.isSupported() ? profile.scaleMultiplier(profile.stabilityMultiplier(), CircuitBoardOverclockHelper.getWarmupRatio(tile)) : 1.0D;
    }

    public static double getEffectiveSpsBufferMultiplier(TileEntityMekanism tile) {
        BoardEffectProfile profile = getInstalledProfile(tile);
        return profile.isSupported() ? profile.bufferMultiplier() : 1.0D;
    }

    public static double getEffectiveSpsPressureMultiplier(TileEntityMekanism tile) {
        BoardEffectProfile profile = getInstalledProfile(tile);
        return profile.isSupported() ? profile.scaleMultiplier(profile.pressureMultiplier(), CircuitBoardOverclockHelper.getWarmupRatio(tile)) : 1.0D;
    }

    public static double getEffectiveSpsEnergyUsageMultiplier(TileEntityMekanism tile) {
        BoardEffectProfile profile = getInstalledProfile(tile);
        return profile.isSupported() ? profile.scaleValue(profile.energyUsageFactor(), CircuitBoardOverclockHelper.getWarmupRatio(tile)) : 1.0D;
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
            return 1.0D;
        }
        return CircuitBoardMultiblockHelper.isInductionMatrix(tile) ? profile.matrixCapacityMultiplier() : profile.bufferMultiplier();
    }

    public static double getWarmupRatio(ICircuitBoardMultiblockData data) {
        TileEntityMekanism owner = data == null ? null : data.overmek$getOwnerTile();
        return owner == null ? 0.0D : CircuitBoardOverclockHelper.getWarmupRatio(owner);
    }

    private static BoardEffectProfile getInstalledProfile(TileEntityMekanism tile) {
        return BoardProfileLoader.getInstalledProfile(CircuitBoardProfileHelper.getSupportProfile(tile), CircuitBoardOverclockHelper.getInstalledStack(tile));
    }
}
