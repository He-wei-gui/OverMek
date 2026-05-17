package com.hewiegui.overmek.util;

import com.hewiegui.overmek.capability.CircuitBoardHolder;
import com.hewiegui.overmek.capability.ICircuitBoardHolder;
import com.hewiegui.overmek.config.OverMekConfig;
import com.hewiegui.overmek.item.CircuitBoardItem;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.WeakHashMap;
import mekanism.common.tile.base.TileEntityMekanism;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.registries.ForgeRegistries;
import org.jetbrains.annotations.Nullable;

public final class OverMekDebug {

    private static final long LOG_INTERVAL_TICKS = 100L;
    private static final Map<BlockEntity, Map<String, Long>> LAST_LOG_TICKS = new WeakHashMap<>();

    private OverMekDebug() {
    }

    public static boolean shouldLog() {
        return OverMekConfig.isDebugLoggingEnabled();
    }

    public static boolean shouldLogThrottled(@Nullable BlockEntity blockEntity, String key) {
        if (!shouldLog() || blockEntity == null) {
            return false;
        }
        Level level = blockEntity.getLevel();
        if (level == null) {
            return true;
        }
        long now = level.getGameTime();
        synchronized (LAST_LOG_TICKS) {
            Map<String, Long> byKey = LAST_LOG_TICKS.computeIfAbsent(blockEntity, ignored -> new HashMap<>());
            long last = byKey.getOrDefault(key, Long.MIN_VALUE);
            if (last != Long.MIN_VALUE && now - last < LOG_INTERVAL_TICKS) {
                return false;
            }
            byKey.put(key, now);
            return true;
        }
    }

    public static void logHostResolution(BlockEntity source, @Nullable BlockEntity host, int locationsCount, String reason) {
        if (!shouldLogThrottled(source, "resolveHost:" + reason)) {
            return;
        }
        OverMekLog.debug(
            "OverMek debug host resolution: source={}, host={}, locations={}, reason={}",
            describeTile(source),
            describeTile(host),
            locationsCount,
            reason
        );
    }

    public static void logHolderResolution(BlockEntity source, @Nullable BlockEntity host, @Nullable ICircuitBoardHolder holder) {
        if (!shouldLogThrottled(source, "resolveHolder")) {
            return;
        }
        OverMekLog.debug(
            "OverMek debug holder resolution: source={}, host={}, hostCapability={}, holder={}, board={}",
            describeTile(source),
            describeTile(host),
            hasCapability(host),
            holder != null,
            describeBoard(holder == null ? ItemStack.EMPTY : holder.getCircuitBoard())
        );
    }

    public static void logInstallAttempt(BlockEntity clicked, BlockEntity host, boolean acceptsBoard, @Nullable ICircuitBoardHolder holder, ItemStack stack, String stage) {
        OverMekLog.debug(
            "OverMek debug board install {}: clicked={}, host={}, acceptsBoard={}, holder={}, hostCapability={}, stack={}, support={}, reason={}",
            stage,
            describeTile(clicked),
            describeTile(host),
            acceptsBoard,
            holder != null,
            hasCapability(host),
            describeBoard(stack),
            describeSupport(CircuitBoardProfileHelper.getSupportProfile(clicked)),
            getProfileFailureReason(clicked, stack)
        );
    }

    public static void logWarmup(TileEntityMekanism tile, boolean active, int before, int after, BoardEffectProfile profile, String reason) {
        if (!shouldLogThrottled(tile, "warmup:" + reason + ":" + active + ":" + after)) {
            return;
        }
        OverMekLog.debug(
            "OverMek debug warmup: tile={}, active={}, before={}, after={}, profileSupported={}, warmupTicks={}, ratio={}, reason={}",
            describeTile(tile),
            active,
            before,
            after,
            profile.isSupported(),
            profile.warmupTicks(),
            profile.isSupported() && profile.warmupTicks() > 0 ? clampRatio(after, profile.warmupTicks()) : 1.0D,
            reason
        );
    }

