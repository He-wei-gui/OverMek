package com.hewiegui.overmek.util;

import com.hewiegui.overmek.capability.ICircuitBoardHolder;
import com.hewiegui.overmek.config.OverMekConfig;
import mekanism.api.math.FloatingLong;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.factory.TileEntityFactory;
import net.minecraft.world.item.ItemStack;

public final class ProcessingBoardService {

    private ProcessingBoardService() {
    }

    public static double getEffectiveSpeedMultiplier(TileEntityMekanism tile) {
        ICircuitBoardHolder holder = BoardHostResolver.resolveHolder(tile);
        if (holder == null || !holder.hasCircuitBoard() || holder.getBoardChannel() != CircuitBoardChannel.STANDARD) {
            return 1.0D;
        }
        BoardEffectProfile profile = BoardProfileLoader.getInstalledProfile(CircuitBoardProfileHelper.getSupportProfile(tile), holder.getCircuitBoard());
        if (!profile.isSupported()) {
            return 1.0D;
        }
        return profile.getSpeedMultiplier(holder.getOverclockCount(), isFactoryLike(tile), OverMekConfig.getMaxOverclockBonus(), getWarmupRatio(tile, profile));
    }

    public static double getDisplayedSpeedMultiplier(TileEntityMekanism tile, ItemStack stack) {
        BoardEffectProfile profile = BoardProfileLoader.getInstalledProfile(CircuitBoardProfileHelper.getSupportProfile(tile), stack);
        int overclockCount = CircuitBoardOverclockHelper.getCircuitBoardOverclockCount(stack);
        if (!profile.isSupported() || overclockCount <= 0) {
            return 1.0D;
        }
        return profile.getSpeedMultiplier(overclockCount, isFactoryLike(tile), OverMekConfig.getMaxOverclockBonus(), CircuitBoardOverclockHelper.getDisplayedWarmupRatio(tile, stack));
    }

    public static double getDisplayedOverclockBonus(TileEntityMekanism tile, ItemStack stack) {
        return Math.max(0.0D, getDisplayedSpeedMultiplier(tile, stack) - 1.0D);
    }

    public static double getEnergyUsageMultiplier(TileEntityMekanism tile) {
        ICircuitBoardHolder holder = BoardHostResolver.resolveHolder(tile);
        if (holder == null || !holder.hasCircuitBoard() || holder.getBoardChannel() != CircuitBoardChannel.STANDARD) {
            return 1.0D;
        }
        BoardEffectProfile profile = BoardProfileLoader.getInstalledProfile(CircuitBoardProfileHelper.getSupportProfile(tile), holder.getCircuitBoard());
        if (!profile.isSupported()) {
            return 1.0D;
        }
        return profile.getEnergyUsageMultiplier(holder.getOverclockCount(), isFactoryLike(tile), OverMekConfig.getMaxOverclockBonus(), getWarmupRatio(tile, profile), OverMekConfig.getOverclockEnergyMultiplier());
    }

    public static double getDisplayedEnergyUsageMultiplier(TileEntityMekanism tile, ItemStack stack) {
        BoardEffectProfile profile = BoardProfileLoader.getInstalledProfile(CircuitBoardProfileHelper.getSupportProfile(tile), stack);
        int overclockCount = CircuitBoardOverclockHelper.getCircuitBoardOverclockCount(stack);
        if (!profile.isSupported() || overclockCount <= 0) {
            return 1.0D;
        }
        return profile.getEnergyUsageMultiplier(overclockCount, isFactoryLike(tile), OverMekConfig.getMaxOverclockBonus(), CircuitBoardOverclockHelper.getDisplayedWarmupRatio(tile, stack), OverMekConfig.getOverclockEnergyMultiplier());
    }

    public static double getEnergyCapacityMultiplier(ItemStack stack) {
        BoardEffectProfile profile = BoardProfileLoader.getProfile(CircuitBoardMachineProfile.PROCESSING, stack);
        return profile.isSupported() ? profile.energyCapacityFactor() : 1.0D;
    }

    public static int getAdjustedTicksRequired(TileEntityMekanism tile, int baseTicksRequired) {
        double speedMultiplier = getEffectiveSpeedMultiplier(tile);
        if (speedMultiplier <= 1.0D || baseTicksRequired <= 1) {
            return baseTicksRequired;
        }
        return Math.max(1, (int) Math.ceil(baseTicksRequired / speedMultiplier));
    }

    public static FloatingLong getAdjustedEnergyPerTick(TileEntityMekanism tile, FloatingLong baseEnergyPerTick) {
        double energyMultiplier = getEnergyUsageMultiplier(tile);
        return energyMultiplier == 1.0D ? baseEnergyPerTick : baseEnergyPerTick.multiply(energyMultiplier);
    }

    public static int getExtraRecipePasses(TileEntityMekanism tile) {
        return Math.max(0, (int) Math.round(getEffectiveSpeedMultiplier(tile) - 1.0D));
    }

    private static double getWarmupRatio(TileEntityMekanism tile, BoardEffectProfile profile) {
        ICircuitBoardHolder holder = BoardHostResolver.resolveHolder(tile);
        return holder == null ? 1.0D : holder.getWarmupRatio(profile.warmupTicks());
    }

    private static boolean isFactoryLike(TileEntityMekanism tile) {
        return tile instanceof TileEntityFactory<?> || JerryAddonCompat.isMoreMachineFactory(tile);
    }
}
