package com.hewiegui.overmek.util;

import com.hewiegui.overmek.capability.CircuitBoardHolder;
import mekanism.common.tile.base.TileEntityMekanism;

public final class CircuitBoardOverclockHelper {

    private static final int MAX_OVERCLOCK_BONUS = 10;

    private CircuitBoardOverclockHelper() {
    }

    public static int getOverclockBonus(TileEntityMekanism tile) {
        var holder = tile.getCapability(CircuitBoardHolder.CIRCUIT_BOARD_CAPABILITY).resolve().orElse(null);
        if (holder == null || !holder.hasCircuitBoard()) {
            return 0;
        }

        int overclockCount = holder.getOverclockCount();
        if (overclockCount <= 0) {
            return 0;
        }

        float tierMultiplier = switch (holder.getTier()) {
            case 0 -> 1.0F;
            case 1 -> 1.5F;
            case 2 -> 2.0F;
            case 3 -> 3.0F;
            default -> 1.0F;
        };

        int bonus = (int) (overclockCount * tierMultiplier);
        return Math.min(bonus, MAX_OVERCLOCK_BONUS);
    }

    public static int getAdjustedTicksRequired(TileEntityMekanism tile, int baseTicksRequired) {
        int bonus = getOverclockBonus(tile);
        if (bonus <= 0 || baseTicksRequired <= 1) {
            return baseTicksRequired;
        }
        return Math.max(1, (int) Math.ceil(baseTicksRequired / (double) (bonus + 1)));
    }
}