    public static void logMultiblockEffects(TileEntityMekanism tile, String key, String values) {
        if (!shouldLogThrottled(tile, "effects:" + key)) {
            return;
        }
        OverMekLog.debug(
            "OverMek debug multiblock effects [{}]: tile={}, state={}, {}",
            key,
            describeTile(tile),
            describeState(tile),
            values
        );
    }

    public static void logEvaporationTick(TileEntityMekanism tile, double lastGain, double throughputMultiplier) {
        if (!shouldLogThrottled(tile, "evaporationTick")) {
            return;
        }
        OverMekLog.debug(
            "OverMek debug evaporation tick: tile={}, lastGain={}, throughputMultiplier={}, state={}",
            describeTile(tile),
            lastGain,
            throughputMultiplier,
            describeState(tile)
        );
    }

    public static void logEvaporationOperations(TileEntityMekanism tile, int baseline, int scaled, double throughputMultiplier) {
        if (!shouldLogThrottled(tile, "evaporationOperations")) {
            return;
        }
        OverMekLog.debug(
            "OverMek debug evaporation operations: tile={}, baseline={}, scaled={}, throughputMultiplier={}, state={}",
            describeTile(tile),
            baseline,
            scaled,
            throughputMultiplier,
            describeState(tile)
        );
    }

    public static void logEvaporationHeat(TileEntityMekanism tile, double base, double adjusted, double factor) {
        if (!shouldLogThrottled(tile, "evaporationHeat")) {
            return;
        }
        OverMekLog.debug(
            "OverMek debug evaporation heat dissipation: tile={}, base={}, adjusted={}, factor={}, state={}",
            describeTile(tile),
            base,
            adjusted,
            factor,
            describeState(tile)
        );
    }

    public static void logSpsExtra(TileEntityMekanism tile, double lastProcessed, long requestedExtra, long extraProcessed, long inputStored, long outputRoom) {
        if (!shouldLogThrottled(tile, "spsExtra")) {
            return;
        }
        OverMekLog.debug(
            "OverMek debug SPS extra processing: tile={}, lastProcessed={}, requestedExtra={}, extraProcessed={}, inputStored={}, outputRoom={}, state={}",
            describeTile(tile),
            lastProcessed,
            requestedExtra,
            extraProcessed,
            inputStored,
            outputRoom,
            describeState(tile)
        );
    }

    public static void logSpsEnergy(TileEntityMekanism tile, Object original, Object adjusted, double energyUsageMultiplier) {
        if (!shouldLogThrottled(tile, "spsEnergy")) {
            return;
        }
        OverMekLog.debug(
            "OverMek debug SPS energy adjustment: tile={}, original={}, adjusted={}, energyUsageMultiplier={}, state={}",
            describeTile(tile),
            original,
            adjusted,
            energyUsageMultiplier,
            describeState(tile)
        );
    }

    public static List<String> diagnose(@Nullable BlockEntity blockEntity) {
        List<String> lines = new ArrayList<>();
        if (blockEntity == null) {
            lines.add("OverMek debug: no block entity at this position.");
            return lines;
        }
        BlockEntity host = BoardHostResolver.resolveHost(blockEntity);
        ICircuitBoardHolder clickedHolder = getDirectHolder(blockEntity);
        ICircuitBoardHolder hostHolder = getDirectHolder(host);
        ItemStack stack = hostHolder == null ? ItemStack.EMPTY : hostHolder.getCircuitBoard();
        MachineSupportProfile supportProfile = CircuitBoardProfileHelper.getSupportProfile(host);
        BoardEffectProfile profile = BoardProfileLoader.getInstalledProfile(supportProfile, stack);

        lines.add("OverMek debug tile:");
        lines.add(" clicked=" + describeTile(blockEntity));
        lines.add(" host=" + describeTile(host));
        lines.add(" clickedHolder=" + describeHolder(clickedHolder));
        lines.add(" hostHolder=" + describeHolder(hostHolder));
        lines.add(" support=" + describeSupport(supportProfile) + ", allowedByConfig=" + CircuitBoardProfileHelper.isAllowedByConfig(host));
        lines.add(" installedProfileSupported=" + profile.isSupported() + ", reason=" + getProfileFailureReason(host, stack));
        lines.add(" warmup=" + describeWarmup(host, hostHolder, profile));
        lines.add(" multipliers=" + describeMultipliers(host, stack, profile));
        return lines;
    }

