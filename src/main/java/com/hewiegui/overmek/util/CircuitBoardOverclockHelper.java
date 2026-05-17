package com.hewiegui.overmek.util;

import com.hewiegui.overmek.capability.ICircuitBoardHolder;
import com.hewiegui.overmek.config.OverMekConfig;
import com.hewiegui.overmek.item.CircuitBoardItem;
import com.hewiegui.overmek.mixin.AccessorTileEntityFactory;
import com.hewiegui.overmek.mixin.AccessorTileEntityProgressMachine;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.math.FloatingLong;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.factory.TileEntityFactory;
import mekanism.common.tile.prefab.TileEntityProgressMachine;
import mekanism.common.tile.prefab.TileEntityRecipeMachine;
import mekanism.common.util.MekanismUtils;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public final class CircuitBoardOverclockHelper {

    private CircuitBoardOverclockHelper() {
    }

    public static boolean canApplyCircuitBoardEffects(BlockEntity blockEntity) {
        return CircuitBoardProfileHelper.isSupportedMachine(blockEntity);
    }

    public static boolean shouldExposeCircuitBoardSlot(BlockEntity blockEntity, @Nullable ICircuitBoardHolder holder) {
        if (!(blockEntity instanceof TileEntityMekanism tile) || !tile.hasGui()) {
            return false;
        }
        return canApplyCircuitBoardEffects(blockEntity) || holder != null && holder.hasCircuitBoard();
    }

    public static int getInstalledTier(TileEntityMekanism tile) {
        ICircuitBoardHolder holder = getHolder(tile);
        return holder == null || !holder.hasCircuitBoard() ? -1 : holder.getTier();
    }

    @Nullable
    public static CircuitBoardChannel getInstalledChannel(TileEntityMekanism tile) {
        ICircuitBoardHolder holder = getHolder(tile);
        return holder == null ? null : holder.getBoardChannel();
    }

    public static ItemStack getInstalledStack(TileEntityMekanism tile) {
        ICircuitBoardHolder holder = getHolder(tile);
        return holder == null ? ItemStack.EMPTY : holder.getCircuitBoard();
    }

    public static int getOverclockBonus(TileEntityMekanism tile) {
        return (int) Math.floor(Math.max(0.0D, getDisplayedOverclockBonus(tile)));
    }

    public static double getDisplayedOverclockBonus(TileEntityMekanism tile) {
        ItemStack stack = getInstalledStack(tile);
        if (stack.isEmpty()) {
            return 0.0D;
        }
        return getDisplayState(tile, stack).overclockBonus();
    }

    public static double getEffectiveSpeedMultiplier(TileEntityMekanism tile) {
        return resolveProcessingSpeedMultiplier(tile);
    }

    public static double getEnergyUsageMultiplier(TileEntityMekanism tile) {
        return resolveProcessingEnergyMultiplier(tile);
    }

    public static double getWarmupRatio(TileEntityMekanism tile) {
        ICircuitBoardHolder holder = getHolder(tile);
        if (holder == null || !holder.hasCircuitBoard()) {
            return 1.0D;
        }
        BoardEffectProfile profile = BoardProfileLoader.getInstalledProfile(CircuitBoardProfileHelper.getSupportProfile(tile), holder.getCircuitBoard());
        if (!profile.isSupported() || !OverMekConfig.isWarmupEnabled() || profile.warmupTicks() <= 0) {
            return 1.0D;
        }
        return holder.getWarmupRatio(profile.warmupTicks());
    }

    public static int getWarmupProgress(TileEntityMekanism tile) {
        ICircuitBoardHolder holder = getHolder(tile);
        return holder == null ? 0 : holder.getWarmupProgress();
    }

    public static int getWarmupTicks(TileEntityMekanism tile) {
        ICircuitBoardHolder holder = getHolder(tile);
        if (holder == null || !holder.hasCircuitBoard()) {
            return 0;
        }
        return getWarmupTicks(tile, holder.getCircuitBoard());
    }

    public static int getWarmupTicksForTier(int tier) {
        return BoardProfileLoader.getProcessingProfile(tier).warmupTicks();
    }

    public static int getCircuitBoardTier(ItemStack stack) {
        return stack.getItem() instanceof CircuitBoardItem board ? board.getTier() : -1;
    }

    public static int getCircuitBoardOverclockCount(ItemStack stack) {
        return stack.getItem() instanceof CircuitBoardItem board ? board.getOverclockCount() : 0;
    }

    @Nullable
    public static CircuitBoardChannel getCircuitBoardChannel(ItemStack stack) {
        return stack.getItem() instanceof CircuitBoardItem board ? board.getChannel() : null;
    }

    public static boolean canInsertCircuitBoard(BlockEntity blockEntity, ItemStack stack) {
        return stack.getItem() instanceof CircuitBoardItem && CircuitBoardProfileHelper.acceptsBoard(blockEntity, stack);
    }

    public static double getBoardSpeedMultiplier(int tier, int overclockCount, boolean factory) {
        return getBoardSpeedMultiplier(tier, overclockCount, factory, 1.0D);
    }

    public static double getBoardSpeedMultiplier(int tier, int overclockCount, boolean factory, double warmupRatio) {
        BoardEffectProfile profile = BoardProfileLoader.getProcessingProfile(tier);
        return profile.getSpeedMultiplier(overclockCount, factory, OverMekConfig.getMaxOverclockBonus(), warmupRatio);
    }

    public static double getBoardEnergyUsageMultiplier(int tier, int overclockCount, boolean factory) {
        return getBoardEnergyUsageMultiplier(tier, overclockCount, factory, 1.0D);
    }

    public static double getBoardEnergyUsageMultiplier(int tier, int overclockCount, boolean factory, double warmupRatio) {
        BoardEffectProfile profile = BoardProfileLoader.getProcessingProfile(tier);
        return profile.getEnergyUsageMultiplier(overclockCount, factory, OverMekConfig.getMaxOverclockBonus(), warmupRatio, OverMekConfig.getOverclockEnergyMultiplier());
    }

    public static double getBoardEnergyCapacityMultiplier(int tier) {
        return BoardProfileLoader.getProcessingProfile(tier).energyCapacityFactor();
    }

    public static boolean hasFactorySpecialization(int tier) {
        BoardEffectProfile profile = BoardProfileLoader.getProcessingProfile(tier);
        return profile.factorySpeedFactor() > 1.0D || profile.factoryMaxBonus() > profile.maxBonus();
    }

    public static void tickWarmup(TileEntityMekanism tile, boolean active) {
        ICircuitBoardHolder holder = getHolder(tile);
        if (holder == null) {
            OverMekDebug.logWarmup(tile, active, 0, 0, BoardEffectProfile.unsupported(), "no-holder");
            return;
        }
        int before = holder.getWarmupProgress();
        if (!holder.hasCircuitBoard() || !OverMekConfig.isWarmupEnabled()) {
            holder.setWarmupProgress(0);
            holder.setGeneratorFuelRemainder(0.0D);
            OverMekDebug.logWarmup(tile, active, before, holder.getWarmupProgress(), BoardEffectProfile.unsupported(), holder.hasCircuitBoard() ? "warmup-disabled" : "no-board");
            return;
        }
        BoardEffectProfile profile = BoardProfileLoader.getInstalledProfile(CircuitBoardProfileHelper.getSupportProfile(tile), holder.getCircuitBoard());
        if (!profile.isSupported() || profile.warmupTicks() <= 0) {
            holder.setWarmupProgress(0);
            holder.setGeneratorFuelRemainder(0.0D);
            OverMekDebug.logWarmup(tile, active, before, holder.getWarmupProgress(), profile, profile.isSupported() ? "no-warmup-ticks" : "unsupported-profile");
            return;
        }
        int updatedWarmup = active
            ? Math.min(profile.warmupTicks(), holder.getWarmupProgress() + 1)
            : Math.max(0, holder.getWarmupProgress() - Math.max(1, profile.warmupCooldown()));
        holder.setWarmupProgress(updatedWarmup);
        OverMekDebug.logWarmup(tile, active, before, updatedWarmup, profile, "updated");
    }

    public static void resetWarmup(TileEntityMekanism tile) {
        ICircuitBoardHolder holder = getHolder(tile);
        if (holder != null) {
            holder.setWarmupProgress(0);
            holder.setGeneratorFuelRemainder(0.0D);
        }
    }

    public static boolean completeWarmup(TileEntityMekanism tile) {
        ICircuitBoardHolder holder = getHolder(tile);
        if (holder == null || !holder.hasCircuitBoard() || !OverMekConfig.isWarmupEnabled()) {
            OverMekDebug.logWarmup(tile, true, holder == null ? 0 : holder.getWarmupProgress(), holder == null ? 0 : holder.getWarmupProgress(), BoardEffectProfile.unsupported(), holder == null ? "complete-no-holder" : holder.hasCircuitBoard() ? "complete-warmup-disabled" : "complete-no-board");
            return false;
        }
        int before = holder.getWarmupProgress();
        BoardEffectProfile profile = BoardProfileLoader.getInstalledProfile(CircuitBoardProfileHelper.getSupportProfile(tile), holder.getCircuitBoard());
        if (!profile.isSupported() || profile.warmupTicks() <= 0) {
            OverMekDebug.logWarmup(tile, true, before, before, profile, profile.isSupported() ? "complete-no-warmup-ticks" : "complete-unsupported-profile");
            return false;
        }
        holder.setWarmupProgress(profile.warmupTicks());
        tile.setChanged();
        OverMekDebug.logWarmup(tile, true, before, profile.warmupTicks(), profile, "complete");
        return true;
    }

    public static int getAdjustedTicksRequired(TileEntityMekanism tile, int baseTicksRequired) {
        return ProcessingBoardService.getAdjustedTicksRequired(tile, baseTicksRequired);
    }

    public static FloatingLong getAdjustedEnergyPerTick(TileEntityMekanism tile, FloatingLong baseEnergyPerTick) {
        return ProcessingBoardService.getAdjustedEnergyPerTick(tile, baseEnergyPerTick);
    }

    public static FloatingLong getAdjustedMaxEnergy(TileEntityMekanism tile, FloatingLong baseMaxEnergy) {
        if (!canApplyCircuitBoardEffects(tile)) {
            return baseMaxEnergy;
        }
        ICircuitBoardHolder holder = getHolder(tile);
        if (holder == null || !holder.hasCircuitBoard()) {
            return baseMaxEnergy;
        }
        MachineSupportProfile supportProfile = CircuitBoardProfileHelper.getSupportProfile(tile);
        double capacityMultiplier = switch (supportProfile.machineProfile()) {
            case PROCESSING -> ProcessingBoardService.getEnergyCapacityMultiplier(holder.getCircuitBoard());
            case GENERATOR -> GeneratorBoardService.getEnergyCapacityMultiplier(tile, holder.getTier());
            default -> 1.0D;
        };
        return capacityMultiplier == 1.0D ? baseMaxEnergy : baseMaxEnergy.multiply(capacityMultiplier);
    }

    public static void syncAdjustedMaxEnergy(TileEntityMekanism tile, MachineEnergyContainer<?> energyContainer) {
        FloatingLong upgradedBaseMaxEnergy = MekanismUtils.getMaxEnergy(tile, energyContainer.getBaseMaxEnergy());
        energyContainer.setMaxEnergy(getAdjustedMaxEnergy(tile, upgradedBaseMaxEnergy));
    }

    public static void syncAdjustedMaxEnergy(TileEntityMekanism tile) {
        for (IEnergyContainer energyContainer : tile.getEnergyContainers(null)) {
            if (energyContainer instanceof MachineEnergyContainer<?> machineEnergyContainer) {
                syncAdjustedMaxEnergy(tile, machineEnergyContainer);
            }
        }
    }

    public static int getCurrentTicksRequired(TileEntityMekanism tile) {
        if (GeneratorBoardService.isSupportedGenerator(tile)) {
            return -1;
        }
        if (tile instanceof TileEntityProgressMachine<?> progressMachine) {
            return ((AccessorTileEntityProgressMachine) progressMachine).overmek$getSyncedTicksRequired();
        }
        if (tile instanceof TileEntityFactory<?> factory) {
            return ((AccessorTileEntityFactory) factory).overmek$getSyncedTicksRequired();
        }
        return -1;
    }

    public static int getBaseTicksRequired(TileEntityMekanism tile) {
        if (GeneratorBoardService.isSupportedGenerator(tile)) {
            return -1;
        }
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
        return getDisplayState(tile, stack).speedMultiplier();
    }

    public static double getActualWarmupRatio(TileEntityMekanism tile, ItemStack stack) {
        MachineSupportProfile supportProfile = CircuitBoardProfileHelper.getSupportProfile(tile);
        if (supportProfile.machineProfile() != CircuitBoardMachineProfile.PROCESSING) {
            return getDisplayedWarmupRatio(tile, stack);
        }
        BoardEffectProfile profile = BoardProfileLoader.getInstalledProfile(supportProfile, stack);
        int overclockCount = getCircuitBoardOverclockCount(stack);
        if (!profile.isSupported() || overclockCount <= 0) {
            return 1.0D;
        }
        double fullSpeed = profile.getSpeedMultiplier(overclockCount, tile instanceof TileEntityFactory<?>, OverMekConfig.getMaxOverclockBonus(), 1.0D);
        double actualSpeed = getActualSpeedMultiplier(tile);
        double numerator = Math.max(0.0D, actualSpeed - 1.0D);
        double denominator = Math.max(0.0001D, fullSpeed - 1.0D);
        return Math.min(1.0D, numerator / denominator);
    }

    public static double getDisplayedWarmupRatio(TileEntityMekanism tile, ItemStack stack) {
        BoardEffectProfile profile = BoardProfileLoader.getInstalledProfile(CircuitBoardProfileHelper.getSupportProfile(tile), stack);
        if (!profile.isSupported() || !OverMekConfig.isWarmupEnabled() || profile.warmupTicks() <= 0) {
            return 1.0D;
        }
        int syncedWarmup = getDisplayedWarmupProgress(tile);
        return Math.min(1.0D, Math.max(0.0D, syncedWarmup / (double) profile.warmupTicks()));
    }

    public static double getActualEnergyUsageMultiplier(TileEntityMekanism tile, ItemStack stack) {
        MachineSupportProfile supportProfile = CircuitBoardProfileHelper.getSupportProfile(tile);
        if (supportProfile.machineProfile() != CircuitBoardMachineProfile.PROCESSING) {
            return 1.0D;
        }
        BoardEffectProfile profile = BoardProfileLoader.getInstalledProfile(supportProfile, stack);
        int overclockCount = getCircuitBoardOverclockCount(stack);
        if (!profile.isSupported() || overclockCount <= 0) {
            return 1.0D;
        }
        return profile.getEnergyUsageMultiplier(overclockCount, tile instanceof TileEntityFactory<?>, OverMekConfig.getMaxOverclockBonus(), getActualWarmupRatio(tile, stack), OverMekConfig.getOverclockEnergyMultiplier());
    }

    public static double getDisplayedEnergyUsageMultiplier(TileEntityMekanism tile, ItemStack stack) {
        return getDisplayState(tile, stack).energyMultiplier();
    }

    public static int getDisplayedWarmupProgress(TileEntityMekanism tile) {
        if (tile instanceof ICircuitBoardDisplayData displayData) {
            return displayData.overmek$getSyncedWarmupProgress();
        }
        return getWarmupProgress(tile);
    }

    public static boolean usesRecipeMachineExtraPasses(TileEntityMekanism tile) {
        return CircuitBoardProfileHelper.getMachineProfile(tile) == CircuitBoardMachineProfile.PROCESSING
            && tile instanceof TileEntityRecipeMachine<?> && !(tile instanceof TileEntityProgressMachine<?>) && !(tile instanceof TileEntityFactory<?>);
    }

    public static int getExtraRecipePasses(TileEntityMekanism tile) {
        return usesRecipeMachineExtraPasses(tile) ? ProcessingBoardService.getExtraRecipePasses(tile) : 0;
    }

    public static int getFullWarmupTicksRequired(TileEntityMekanism tile, ItemStack stack) {
        BoardEffectProfile profile = BoardProfileLoader.getInstalledProfile(CircuitBoardProfileHelper.getSupportProfile(tile), stack);
        return profile.isSupported() ? profile.warmupTicks() : -1;
    }

    public static double getFullSpeedMultiplier(TileEntityMekanism tile, ItemStack stack) {
        BoardEffectProfile profile = BoardProfileLoader.getInstalledProfile(CircuitBoardProfileHelper.getSupportProfile(tile), stack);
        MachineSupportProfile supportProfile = CircuitBoardProfileHelper.getSupportProfile(tile);
        if (!profile.isSupported()) {
            return 1.0D;
        }
        return switch (supportProfile.machineProfile()) {
            case PROCESSING -> profile.getSpeedMultiplier(getCircuitBoardOverclockCount(stack), tile instanceof TileEntityFactory<?>, OverMekConfig.getMaxOverclockBonus(), 1.0D);
            case GENERATOR -> profile.generationMultiplier();
            case FISSION -> profile.speedMultiplier();
            case POWER_MULTIBLOCK -> profile.generationMultiplier();
            case EVAPORATION_MULTIBLOCK -> profile.throughputMultiplier();
            case SPS_MULTIBLOCK -> profile.throughputMultiplier();
            case UNSUPPORTED -> 1.0D;
        };
    }

    public static BoardSlotDisplayState getDisplayState(TileEntityMekanism tile, ItemStack stack) {
        MachineSupportProfile supportProfile = CircuitBoardProfileHelper.getSupportProfile(tile);
        if (stack.isEmpty()) {
            return BoardSlotDisplayState.empty(supportProfile);
        }
        boolean compatible = canInsertCircuitBoard(tile, stack);
        double warmupRatio = compatible ? getDisplayedWarmupRatio(tile, stack) : 0.0D;
        if (!compatible) {
            return new BoardSlotDisplayState(supportProfile, true, false, supportProfile.isSupported(), warmupRatio, 1.0D, 0.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, 1.0D, -1);
        }
        return switch (supportProfile.tooltipCategory()) {
            case PROCESSING -> new BoardSlotDisplayState(
                supportProfile,
                true,
                true,
                true,
                warmupRatio,
                ProcessingBoardService.getDisplayedSpeedMultiplier(tile, stack),
                ProcessingBoardService.getDisplayedOverclockBonus(tile, stack),
                ProcessingBoardService.getDisplayedEnergyUsageMultiplier(tile, stack),
                1.0D,
                1.0D,
                1.0D,
                1.0D,
                1.0D,
                1.0D,
                1.0D,
                1.0D,
                1.0D,
                getCurrentTicksRequired(tile)
            );
            case GENERATOR -> new BoardSlotDisplayState(
                supportProfile,
                true,
                true,
                true,
                warmupRatio,
                1.0D,
                0.0D,
                1.0D,
                GeneratorBoardService.getDisplayedGenerationMultiplier(tile, stack),
                1.0D,
                1.0D,
                1.0D,
                GeneratorBoardService.isFuelGenerator(tile) ? GeneratorBoardService.getDisplayedFuelConsumptionMultiplier(tile, stack) : 1.0D,
                GeneratorBoardService.getEnergyCapacityMultiplier(tile, getCircuitBoardTier(stack)),
                1.0D,
                1.0D,
                1.0D,
                -1
            );
            case FISSION -> new BoardSlotDisplayState(
                supportProfile,
                true,
                true,
                true,
                warmupRatio,
                1.0D,
                0.0D,
                1.0D,
                1.0D,
                MultiblockBoardService.getDisplayedFissionEfficiencyMultiplier(tile, stack),
                MultiblockBoardService.getDisplayedFissionStabilityMultiplier(tile, stack),
                1.0D,
                1.0D,
                MultiblockBoardService.getDisplayedBufferMultiplier(tile, stack),
                1.0D,
                1.0D,
                1.0D,
                -1
            );
            case POWER_MULTIBLOCK -> new BoardSlotDisplayState(
                supportProfile,
                true,
                true,
                true,
                warmupRatio,
                1.0D,
                0.0D,
                1.0D,
                MultiblockBoardService.getDisplayedPowerGenerationMultiplier(tile, stack),
                1.0D,
                1.0D,
                1.0D,
                MultiblockBoardService.getDisplayedPowerFuelMultiplier(tile, stack),
                MultiblockBoardService.getDisplayedBufferMultiplier(tile, stack),
                MultiblockBoardService.getDisplayedMatrixCapacityMultiplier(tile, stack),
                MultiblockBoardService.getDisplayedMatrixTransferMultiplier(tile, stack),
                1.0D,
                -1
            );
            case EVAPORATION_MULTIBLOCK -> new BoardSlotDisplayState(
                supportProfile,
                true,
                true,
                true,
                warmupRatio,
                1.0D,
                0.0D,
                1.0D,
                1.0D,
                1.0D,
                1.0D,
                MultiblockBoardService.getDisplayedEvaporationThroughputMultiplier(tile, stack),
                1.0D,
                MultiblockBoardService.getDisplayedBufferMultiplier(tile, stack),
                1.0D,
                1.0D,
                1.0D,
                -1
            );
            case SPS_MULTIBLOCK -> new BoardSlotDisplayState(
                supportProfile,
                true,
                true,
                true,
                warmupRatio,
                1.0D,
                0.0D,
                MultiblockBoardService.getDisplayedSpsEnergyUsageMultiplier(tile, stack),
                1.0D,
                1.0D,
                MultiblockBoardService.getDisplayedSpsStabilityMultiplier(tile, stack),
                MultiblockBoardService.getDisplayedSpsThroughputMultiplier(tile, stack),
                1.0D,
                MultiblockBoardService.getDisplayedSpsBufferMultiplier(tile, stack),
                1.0D,
                1.0D,
                MultiblockBoardService.getDisplayedSpsPressureMultiplier(tile, stack),
                -1
            );
            case UNSUPPORTED -> BoardSlotDisplayState.empty(supportProfile);
        };
    }

    private static double resolveProcessingSpeedMultiplier(TileEntityMekanism tile) {
        if (!OverMekConfig.isOverclockEnabled() || !canApplyCircuitBoardEffects(tile)) {
            return 1.0D;
        }
        if (tile instanceof TileEntityFactory<?> && !OverMekConfig.isFactoryOverclockEnabled()) {
            return 1.0D;
        }
        return CircuitBoardProfileHelper.getMachineProfile(tile) == CircuitBoardMachineProfile.PROCESSING
            ? ProcessingBoardService.getEffectiveSpeedMultiplier(tile)
            : 1.0D;
    }

    private static double resolveProcessingEnergyMultiplier(TileEntityMekanism tile) {
        if (CircuitBoardProfileHelper.getMachineProfile(tile) != CircuitBoardMachineProfile.PROCESSING) {
            return 1.0D;
        }
        return ProcessingBoardService.getEnergyUsageMultiplier(tile);
    }

    @Nullable
    private static ICircuitBoardHolder getHolder(TileEntityMekanism tile) {
        return BoardHostResolver.resolveHolder(tile);
    }

    private static int getWarmupTicks(TileEntityMekanism tile, ItemStack stack) {
        BoardEffectProfile profile = BoardProfileLoader.getInstalledProfile(CircuitBoardProfileHelper.getSupportProfile(tile), stack);
        return profile.isSupported() ? profile.warmupTicks() : 0;
    }
}
