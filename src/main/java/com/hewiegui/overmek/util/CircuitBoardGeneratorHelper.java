package com.hewiegui.overmek.util;

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

public final class CircuitBoardGeneratorHelper {

    private CircuitBoardGeneratorHelper() {
    }

    public static boolean isSupportedGenerator(TileEntityMekanism tile) {
        return CircuitBoardProfileHelper.getMachineProfile(tile) == CircuitBoardMachineProfile.GENERATOR
            && (tile instanceof TileEntityGenerator || tile instanceof TileEntitySolarGenerator || tile instanceof TileEntityAdvancedSolarGenerator || tile instanceof TileEntityWindGenerator
                || JerryAddonCompat.isMoreGenerator(tile));
    }

    public static boolean isFuelGenerator(TileEntityMekanism tile) {
        return tile instanceof TileEntityHeatGenerator || tile instanceof TileEntityBioGenerator || tile instanceof TileEntityGasGenerator || JerryAddonCompat.isMoreFuelGenerator(tile);
    }

    public static double getDisplayedGenerationMultiplier(TileEntityMekanism tile, ItemStack stack) {
        return GeneratorBoardService.getDisplayedGenerationMultiplier(tile, stack);
    }

    public static double getDisplayedFuelConsumptionMultiplier(TileEntityMekanism tile, ItemStack stack) {
        return GeneratorBoardService.getDisplayedFuelConsumptionMultiplier(tile, stack);
    }

    public static double getEnergyCapacityMultiplier(TileEntityMekanism tile, int tier) {
        return GeneratorBoardService.getEnergyCapacityMultiplier(tile, tier);
    }

    public static int getWarmupTicks(TileEntityMekanism tile, int tier) {
        return GeneratorBoardService.getWarmupTicks(tile, tier);
    }

    public static double getEffectiveGenerationMultiplier(TileEntityMekanism tile) {
        return GeneratorBoardService.getEffectiveGenerationMultiplier(tile);
    }

    public static double getEffectiveFuelConsumptionMultiplier(TileEntityMekanism tile) {
        return GeneratorBoardService.getEffectiveFuelConsumptionMultiplier(tile);
    }

    public static void tickGeneratorWarmup(TileEntityMekanism tile, boolean active) {
        CircuitBoardOverclockHelper.tickWarmup(tile, active);
    }

    public static FloatingLong getExtraGeneration(TileEntityMekanism tile, FloatingLong baseProduction, double fulfillmentRatio) {
        return GeneratorBoardService.getExtraGeneration(tile, baseProduction, fulfillmentRatio);
    }

    public static int takeExtraFuelUnits(TileEntityMekanism tile, double baseUnitsPerTick) {
        return GeneratorBoardService.takeExtraFuelUnits(tile, baseUnitsPerTick);
    }

    public static void resetFuelRemainder(TileEntityMekanism tile) {
        GeneratorBoardService.resetFuelRemainder(tile);
    }
}