    public static String describeState(@Nullable TileEntityMekanism tile) {
        if (tile == null) {
            return "tile=null";
        }
        BlockEntity host = BoardHostResolver.resolveHost(tile);
        ICircuitBoardHolder holder = getDirectHolder(host);
        ItemStack stack = holder == null ? ItemStack.EMPTY : holder.getCircuitBoard();
        MachineSupportProfile supportProfile = CircuitBoardProfileHelper.getSupportProfile(host);
        BoardEffectProfile profile = BoardProfileLoader.getInstalledProfile(supportProfile, stack);
        return "host=" + describeTile(host)
            + ", holder=" + describeHolder(holder)
            + ", support=" + describeSupport(supportProfile)
            + ", profileSupported=" + profile.isSupported()
            + ", warmup=" + describeWarmup(host, holder, profile)
            + ", reason=" + getProfileFailureReason(host, stack);
    }

    public static String describeTile(@Nullable BlockEntity blockEntity) {
        if (blockEntity == null) {
            return "null";
        }
        Level level = blockEntity.getLevel();
        BlockPos pos = blockEntity.getBlockPos();
        String dimension = level == null ? "no-level" : level.dimension().location().toString();
        return blockEntity.getClass().getSimpleName() + "@" + dimension + "[" + pos.getX() + "," + pos.getY() + "," + pos.getZ() + "]";
    }

    private static String describeHolder(@Nullable ICircuitBoardHolder holder) {
        if (holder == null) {
            return "null";
        }
        ItemStack stack = holder.getCircuitBoard();
        return "present(hasBoard=" + holder.hasCircuitBoard()
            + ", board=" + describeBoard(stack)
            + ", warmupProgress=" + holder.getWarmupProgress()
            + ")";
    }

    private static String describeBoard(ItemStack stack) {
        if (stack == null || stack.isEmpty()) {
            return "empty";
        }
        String itemId = String.valueOf(ForgeRegistries.ITEMS.getKey(stack.getItem()));
        if (stack.getItem() instanceof CircuitBoardItem board) {
            return itemId + "(channel=" + board.getChannel() + ", tier=" + board.getTier() + ", overclocks=" + board.getOverclockCount() + ")";
        }
        return itemId + "(not CircuitBoardItem)";
    }

    private static String describeSupport(MachineSupportProfile supportProfile) {
        if (supportProfile == null) {
            return "null";
        }
        return supportProfile.machineProfile()
            + "(supported=" + supportProfile.isSupported()
            + ", acceptedChannel=" + supportProfile.acceptedChannel()
            + ", tooltip=" + supportProfile.tooltipCategory()
            + ", multiblock=" + supportProfile.multiblock()
            + ")";
    }

    private static String describeWarmup(@Nullable BlockEntity host, @Nullable ICircuitBoardHolder holder, BoardEffectProfile profile) {
        if (!(host instanceof TileEntityMekanism tile)) {
            return "not-mekanism";
        }
        int progress = holder == null ? 0 : holder.getWarmupProgress();
        int ticks = profile.isSupported() ? profile.warmupTicks() : 0;
        return progress + "/" + ticks + "(ratio=" + CircuitBoardOverclockHelper.getWarmupRatio(tile) + ")";
    }

