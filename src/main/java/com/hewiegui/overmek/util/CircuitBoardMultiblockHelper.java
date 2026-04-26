package com.hewiegui.overmek.util;

import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.multiblock.TileEntityInductionCasing;
import mekanism.common.tile.multiblock.TileEntitySPSCasing;
import mekanism.common.tile.multiblock.TileEntityThermalEvaporationBlock;
import mekanism.generators.common.tile.fission.TileEntityFissionReactorCasing;
import mekanism.generators.common.tile.fusion.TileEntityFusionReactorBlock;
import mekanism.generators.common.tile.turbine.TileEntityTurbineCasing;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public final class CircuitBoardMultiblockHelper {

    private CircuitBoardMultiblockHelper() {
    }

    public static boolean isSupportedMultiblockController(BlockEntity blockEntity) {
        return CircuitBoardProfileHelper.isMultiblockMachine(blockEntity);
    }

    public static boolean isPowerMultiblock(BlockEntity blockEntity) {
        return blockEntity instanceof TileEntityFusionReactorBlock
            || blockEntity instanceof TileEntityTurbineCasing
            || blockEntity instanceof TileEntityInductionCasing;
    }

    public static boolean isInductionMatrix(BlockEntity blockEntity) {
        return blockEntity instanceof TileEntityInductionCasing;
    }

    public static boolean isSps(BlockEntity blockEntity) {
        return blockEntity instanceof TileEntitySPSCasing;
    }

    public static boolean isFissionReactor(BlockEntity blockEntity) {
        return blockEntity instanceof TileEntityFissionReactorCasing;
    }

    public static boolean isEvaporationTower(BlockEntity blockEntity) {
        return blockEntity instanceof TileEntityThermalEvaporationBlock;
    }

    public static double getDisplayedFissionEfficiencyMultiplier(TileEntityMekanism tile, ItemStack stack) {
        return MultiblockBoardService.getDisplayedCoolingMultiplier(tile, stack);
    }

    public static double getDisplayedFissionStabilityMultiplier(TileEntityMekanism tile, ItemStack stack) {
        return MultiblockBoardService.getDisplayedFissionStabilityMultiplier(tile, stack);
    }

    public static double getEffectiveFissionEfficiencyMultiplier(TileEntityMekanism tile) {
        return MultiblockBoardService.getEffectiveFissionEfficiencyMultiplier(tile);
    }

    public static double getEffectiveFissionStabilityMultiplier(TileEntityMekanism tile) {
        return MultiblockBoardService.getEffectiveFissionStabilityMultiplier(tile);
    }

    public static double getDisplayedPowerGenerationMultiplier(TileEntityMekanism tile, ItemStack stack) {
        return MultiblockBoardService.getDisplayedPowerGenerationMultiplier(tile, stack);
    }

    public static double getDisplayedPowerFuelMultiplier(TileEntityMekanism tile, ItemStack stack) {
        return MultiblockBoardService.getDisplayedPowerFuelMultiplier(tile, stack);
    }

    public static double getEffectiveGeneratorGenerationMultiplier(TileEntityMekanism tile) {
        return MultiblockBoardService.getEffectiveGeneratorGenerationMultiplier(tile);
    }

    public static double getEffectiveGeneratorFuelMultiplier(TileEntityMekanism tile) {
        return MultiblockBoardService.getEffectiveGeneratorFuelMultiplier(tile);
    }

    public static double getDisplayedEvaporationThroughputMultiplier(TileEntityMekanism tile, ItemStack stack) {
        return MultiblockBoardService.getDisplayedEvaporationThroughputMultiplier(tile, stack);
    }

    public static double getEffectiveEvaporationThroughputMultiplier(TileEntityMekanism tile) {
        return MultiblockBoardService.getEffectiveEvaporationThroughputMultiplier(tile);
    }

    public static int getExtraEvaporationPasses(TileEntityMekanism tile) {
        return MultiblockBoardService.getExtraEvaporationPasses(tile);
    }

    public static double getDisplayedMatrixCapacityMultiplier(TileEntityMekanism tile, ItemStack stack) {
        return MultiblockBoardService.getDisplayedMatrixCapacityMultiplier(tile, stack);
    }

    public static double getDisplayedMatrixTransferMultiplier(TileEntityMekanism tile, ItemStack stack) {
        return MultiblockBoardService.getDisplayedMatrixTransferMultiplier(tile, stack);
    }

    public static double getEffectiveMatrixCapacityMultiplier(TileEntityMekanism tile) {
        return MultiblockBoardService.getEffectiveMatrixCapacityMultiplier(tile);
    }

    public static double getEffectiveMatrixTransferMultiplier(TileEntityMekanism tile) {
        return MultiblockBoardService.getEffectiveMatrixTransferMultiplier(tile);
    }

    public static double getDisplayedSpsThroughputMultiplier(TileEntityMekanism tile, ItemStack stack) {
        return MultiblockBoardService.getDisplayedSpsThroughputMultiplier(tile, stack);
    }

    public static double getDisplayedSpsStabilityMultiplier(TileEntityMekanism tile, ItemStack stack) {
        return MultiblockBoardService.getDisplayedSpsStabilityMultiplier(tile, stack);
    }

    public static double getDisplayedSpsBufferMultiplier(TileEntityMekanism tile, ItemStack stack) {
        return MultiblockBoardService.getDisplayedSpsBufferMultiplier(tile, stack);
    }

    public static double getDisplayedSpsPressureMultiplier(TileEntityMekanism tile, ItemStack stack) {
        return MultiblockBoardService.getDisplayedSpsPressureMultiplier(tile, stack);
    }

    public static double getDisplayedSpsEnergyUsageMultiplier(TileEntityMekanism tile, ItemStack stack) {
        return MultiblockBoardService.getDisplayedSpsEnergyUsageMultiplier(tile, stack);
    }

    public static double getEffectiveSpsThroughputMultiplier(TileEntityMekanism tile) {
        return MultiblockBoardService.getEffectiveSpsThroughputMultiplier(tile);
    }

    public static double getEffectiveSpsStabilityMultiplier(TileEntityMekanism tile) {
        return MultiblockBoardService.getEffectiveSpsStabilityMultiplier(tile);
    }

    public static double getEffectiveSpsBufferMultiplier(TileEntityMekanism tile) {
        return MultiblockBoardService.getEffectiveSpsBufferMultiplier(tile);
    }

    public static double getEffectiveSpsPressureMultiplier(TileEntityMekanism tile) {
        return MultiblockBoardService.getEffectiveSpsPressureMultiplier(tile);
    }

    public static double getEffectiveSpsEnergyUsageMultiplier(TileEntityMekanism tile) {
        return MultiblockBoardService.getEffectiveSpsEnergyUsageMultiplier(tile);
    }

    public static double getDisplayedBufferMultiplier(TileEntityMekanism tile, ItemStack stack) {
        return MultiblockBoardService.getDisplayedBufferMultiplier(tile, stack);
    }

    public static double getEffectiveBufferMultiplier(TileEntityMekanism tile) {
        return MultiblockBoardService.getEffectiveBufferMultiplier(tile);
    }

    public static double getEffectiveHeatDissipationFactor(TileEntityMekanism tile) {
        return MultiblockBoardService.getEffectiveHeatDissipationFactor(tile);
    }

    public static double getWarmupRatio(@Nullable ICircuitBoardMultiblockData data) {
        return MultiblockBoardService.getWarmupRatio(data);
    }
}
