package com.hewiegui.overmek.util;

import com.hewiegui.overmek.capability.CircuitBoardHolder;
import com.hewiegui.overmek.capability.ICircuitBoardHolder;
import com.hewiegui.overmek.config.OverMekConfig;
import java.util.List;
import java.util.regex.Pattern;
import mekanism.api.math.FloatingLong;
import mekanism.common.tile.factory.TileEntityFactory;
import mekanism.common.tile.base.TileEntityMekanism;
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

        double baseBonus = holder.getOverclockCount() * OverMekConfig.getTierSpeedMultiplier(holder.getTier());
        if (baseBonus <= 0.0D) {
            return 1.0D;
        }

        if (tile instanceof TileEntityFactory<?>) {
            baseBonus *= OverMekConfig.getTierFactorySpeedFactor(holder.getTier());
        }
        double cappedBonus = Math.min(baseBonus, OverMekConfig.getMaxOverclockBonus());
        return cappedBonus + 1.0D;
    }

    public static double getEnergyUsageMultiplier(TileEntityMekanism tile) {
        ICircuitBoardHolder holder = getHolder(tile);
        if (holder == null || !holder.hasCircuitBoard()) {
            return 1.0D;
        }
        return getEffectiveSpeedMultiplier(tile) * OverMekConfig.getTierEnergyUsageFactor(holder.getTier());
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
