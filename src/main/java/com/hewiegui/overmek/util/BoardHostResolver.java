package com.hewiegui.overmek.util;

import com.hewiegui.overmek.capability.CircuitBoardHolder;
import com.hewiegui.overmek.capability.ICircuitBoardHolder;
import java.util.Set;
import mekanism.common.lib.multiblock.MultiblockData;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.multiblock.TileEntityInductionCasing;
import mekanism.common.tile.multiblock.TileEntityInductionCell;
import mekanism.common.tile.multiblock.TileEntityInductionProvider;
import mekanism.common.tile.multiblock.TileEntitySPSCasing;
import mekanism.common.tile.multiblock.TileEntityThermalEvaporationBlock;
import mekanism.common.tile.prefab.TileEntityMultiblock;
import mekanism.generators.common.tile.fission.TileEntityFissionReactorCasing;
import mekanism.generators.common.tile.fusion.TileEntityFusionReactorBlock;
import mekanism.generators.common.tile.turbine.TileEntityTurbineCasing;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public final class BoardHostResolver {

    private BoardHostResolver() {
    }

    public static BlockEntity resolveHost(BlockEntity blockEntity) {
        if (!(blockEntity instanceof TileEntityMultiblock<?>) || !CircuitBoardMultiblockHelper.isSupportedMultiblockController(blockEntity)) {
            return blockEntity;
        }
        Level level = blockEntity.getLevel();
        if (level == null) {
            return blockEntity;
        }
        MultiblockData multiblockData = ((TileEntityMultiblock<?>) blockEntity).getMultiblock();
        if (multiblockData == null) {
            return blockEntity;
        }
        Set<BlockPos> locations = multiblockData.locations;
        if (locations == null || locations.isEmpty()) {
            return blockEntity;
        }
        BlockEntity host = null;
        long bestPos = Long.MAX_VALUE;
        for (BlockPos pos : locations) {
            BlockEntity candidate = level.getBlockEntity(pos);
            if (!isSameBoardHostFamily(blockEntity, candidate)) {
                continue;
            }
            long candidatePos = pos.asLong();
            if (host == null || Long.compareUnsigned(candidatePos, bestPos) < 0) {
                host = candidate;
                bestPos = candidatePos;
            }
        }
        return host == null ? blockEntity : host;
    }

    public static boolean isHost(BlockEntity blockEntity) {
        return resolveHost(blockEntity) == blockEntity;
    }

    @Nullable
    public static ICircuitBoardHolder resolveHolder(BlockEntity blockEntity) {
        BlockEntity host = resolveHost(blockEntity);
        migrateLegacyState(blockEntity, host);
        return host.getCapability(CircuitBoardHolder.CIRCUIT_BOARD_CAPABILITY).resolve().orElse(null);
    }

    @Nullable
    public static ICircuitBoardHolder resolveHostHolder(BlockEntity blockEntity) {
        BlockEntity host = resolveHost(blockEntity);
        return host.getCapability(CircuitBoardHolder.CIRCUIT_BOARD_CAPABILITY).resolve().orElse(null);
    }

    private static void migrateLegacyState(BlockEntity source, BlockEntity host) {
        if (source == host) {
            return;
        }
        ICircuitBoardHolder sourceHolder = source.getCapability(CircuitBoardHolder.CIRCUIT_BOARD_CAPABILITY).resolve().orElse(null);
        ICircuitBoardHolder hostHolder = host.getCapability(CircuitBoardHolder.CIRCUIT_BOARD_CAPABILITY).resolve().orElse(null);
        if (sourceHolder == null || hostHolder == null || !sourceHolder.hasCircuitBoard() || hostHolder.hasCircuitBoard()) {
            return;
        }
        hostHolder.setCircuitBoard(sourceHolder.getCircuitBoard());
        hostHolder.setWarmupProgress(sourceHolder.getWarmupProgress());
        hostHolder.setGeneratorFuelRemainder(sourceHolder.getGeneratorFuelRemainder());
        sourceHolder.setCircuitBoard(ItemStack.EMPTY);
        sourceHolder.setWarmupProgress(0);
        sourceHolder.setGeneratorFuelRemainder(0.0D);
        markChanged(source);
        markChanged(host);
    }

    private static void markChanged(BlockEntity blockEntity) {
        if (blockEntity instanceof TileEntityMekanism tile) {
            tile.setChanged();
        } else if (blockEntity != null) {
            blockEntity.setChanged();
        }
    }

    private static boolean isSameBoardHostFamily(BlockEntity source, @Nullable BlockEntity candidate) {
        if (candidate == null) {
            return false;
        }
        if (source instanceof TileEntityFissionReactorCasing) {
            return candidate instanceof TileEntityFissionReactorCasing;
        }
        if (source instanceof TileEntityFusionReactorBlock) {
            return candidate instanceof TileEntityFusionReactorBlock;
        }
        if (source instanceof TileEntityTurbineCasing) {
            return candidate instanceof TileEntityTurbineCasing;
        }
        if (source instanceof TileEntityInductionCasing || source instanceof TileEntityInductionCell || source instanceof TileEntityInductionProvider) {
            return candidate instanceof TileEntityInductionCasing || candidate instanceof TileEntityInductionCell || candidate instanceof TileEntityInductionProvider;
        }
        if (source instanceof TileEntitySPSCasing) {
            return candidate instanceof TileEntitySPSCasing;
        }
        if (source instanceof TileEntityThermalEvaporationBlock) {
            return candidate instanceof TileEntityThermalEvaporationBlock;
        }
        return source.getClass().isInstance(candidate);
    }
}
