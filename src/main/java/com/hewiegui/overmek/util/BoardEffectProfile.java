package com.hewiegui.overmek.util;

public record BoardEffectProfile(
    CircuitBoardChannel channel,
    int tier,
    double speedMultiplier,
    double energyUsageFactor,
    double factorySpeedFactor,
    double energyCapacityFactor,
    double maxBonus,
    double factoryMaxBonus,
    double generationMultiplier,
    double fuelMultiplier,
    double bufferMultiplier,
    double matrixCapacityMultiplier,
    double matrixTransferMultiplier,
    double throughputMultiplier,
    double stabilityMultiplier,
    double pressureMultiplier,
    int warmupTicks,
    int warmupCooldown
) {

    public static BoardEffectProfile unsupported() {
        return new BoardEffectProfile(null, -1, 1.0D, 1.0D, 1.0D, 1.0D, 0.0D, 0.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 0, 1);
    }

    public boolean isSupported() {
        return channel != null;
    }

    public double clampWarmup(double warmupRatio) {
        return Math.max(0.0D, Math.min(1.0D, warmupRatio));
    }

    public double scaleMultiplier(double fullMultiplier, double warmupRatio) {
        return 1.0D + Math.max(0.0D, fullMultiplier - 1.0D) * clampWarmup(warmupRatio);
    }

    public double scaleValue(double targetValue, double warmupRatio) {
        return 1.0D + (targetValue - 1.0D) * clampWarmup(warmupRatio);
    }

    public double getSpeedBonus(int overclockCount, boolean factory, int globalMaxBonus) {
        if (!isSupported() || overclockCount <= 0) {
            return 0.0D;
        }
        double rawBonus = overclockCount * speedMultiplier;
        if (rawBonus <= 0.0D) {
            return 0.0D;
        }
        if (factory) {
            rawBonus *= factorySpeedFactor;
        }
        double tierCap = factory ? factoryMaxBonus : maxBonus;
        return Math.min(Math.min(rawBonus, tierCap), globalMaxBonus);
    }

    public double getSpeedMultiplier(int overclockCount, boolean factory, int globalMaxBonus, double warmupRatio) {
        return 1.0D + getSpeedBonus(overclockCount, factory, globalMaxBonus) * clampWarmup(warmupRatio);
    }

    public double getEnergyUsageMultiplier(int overclockCount, boolean factory, int globalMaxBonus, double warmupRatio, double globalEnergyMultiplier) {
        return getSpeedMultiplier(overclockCount, factory, globalMaxBonus, warmupRatio) * energyUsageFactor * globalEnergyMultiplier;
    }
}
