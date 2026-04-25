package com.hewiegui.overmek.util;

import com.hewiegui.overmek.capability.ICircuitBoardHolder;
import mekanism.api.math.FloatingLong;
import mekanism.generators.common.tile.TileEntityBioGenerator;
import mekanism.generators.common.tile.TileEntityGasGenerator;
import mekanism.generators.common.tile.TileEntityGenerator;
import mekanism.generators.common.tile.TileEntityHeatGenerator;
import mekanism.common.tile.base.TileEntityMekanism;
import net.minecraft.world.item.ItemStack;

public final class GeneratorBoardService {

    private GeneratorBoardService() {
    }

    public static boolean isSupportedGenerator(TileEntityMekanism tile) {
        return tile instanceof TileEntityGenerator;
    }

    public static boolean isFuelGenerator(TileEntityMekanism tile) {
        return tile instanceof TileEntityHeatGenerator || tile instanceof TileEntityBioGenerator || tile instanceof TileEntityGasGenerator;
    }

    public static double getEffectiveGenerationMultiplier(TileEntityMekanism tile) {
        ICircuitBoardHolder holder = BoardHostResolver.resolveHolder(tile);
        if (holder == null || !holder.hasCircuitBoard() || holder.getBoardChannel() != CircuitBoardChannel.STANDARD) {
            return 1.0D;
        }
        BoardEffectProfile profile = BoardProfileLoader.getInstalledProfile(CircuitBoardProfileHelper.getSupportProfile(tile), holder.getCircuitBoard());
        return profile.isSupported() ? profile.scaleMultiplier(profile.generationMultiplier(), holder.getWarmupRatio(profile.warmupTicks())) : 1.0D;
    }

    public static double getDisplayedGenerationMultiplier(TileEntityMekanism tile, ItemStack stack) {
        BoardEffectProfile profile = BoardProfileLoader.getInstalledProfile(CircuitBoardProfileHelper.getSupportProfile(tile), stack);
        return profile.isSupported() ? profile.scaleMultiplier(profile.generationMultiplier(), CircuitBoardOverclockHelper.getDisplayedWarmupRatio(tile, stack)) : 1.0D;
    }

    public static double getEffectiveFuelConsumptionMultiplier(TileEntityMekanism tile) {
        ICircuitBoardHolder holder = BoardHostResolver.resolveHolder(tile);
        if (holder == null || !holder.hasCircuitBoard() || holder.getBoardChannel() != CircuitBoardChannel.STANDARD || !isFuelGenerator(tile)) {
            return 1.0D;
        }
        BoardEffectProfile profile = BoardProfileLoader.getInstalledProfile(CircuitBoardProfileHelper.getSupportProfile(tile), holder.getCircuitBoard());
        return profile.isSupported() ? profile.scaleMultiplier(profile.fuelMultiplier(), holder.getWarmupRatio(profile.warmupTicks())) : 1.0D;
    }

    public static double getDisplayedFuelConsumptionMultiplier(TileEntityMekanism tile, ItemStack stack) {
        if (!isFuelGenerator(tile)) {
            return 1.0D;
        }
        BoardEffectProfile profile = BoardProfileLoader.getInstalledProfile(CircuitBoardProfileHelper.getSupportProfile(tile), stack);
        return profile.isSupported() ? profile.scaleMultiplier(profile.fuelMultiplier(), CircuitBoardOverclockHelper.getDisplayedWarmupRatio(tile, stack)) : 1.0D;
    }

    public static double getEnergyCapacityMultiplier(TileEntityMekanism tile, int tier) {
        if (!isSupportedGenerator(tile)) {
            return 1.0D;
        }
        return BoardProfileLoader.getGeneratorProfile(tier).energyCapacityFactor();
    }

    public static int getWarmupTicks(TileEntityMekanism tile, int tier) {
        if (!isSupportedGenerator(tile)) {
            return 0;
        }
        return BoardProfileLoader.getGeneratorProfile(tier).warmupTicks();
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
        ICircuitBoardHolder holder = BoardHostResolver.resolveHolder(tile);
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
        ICircuitBoardHolder holder = BoardHostResolver.resolveHolder(tile);
        if (holder != null) {
            holder.setGeneratorFuelRemainder(0.0D);
        }
    }
}
