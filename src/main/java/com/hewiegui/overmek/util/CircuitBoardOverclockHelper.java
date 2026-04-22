package com.hewiegui.overmek.util;

import com.hewiegui.overmek.capability.CircuitBoardHolder;
import com.hewiegui.overmek.config.OverMekConfig;
import mekanism.common.tile.factory.TileEntityFactory;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.prefab.TileEntityProgressMachine;

public final class CircuitBoardOverclockHelper {

    private CircuitBoardOverclockHelper() {
    }

    public static int getOverclockBonus(TileEntityMekanism tile) {
        if (!OverMekConfig.isOverclockEnabled()) {
            return 0;
        }
        if (tile instanceof TileEntityFactory<?> && !OverMekConfig.isFactoryOverclockEnabled()) {
            return 0;
        }

        var holder = tile.getCapability(CircuitBoardHolder.CIRCUIT_BOARD_CAPABILITY).resolve().orElse(null);
        if (holder == null || !holder.hasCircuitBoard()) {
            return 0;
        }

        int overclockCount = holder.getOverclockCount();
        if (overclockCount <= 0) {
            return 0;
        }

        double tierMultiplier = OverMekConfig.getTierMultiplier(holder.getTier());
        int bonus = (int) Math.floor(overclockCount * tierMultiplier);
        return Math.min(bonus, OverMekConfig.getMaxOverclockBonus());
    }

    public static int getAdjustedTicksRequired(TileEntityMekanism tile, int baseTicksRequired) {
        int bonus = getOverclockBonus(tile);
        if (bonus <= 0 || baseTicksRequired <= 1) {
            return baseTicksRequired;
        }
        return Math.max(1, (int) Math.ceil(baseTicksRequired / (double) (bonus + 1)));
    }

    public static double getEffectiveSpeedMultiplier(TileEntityMekanism tile) {
        return getOverclockBonus(tile) + 1.0D;
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
}
