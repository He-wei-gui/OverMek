package com.hewiegui.overmek.util;

import com.hewiegui.overmek.capability.CircuitBoardHolder;
import com.hewiegui.overmek.capability.ICircuitBoardHolder;
import com.hewiegui.overmek.config.OverMekConfig;
import java.util.List;
import java.util.regex.Pattern;
import mekanism.api.math.FloatingLong;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.factory.TileEntityFactory;
import mekanism.common.tile.prefab.TileEntityProgressMachine;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public final class CircuitBoardOverclockHelper {

    private CircuitBoardOverclockHelper() {
    }

    public static boolean canApplyCircuitBoardEffects(BlockEntity blockEntity) {
        if (!(blockEntity instanceof TileEntityMekanism)) {
            return false;
        }
        if (!(blockEntity instanceof TileEntityProgressMachine<?> || blockEntity instanceof TileEntityFactory<?>)) {
            return false;
        }
        String className = blockEntity.getClass().getName();
        if (matchesAnyRule(className, OverMekConfig.getBlockedMachineClasses())) {
            return false;
        }
        List<? extends String> allowList = OverMekConfig.getAllowedMachineClasses();
        return allowList.isEmpty() || matchesAnyRule(className, allowList);
    }

    public static boolean shouldExposeCircuitBoardSlot(BlockEntity blockEntity, @Nullable ICircuitBoardHolder holder) {
        if (!(blockEntity instanceof TileEntityMekanism tile) || !tile.supportsUpgrades()) {
            return false;
        }
        return canApplyCircuitBoardEffects(blockEntity) || holder != null && holder.hasCircuitBoard();
    }

    public static int getInstalledTier(TileEntityMekanism tile) {
        ICircuitBoardHolder holder = getHolder(tile);
        if (holder == null || !holder.hasCircuitBoard()) {
            return -1;
        }
        return holder.getTier();
    }

    public static int getOverclockBonus(TileEntityMekanism tile) {
        return (int) Math.floor(Math.max(0.0D, getDisplayedOverclockBonus(tile)));
    }

    public static double getDisplayedOverclockBonus(TileEntityMekanism tile) {
        return Math.max(0.0D, getEffectiveSpeedMultiplier(tile) - 1.0D);
    }

    public static double getEffectiveSpeedMultiplier(TileEntityMekanism tile) {
        if (!OverMekConfig.isOverclockEnabled()) {
            return 1.0D;
        }
        if (!canApplyCircuitBoardEffects(tile)) {
            return 1.0D;
        }
        if (tile instanceof TileEntityFactory<?> && !OverMekConfig.isFactoryOverclockEnabled()) {
            return 1.0D;
        }

        ICircuitBoardHolder holder = getHolder(tile);
        if (holder == null || !holder.hasCircuitBoard()) {
            return 1.0D;
        }
        return getBoardSpeedMultiplier(holder.getTier(), holder.getOverclockCount(), tile instanceof TileEntityFactory<?>);
    }

    public static double getEnergyUsageMultiplier(TileEntityMekanism tile) {
        ICircuitBoardHolder holder = getHolder(tile);
        if (holder == null || !holder.hasCircuitBoard()) {
            return 1.0D;
        }
        return getBoardEnergyUsageMultiplier(holder.getTier(), holder.getOverclockCount(), tile instanceof TileEntityFactory<?>);
    }

    public static double getBoardSpeedMultiplier(int tier, int overclockCount, boolean factory) {
        double speedBonus = getBoardSpeedBonus(tier, overclockCount, factory);
        return speedBonus <= 0.0D ? 1.0D : speedBonus + 1.0D;
    }

    public static double getBoardEnergyUsageMultiplier(int tier, int overclockCount, boolean factory) {
        return getBoardSpeedMultiplier(tier, overclockCount, factory) * OverMekConfig.getTierEnergyUsageFactor(tier);
    }

    public static boolean hasFactorySpecialization(int tier) {
        return OverMekConfig.getTierFactorySpeedFactor(tier) > 1.0D
            || OverMekConfig.getTierMaxBonus(tier, true) > OverMekConfig.getTierMaxBonus(tier, false);
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
        if (energyMultiplier == 1.0D) {
            return baseEnergyPerTick;
        }
        return baseEnergyPerTick.multiply(energyMultiplier);
    }

    public static int getCurrentTicksRequired(TileEntityMekanism tile) {
        if (tile instanceof TileEntityProgressMachine<?> progressMachine) {
            return progressMachine.getTicksRequired();
        }
        if (tile instanceof TileEntityFactory<?> factory) {
            return factory.getTicksRequired();
        }
        return -1;
    }

    private static double getBoardSpeedBonus(int tier, int overclockCount, boolean factory) {
        if (tier < 0 || overclockCount <= 0) {
            return 0.0D;
        }
        double rawBonus = overclockCount * OverMekConfig.getTierSpeedMultiplier(tier);
        if (rawBonus <= 0.0D) {
            return 0.0D;
        }
        if (factory) {
            rawBonus *= OverMekConfig.getTierFactorySpeedFactor(tier);
        }
        double tierCap = OverMekConfig.getTierMaxBonus(tier, factory);
        double cappedBonus = Math.min(rawBonus, tierCap);
        return Math.min(cappedBonus, OverMekConfig.getMaxOverclockBonus());
    }

    @Nullable
    private static ICircuitBoardHolder getHolder(TileEntityMekanism tile) {
        return tile.getCapability(CircuitBoardHolder.CIRCUIT_BOARD_CAPABILITY).resolve().orElse(null);
    }

    private static boolean matchesAnyRule(String className, List<? extends String> rules) {
        String simpleName = className.substring(className.lastIndexOf('.') + 1);
        for (String rawRule : rules) {
            if (rawRule == null) {
                continue;
            }
            String rule = rawRule.trim();
            if (rule.isEmpty()) {
                continue;
            }
            if (matchesRule(className, rule) || matchesRule(simpleName, rule)) {
                return true;
            }
        }
        return false;
    }

    private static boolean matchesRule(String value, String rule) {
        if (!rule.contains("*")) {
            return value.equals(rule);
        }
        String regex = "^" + Pattern.quote(rule).replace("\\*", "\\E.*\\Q") + "$";
        return Pattern.compile(regex).matcher(value).matches();
    }
}