    private static String describeMultipliers(@Nullable BlockEntity host, ItemStack stack, BoardEffectProfile profile) {
        if (!(host instanceof TileEntityMekanism tile) || !profile.isSupported()) {
            return "none";
        }
        return switch (CircuitBoardProfileHelper.getSupportProfile(host).machineProfile()) {
            case EVAPORATION_MULTIBLOCK -> "evaporation throughput="
                + CircuitBoardMultiblockHelper.getEffectiveEvaporationThroughputMultiplier(tile)
                + ", heatDissipationFactor=" + CircuitBoardMultiblockHelper.getEffectiveHeatDissipationFactor(tile)
                + ", buffer=" + CircuitBoardMultiblockHelper.getEffectiveBufferMultiplier(tile)
                + ", displayedThroughput=" + CircuitBoardMultiblockHelper.getDisplayedEvaporationThroughputMultiplier(tile, stack);
            case SPS_MULTIBLOCK -> "sps throughput="
                + CircuitBoardMultiblockHelper.getEffectiveSpsThroughputMultiplier(tile)
                + ", energy=" + CircuitBoardMultiblockHelper.getEffectiveSpsEnergyUsageMultiplier(tile)
                + ", pressure=" + CircuitBoardMultiblockHelper.getEffectiveSpsPressureMultiplier(tile)
                + ", stability=" + CircuitBoardMultiblockHelper.getEffectiveSpsStabilityMultiplier(tile)
                + ", buffer=" + CircuitBoardMultiblockHelper.getEffectiveSpsBufferMultiplier(tile)
                + ", displayedThroughput=" + CircuitBoardMultiblockHelper.getDisplayedSpsThroughputMultiplier(tile, stack);
            case FISSION -> "fission efficiency="
                + CircuitBoardMultiblockHelper.getEffectiveFissionEfficiencyMultiplier(tile)
                + ", stability=" + CircuitBoardMultiblockHelper.getEffectiveFissionStabilityMultiplier(tile)
                + ", buffer=" + CircuitBoardMultiblockHelper.getEffectiveBufferMultiplier(tile);
            case POWER_MULTIBLOCK -> "power generation="
                + CircuitBoardMultiblockHelper.getEffectiveGeneratorGenerationMultiplier(tile)
                + ", fuel=" + CircuitBoardMultiblockHelper.getEffectiveGeneratorFuelMultiplier(tile)
                + ", buffer=" + CircuitBoardMultiblockHelper.getEffectiveBufferMultiplier(tile)
                + ", matrixCapacity=" + CircuitBoardMultiblockHelper.getEffectiveMatrixCapacityMultiplier(tile)
                + ", matrixTransfer=" + CircuitBoardMultiblockHelper.getEffectiveMatrixTransferMultiplier(tile);
            default -> "profile=" + CircuitBoardProfileHelper.getSupportProfile(host).machineProfile();
        };
    }

    private static String getProfileFailureReason(@Nullable BlockEntity blockEntity, ItemStack stack) {
        if (!(blockEntity instanceof TileEntityMekanism)) {
            return "not a Mekanism tile";
        }
        MachineSupportProfile supportProfile = CircuitBoardProfileHelper.getSupportProfile(blockEntity);
        if (!supportProfile.isSupported()) {
            return "unsupported machine";
        }
        if (!CircuitBoardProfileHelper.isAllowedByConfig(blockEntity)) {
            return "blocked by OverMek machine filter config";
        }
        if (stack == null || stack.isEmpty()) {
            return "no board installed on resolved host";
        }
        if (!(stack.getItem() instanceof CircuitBoardItem board)) {
            return "installed stack is not CircuitBoardItem";
        }
        if (board.getChannel() != supportProfile.acceptedChannel()) {
            return "board channel " + board.getChannel() + " does not match accepted channel " + supportProfile.acceptedChannel();
        }
        BoardEffectProfile profile = BoardProfileLoader.getInstalledProfile(supportProfile, stack);
        return profile.isSupported() ? "ok" : "BoardProfileLoader returned unsupported";
    }

    private static boolean hasCapability(@Nullable BlockEntity blockEntity) {
        return getDirectHolder(blockEntity) != null;
    }

    @Nullable
    private static ICircuitBoardHolder getDirectHolder(@Nullable BlockEntity blockEntity) {
        if (blockEntity == null) {
            return null;
        }
        return blockEntity.getCapability(CircuitBoardHolder.CIRCUIT_BOARD_CAPABILITY).resolve().orElse(null);
    }

    private static double clampRatio(int progress, int ticks) {
        return ticks <= 0 ? 1.0D : Math.min(1.0D, Math.max(0.0D, progress / (double) ticks));
    }
}
