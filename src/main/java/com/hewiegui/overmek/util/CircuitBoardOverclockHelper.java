package com.hewiegui.overmek.util;

import com.hewiegui.overmek.capability.CircuitBoardHolder;
import com.hewiegui.overmek.capability.ICircuitBoardHolder;
import com.hewiegui.overmek.config.OverMekConfig;
import com.hewiegui.overmek.item.CircuitBoardItem;
import com.hewiegui.overmek.mixin.AccessorTileEntityFactory;
import com.hewiegui.overmek.mixin.AccessorTileEntityProgressMachine;
import java.util.List;
import java.util.regex.Pattern;
import mekanism.api.math.FloatingLong;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.factory.TileEntityFactory;
import mekanism.common.tile.prefab.TileEntityProgressMachine;
import mekanism.common.util.MekanismUtils;
import net.minecraft.world.item.ItemStack;
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
        return getBoardSpeedMultiplier(holder.getTier(), holder.getOverclockCount(), tile instanceof TileEntityFactory<?>, getWarmupRatio(holder));
    }

    public static double getEnergyUsageMultiplier(TileEntityMekanism tile) {
        ICircuitBoardHolder holder = getHolder(tile);
        if (holder == null || !holder.hasCircuitBoard()) {
            return 1.0D;
        }
        return getBoardEnergyUsageMultiplier(holder.getTier(), holder.getOverclockCount(), tile instanceof TileEntityFactory<?>, getWarmupRatio(holder));
    }

    public static double getWarmupRatio(TileEntityMekanism tile) {
        ICircuitBoardHolder holder = getHolder(tile);
        if (holder == null || !holder.hasCircuitBoard()) {
            return 1.0D;
        }
        return getWarmupRatio(holder);
    }

    public static int getWarmupProgress(TileEntityMekanism tile) {
        ICircuitBoardHolder holder = getHolder(tile);
        return holder == null ? 0 : holder.getWarmupProgress();
    }

    public static int getWarmupTicks(TileEntityMekanism tile) {
        int tier = getInstalledTier(tile);
        return tier < 0 ? 0 : OverMekConfig.getTierWarmupTicks(tier);
    }

    public static int getWarmupTicksForTier(int tier) {
        return tier < 0 ? 0 : OverMekConfig.getTierWarmupTicks(tier);
    }

    public static int getCircuitBoardTier(ItemStack stack) {
        return stack.getItem() instanceof CircuitBoardItem board ? board.getTier() : -1;
    }

    public static int getCircuitBoardOverclockCount(ItemStack stack) {
        return stack.getItem() instanceof CircuitBoardItem board ? board.getOverclockCount() : 0;
    }

    public static double getBoardSpeedMultiplier(int tier, int overclockCount, boolean factory) {
        return getBoardSpeedMultiplier(tier, overclockCount, factory, 1.0D);
    }

    public static double getBoardSpeedMultiplier(int tier, int overclockCount, boolean factory, double warmupRatio) {
        double fullSpeedBonus = getBoardSpeedBonus(tier, overclockCount, factory);
        if (fullSpeedBonus <= 0.0D) {
            return 1.0D;
        }
        double clampedWarmup = Math.min(1.0D, Math.max(0.0D, warmupRatio));
        return 1.0D + fullSpeedBonus * clampedWarmup;
    }

    public static double getBoardEnergyUsageMultiplier(int tier, int overclockCount, boolean factory) {
        return getBoardEnergyUsageMultiplier(tier, overclockCount, factory, 1.0D);
    }

    public static double getBoardEnergyUsageMultiplier(int tier, int overclockCount, boolean factory, double warmupRatio) {
        double speedMultiplier = getBoardSpeedMultiplier(tier, overclockCount, factory, warmupRatio);
        return speedMultiplier * OverMekConfig.getTierEnergyUsageFactor(tier) * OverMekConfig.getOverclockEnergyMultiplier();
    }

    public static double getBoardEnergyCapacityMultiplier(int tier) {
        if (tier < 0) {
            return 1.0D;
        }
        return OverMekConfig.getTierEnergyCapacityFactor(tier);
    }

    public static boolean hasFactorySpecialization(int tier) {
        return OverMekConfig.getTierFactorySpeedFactor(tier) > 1.0D
            || OverMekConfig.getTierMaxBonus(tier, true) > OverMekConfig.getTierMaxBonus(tier, false);
    }

    public static void tickWarmup(TileEntityMekanism tile, boolean active) {
        ICircuitBoardHolder holder = getHolder(tile);
        if (holder == null) {
            return;
        }
        if (!holder.hasCircuitBoard() || !OverMekConfig.isWarmupEnabled()) {
            holder.setWarmupProgress(0);
            return;
        }
        int tier = holder.getTier();
        int warmupTicks = OverMekConfig.getTierWarmupTicks(tier);
        if (warmupTicks <= 0) {
            holder.setWarmupProgress(0);
            return;
        }
        int currentWarmup = holder.getWarmupProgress();
        int updatedWarmup = active
            ? Math.min(warmupTicks, currentWarmup + 1)
            : Math.max(0, currentWarmup - OverMekConfig.getTierWarmupCooldown(tier));
        holder.setWarmupProgress(updatedWarmup);
    }

    public static void resetWarmup(TileEntityMekanism tile) {
        ICircuitBoardHolder holder = getHolder(tile);
        if (holder != null) {
            holder.setWarmupProgress(0);
        }
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

    public static FloatingLong getAdjustedMaxEnergy(TileEntityMekanism tile, FloatingLong baseMaxEnergy) {
        if (!canApplyCircuitBoardEffects(tile)) {
            return baseMaxEnergy;
        }
        ICircuitBoardHolder holder = getHolder(tile);
        if (holder == null || !holder.hasCircuitBoard()) {
            return baseMaxEnergy;
        }
        double capacityMultiplier = getBoardEnergyCapacityMultiplier(holder.getTier());
        if (capacityMultiplier == 1.0D) {
            return baseMaxEnergy;
        }
        return baseMaxEnergy.multiply(capacityMultiplier);
    }

    public static void syncAdjustedMaxEnergy(TileEntityMekanism tile, MachineEnergyContainer<?> energyContainer) {
        FloatingLong upgradedBaseMaxEnergy = MekanismUtils.getMaxEnergy(tile, energyContainer.getBaseMaxEnergy());
        energyContainer.setMaxEnergy(getAdjustedMaxEnergy(tile, upgradedBaseMaxEnergy));
    }

    public static int getCurrentTicksRequired(TileEntityMekanism tile) {
        if (tile instanceof TileEntityProgressMachine<?> progressMachine) {
            return ((AccessorTileEntityProgressMachine) progressMachine).overmek$getSyncedTicksRequired();
        }
        if (tile instanceof TileEntityFactory<?> factory) {
            return ((AccessorTileEntityFactory) factory).overmek$getSyncedTicksRequired();
        }
        return -1;
    }

    public static int getBaseTicksRequired(TileEntityMekanism tile) {
        int baseTicksRequired;
        if (tile instanceof TileEntityProgressMachine<?> progressMachine) {
            baseTicksRequired = ((AccessorTileEntityProgressMachine) progressMachine).overmek$getBaseTicksRequired();
        } else if (tile instanceof TileEntityFactory<?>) {
            baseTicksRequired = 200;
        } else {
            return -1;
        }
        return MekanismUtils.getTicks(tile, baseTicksRequired);
    }

    public static double getActualSpeedMultiplier(TileEntityMekanism tile) {
        int baseTicks = getBaseTicksRequired(tile);
        int currentTicks = getCurrentTicksRequired(tile);
        if (baseTicks <= 0 || currentTicks <= 0) {
            return 1.0D;
        }
        return Math.max(1.0D, baseTicks / (double) currentTicks);
    }

    public static double getDisplayedSpeedMultiplier(TileEntityMekanism tile, ItemStack stack) {
        int tier = getCircuitBoardTier(stack);
        int overclockCount = getCircuitBoardOverclockCount(stack);
        if (tier < 0 || overclockCount <= 0) {
            return 1.0D;
        }
        return getBoardSpeedMultiplier(
            tier,
            overclockCount,
            tile instanceof TileEntityFactory<?>,
            getDisplayedWarmupRatio(tile, stack)
        );
    }

    public static double getActualWarmupRatio(TileEntityMekanism tile, ItemStack stack) {
        int tier = getCircuitBoardTier(stack);
        int overclockCount = getCircuitBoardOverclockCount(stack);
        if (tier < 0 || overclockCount <= 0) {
            return 1.0D;
        }
        double fullSpeed = getBoardSpeedMultiplier(tier, overclockCount, tile instanceof TileEntityFactory<?>);
        double actualSpeed = getActualSpeedMultiplier(tile);
        double numerator = Math.max(0.0D, actualSpeed - 1.0D);
        double denominator = Math.max(0.0001D, fullSpeed - 1.0D);
        return Math.min(1.0D, numerator / denominator);
    }

    public static double getDisplayedWarmupRatio(TileEntityMekanism tile, ItemStack stack) {
        int tier = getCircuitBoardTier(stack);
        int overclockCount = getCircuitBoardOverclockCount(stack);
        if (tier < 0 || overclockCount <= 0) {
            return 1.0D;
        }
        int warmupTicks = getWarmupTicksForTier(tier);
        if (!OverMekConfig.isWarmupEnabled() || warmupTicks <= 0) {
            return 1.0D;
        }
        int syncedWarmup = getDisplayedWarmupProgress(tile);
        return Math.min(1.0D, Math.max(0.0D, syncedWarmup / (double) warmupTicks));
    }

    public static double getActualEnergyUsageMultiplier(TileEntityMekanism tile, ItemStack stack) {
        int tier = getCircuitBoardTier(stack);
        int overclockCount = getCircuitBoardOverclockCount(stack);
        if (tier < 0 || overclockCount <= 0) {
            return 1.0D;
        }
        return getBoardEnergyUsageMultiplier(
            tier,
            overclockCount,
            tile instanceof TileEntityFactory<?>,
            getActualWarmupRatio(tile, stack)
        );
    }

    public static double getDisplayedEnergyUsageMultiplier(TileEntityMekanism tile, ItemStack stack) {
        int tier = getCircuitBoardTier(stack);
        int overclockCount = getCircuitBoardOverclockCount(stack);
        if (tier < 0 || overclockCount <= 0) {
            return 1.0D;
        }
        return getBoardEnergyUsageMultiplier(
            tier,
            overclockCount,
            tile instanceof TileEntityFactory<?>,
            getDisplayedWarmupRatio(tile, stack)
        );
    }

    public static int getDisplayedWarmupProgress(TileEntityMekanism tile) {
        if (tile instanceof ICircuitBoardDisplayData displayData) {
            return displayData.overmek$getSyncedWarmupProgress();
        }
        return getWarmupProgress(tile);
    }

    public static int getFullWarmupTicksRequired(TileEntityMekanism tile, ItemStack stack) {
        int tier = getCircuitBoardTier(stack);
        int overclockCount = getCircuitBoardOverclockCount(stack);
        int baseTicks = getBaseTicksRequired(tile);
        if (tier < 0 || overclockCount <= 0 || baseTicks <= 0) {
            return -1;
        }
        double fullSpeed = getBoardSpeedMultiplier(tier, overclockCount, tile instanceof TileEntityFactory<?>);
        if (fullSpeed <= 1.0D) {
            return baseTicks;
        }
        return Math.max(1, (int) Math.ceil(baseTicks / fullSpeed));
    }

    public static double getFullSpeedMultiplier(TileEntityMekanism tile, ItemStack stack) {
        int tier = getCircuitBoardTier(stack);
        int overclockCount = getCircuitBoardOverclockCount(stack);
        if (tier < 0 || overclockCount <= 0) {
            return 1.0D;
        }
        return getBoardSpeedMultiplier(tier, overclockCount, tile instanceof TileEntityFactory<?>);
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

    private static double getWarmupRatio(ICircuitBoardHolder holder) {
        if (!OverMekConfig.isWarmupEnabled()) {
            return 1.0D;
        }
        int tier = holder.getTier();
        int warmupTicks = OverMekConfig.getTierWarmupTicks(tier);
        return holder.getWarmupRatio(warmupTicks);
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
