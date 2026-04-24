package com.hewiegui.overmek.util;

import com.hewiegui.overmek.capability.CircuitBoardHolder;
import com.hewiegui.overmek.capability.ICircuitBoardHolder;
import com.hewiegui.overmek.config.OverMekConfig;
import mekanism.api.math.FloatingLong;
import mekanism.generators.common.tile.TileEntityAdvancedSolarGenerator;
import mekanism.generators.common.tile.TileEntityBioGenerator;
import mekanism.generators.common.tile.TileEntityGasGenerator;
import mekanism.generators.common.tile.TileEntityGenerator;
import mekanism.generators.common.tile.TileEntityHeatGenerator;
import mekanism.generators.common.tile.TileEntitySolarGenerator;
import mekanism.generators.common.tile.TileEntityWindGenerator;
import mekanism.common.tile.base.TileEntityMekanism;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public final class CircuitBoardGeneratorHelper {

    private CircuitBoardGeneratorHelper() {
    }

    public static boolean isSupportedGenerator(TileEntityMekanism tile) {
        return OverMekConfig.isGeneratorBoardConfigEnabled() && tile instanceof TileEntityGenerator;
    }

    public static boolean isFuelGenerator(TileEntityMekanism tile) {
        return tile instanceof TileEntityHeatGenerator || tile instanceof TileEntityBioGenerator || tile instanceof TileEntityGasGenerator;
    }

    public static double getDisplayedGenerationMultiplier(TileEntityMekanism tile, ItemStack stack) {
        int tier = CircuitBoardOverclockHelper.getCircuitBoardTier(stack);
        if (tier < 0 || !isSupportedGenerator(tile)) {
            return 1.0D;
        }
        return getGenerationMultiplier(tier, CircuitBoardOverclockHelper.getDisplayedWarmupRatio(tile, stack));
    }

    public static double getDisplayedFuelConsumptionMultiplier(TileEntityMekanism tile, ItemStack stack) {
        int tier = CircuitBoardOverclockHelper.getCircuitBoardTier(stack);
        if (tier < 0 || !isFuelGenerator(tile)) {
            return 1.0D;
        }
        return getFuelConsumptionMultiplier(tier, CircuitBoardOverclockHelper.getDisplayedWarmupRatio(tile, stack));
    }

    public static double getEnergyCapacityMultiplier(TileEntityMekanism tile, int tier) {
        if (!isSupportedGenerator(tile) || tier < 0) {
            return 1.0D;
        }
        return OverMekConfig.getGeneratorEnergyCapacityFactor(tier);
    }

    public static int getWarmupTicks(TileEntityMekanism tile, int tier) {
        if (!isSupportedGenerator(tile) || tier < 0) {
            return 0;
        }
        return OverMekConfig.getGeneratorStartupWarmupTicks(tier);
    }

    public static double getEffectiveGenerationMultiplier(TileEntityMekanism tile) {
        ICircuitBoardHolder holder = getHolder(tile);
        if (holder == null || !holder.hasCircuitBoard() || !isSupportedGenerator(tile)) {
            return 1.0D;
        }
        return getGenerationMultiplier(holder.getTier(), holder.getWarmupRatio(getWarmupTicks(tile, holder.getTier())));
    }

    public static double getEffectiveFuelConsumptionMultiplier(TileEntityMekanism tile) {
        ICircuitBoardHolder holder = getHolder(tile);
        if (holder == null || !holder.hasCircuitBoard() || !isFuelGenerator(tile)) {
            return 1.0D;
        }
        return getFuelConsumptionMultiplier(holder.getTier(), holder.getWarmupRatio(getWarmupTicks(tile, holder.getTier())));
    }

    public static void tickGeneratorWarmup(TileEntityMekanism tile, boolean active) {
        CircuitBoardOverclockHelper.tickWarmup(tile, active);
    }

    public static FloatingLong getExtraGeneration(TileEntityMekanism tile, FloatingLong baseProduction, double fulfillmentRatio) {
        if (baseProduction.isZero()) {
            return FloatingLong.ZERO;
        }
        double generationMultiplier = getEffectiveGenerationMultiplier(tile);
        if (generationMultiplier <= 1.0D) {
            return FloatingLong.ZERO;
        }
        return baseProduction.multiply((generationMultiplier - 1.0D) * Math.max(0.0D, fulfillmentRatio));
    }

    public static int takeExtraFuelUnits(TileEntityMekanism tile, double baseUnitsPerTick) {
        ICircuitBoardHolder holder = getHolder(tile);
        if (holder == null || !holder.hasCircuitBoard() || baseUnitsPerTick <= 0.0D) {
            return 0;
        }
        double fuelMultiplier = getEffectiveFuelConsumptionMultiplier(tile);
        if (fuelMultiplier <= 1.0D) {
            return 0;
        }
        double buffered = holder.getGeneratorFuelRemainder() + baseUnitsPerTick * (fuelMultiplier - 1.0D);
        int wholeUnits = (int) Math.floor(buffered);
        holder.setGeneratorFuelRemainder(buffered - wholeUnits);
        return Math.max(0, wholeUnits);
    }

    public static void resetFuelRemainder(TileEntityMekanism tile) {
        ICircuitBoardHolder holder = getHolder(tile);
        if (holder != null) {
            holder.setGeneratorFuelRemainder(0.0D);
        }
    }

    private static double getGenerationMultiplier(int tier, double warmupRatio) {
        double fullMultiplier = OverMekConfig.getGeneratorGenerationMultiplier(tier);
        return 1.0D + Math.max(0.0D, fullMultiplier - 1.0D) * clampWarmup(warmupRatio);
    }

    private static double getFuelConsumptionMultiplier(int tier, double warmupRatio) {
        double fullMultiplier = OverMekConfig.getGeneratorFuelConsumptionMultiplier(tier);
        return 1.0D + Math.max(0.0D, fullMultiplier - 1.0D) * clampWarmup(warmupRatio);
    }

    private static double clampWarmup(double warmupRatio) {
        return Math.max(0.0D, Math.min(1.0D, warmupRatio));
    }

    @Nullable
    private static ICircuitBoardHolder getHolder(TileEntityMekanism tile) {
        return tile.getCapability(CircuitBoardHolder.CIRCUIT_BOARD_CAPABILITY).resolve().orElse(null);
    }
}
