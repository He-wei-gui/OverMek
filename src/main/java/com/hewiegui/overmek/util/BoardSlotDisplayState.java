package com.hewiegui.overmek.util;

public record BoardSlotDisplayState(
    MachineSupportProfile supportProfile,
    boolean hasBoard,
    boolean compatibleBoard,
    boolean showWarmupBar,
    double warmupRatio,
    double speedMultiplier,
    double overclockBonus,
    double energyMultiplier,
    double generationMultiplier,
    double coolingMultiplier,
    double stabilityMultiplier,
    double throughputMultiplier,
    double fuelMultiplier,
    double capacityMultiplier,
    double matrixCapacityMultiplier,
    double matrixTransferMultiplier,
    double pressureMultiplier,
    int ticksRequired
) {

    public static BoardSlotDisplayState empty(MachineSupportProfile supportProfile) {
        return new BoardSlotDisplayState(
            supportProfile,
            false,
            true,
            supportProfile.isSupported(),
            0.0D,
            1.0D,
            0.0D,
            1.0D,
            1.0D,
            1.0D,
            1.0D,
            1.0D,
            1.0D,
            1.0D,
            1.0D,
            1.0D,
            1.0D,
            -1
        );
    }
}
