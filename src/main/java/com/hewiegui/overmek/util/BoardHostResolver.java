package com.hewiegui.overmek.util;

import com.hewiegui.overmek.capability.CircuitBoardHolder;
import com.hewiegui.overmek.capability.ICircuitBoardHolder;
import java.util.Set;
import mekanism.common.lib.multiblock.MultiblockData;
import mekanism.common.content.evaporation.EvaporationMultiblockData;
import mekanism.common.content.matrix.MatrixMultiblockData;
import mekanism.common.content.sps.SPSMultiblockData;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.multiblock.TileEntityInductionCasing;
import mekanism.common.tile.multiblock.TileEntityInductionCell;
import mekanism.common.tile.multiblock.TileEntityInductionProvider;
import mekanism.common.tile.multiblock.TileEntitySPSCasing;
import mekanism.common.tile.multiblock.TileEntitySuperchargedCoil;
import mekanism.common.tile.multiblock.TileEntityThermalEvaporationBlock;
import mekanism.common.tile.prefab.TileEntityInternalMultiblock;
import mekanism.common.tile.prefab.TileEntityMultiblock;
import mekanism.common.tile.prefab.TileEntityStructuralMultiblock;
import mekanism.generators.common.content.fission.FissionReactorMultiblockData;
import mekanism.generators.common.content.fusion.FusionReactorMultiblockData;
import mekanism.generators.common.content.turbine.TurbineMultiblockData;
import mekanism.generators.common.tile.fission.TileEntityFissionAssembly;
import mekanism.generators.common.tile.fission.TileEntityFissionReactorCasing;
import mekanism.generators.common.tile.fusion.TileEntityFusionReactorBlock;
import mekanism.generators.common.tile.turbine.TileEntityElectromagneticCoil;
import mekanism.generators.common.tile.turbine.TileEntityRotationalComplex;
import mekanism.generators.common.tile.turbine.TileEntitySaturatingCondenser;
import mekanism.generators.common.tile.turbine.TileEntityTurbineCasing;
import mekanism.generators.common.tile.turbine.TileEntityTurbineRotor;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public final class BoardHostResolver {

    private BoardHostResolver() {
    }

    public static BlockEntity resolveHost(BlockEntity blockEntity) {
        if (!CircuitBoardMultiblockHelper.isSupportedMultiblockController(blockEntity)) {
            return blockEntity;
        }
        Level level = blockEntity.getLevel();
        if (level == null) {
            OverMekDebug.logHostResolution(blockEntity, blockEntity, 0, "no-level");
            return blockEntity;
        }
        MultiblockData multiblockData = getMultiblockData(blockEntity);
        if (multiblockData == null) {
            OverMekDebug.logHostResolution(blockEntity, blockEntity, 0, "no-multiblock-data");
            return blockEntity;
        }
        Set<BlockPos> locations = multiblockData.locations;
        if (locations == null || locations.isEmpty()) {
            OverMekDebug.logHostResolution(blockEntity, blockEntity, 0, "empty-locations");
            return blockEntity;
        }
        BlockEntity host = null;
        long bestPos = Long.MAX_VALUE;
        for (BlockPos pos : locations) {
            BlockEntity candidate = level.getBlockEntity(pos);
            if (!isSameBoardHostFamily(blockEntity, candidate) && !isDataFamilyHost(multiblockData, candidate)) {
                continue;
            }
            long candidatePos = pos.asLong();
            if (host == null || Long.compareUnsigned(candidatePos, bestPos) < 0) {
                host = candidate;
                bestPos = candidatePos;
            }
        }
        BlockEntity resolvedHost = host == null ? blockEntity : host;
        OverMekDebug.logHostResolution(blockEntity, resolvedHost, locations.size(), host == null ? "fallback-source" : "selected-location");
        return resolvedHost;
    }

    @Nullable
    private static MultiblockData getMultiblockData(BlockEntity blockEntity) {
        if (blockEntity instanceof TileEntityMultiblock<?> multiblockTile) {
            return multiblockTile.getMultiblock();
        }
        if (blockEntity instanceof TileEntityInternalMultiblock internalMultiblock) {
            return internalMultiblock.getMultiblock();
        }
        if (blockEntity instanceof TileEntityStructuralMultiblock structuralMultiblock) {
            for (var structure : structuralMultiblock.getStructureMap().values()) {
                if (structure != null && structure.isValid() && structure.getMultiblockData() != null) {
                    return structure.getMultiblockData();
                }
            }
        }
        return null;
    }

    public static boolean isHost(BlockEntity blockEntity) {
        return resolveHost(blockEntity) == blockEntity;
    }

    @Nullable
    public static ICircuitBoardHolder resolveHolder(BlockEntity blockEntity) {
        BlockEntity host = resolveHost(blockEntity);
        migrateLegacyState(blockEntity, host);
        ICircuitBoardHolder hostHolder = host.getCapability(CircuitBoardHolder.CIRCUIT_BOARD_CAPABILITY).resolve().orElse(null);
        if (hostHolder != null && !hostHolder.hasCircuitBoard()) {
            migrateFromAnyMember(blockEntity, host, hostHolder);
        }
        OverMekDebug.logHolderResolution(blockEntity, host, hostHolder);
        return hostHolder;
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
        transferHolderContents(sourceHolder, hostHolder);
        markChanged(source);
        markChanged(host);
    }

    private static void migrateFromAnyMember(BlockEntity source, BlockEntity host, ICircuitBoardHolder hostHolder) {
        if (!CircuitBoardMultiblockHelper.isSupportedMultiblockController(source)) {
            return;
        }
        Level level = host.getLevel();
        if (level == null) {
            return;
        }
        MultiblockData multiblockData = getMultiblockData(source);
        if (multiblockData == null) {
            return;
        }
        Set<BlockPos> locations = multiblockData.locations;
        if (locations == null || locations.isEmpty()) {
            return;
        }
        for (BlockPos pos : locations) {
            BlockEntity candidate = level.getBlockEntity(pos);
            if (candidate == null || candidate == host) {
                continue;
            }
            if (!isSameBoardHostFamily(source, candidate) && !isDataFamilyHost(multiblockData, candidate)) {
                continue;
            }
            ICircuitBoardHolder memberHolder = candidate.getCapability(CircuitBoardHolder.CIRCUIT_BOARD_CAPABILITY).resolve().orElse(null);
            if (memberHolder == null || !memberHolder.hasCircuitBoard()) {
                continue;
            }
            transferHolderContents(memberHolder, hostHolder);
            markChanged(candidate);
            markChanged(host);
            return;
        }
    }

    private static void transferHolderContents(ICircuitBoardHolder fromHolder, ICircuitBoardHolder toHolder) {
        toHolder.setCircuitBoard(fromHolder.getCircuitBoard());
        toHolder.setWarmupProgress(fromHolder.getWarmupProgress());
        toHolder.setGeneratorFuelRemainder(fromHolder.getGeneratorFuelRemainder());
        fromHolder.setCircuitBoard(ItemStack.EMPTY);
        fromHolder.setWarmupProgress(0);
        fromHolder.setGeneratorFuelRemainder(0.0D);
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
        if (source instanceof TileEntityFissionAssembly) {
            return candidate instanceof TileEntityFissionReactorCasing;
        }
        if (source instanceof TileEntityFusionReactorBlock) {
            return candidate instanceof TileEntityFusionReactorBlock;
        }
        if (source instanceof TileEntityTurbineCasing) {
            return candidate instanceof TileEntityTurbineCasing;
        }
        if (source instanceof TileEntityTurbineRotor
            || source instanceof TileEntityElectromagneticCoil
            || source instanceof TileEntityRotationalComplex
            || source instanceof TileEntitySaturatingCondenser) {
            return candidate instanceof TileEntityTurbineCasing;
        }
        if (source instanceof TileEntityInductionCasing || source instanceof TileEntityInductionCell || source instanceof TileEntityInductionProvider) {
            return candidate instanceof TileEntityInductionCasing || candidate instanceof TileEntityInductionCell || candidate instanceof TileEntityInductionProvider;
        }
        if (source instanceof TileEntitySPSCasing) {
            return candidate instanceof TileEntitySPSCasing;
        }
        if (source instanceof TileEntitySuperchargedCoil) {
            return candidate instanceof TileEntitySPSCasing;
        }
        if (source instanceof TileEntityThermalEvaporationBlock) {
            return candidate instanceof TileEntityThermalEvaporationBlock;
        }
        return source.getClass().isInstance(candidate);
    }

    private static boolean isDataFamilyHost(MultiblockData multiblockData, @Nullable BlockEntity candidate) {
        if (candidate == null) {
            return false;
        }
        if (multiblockData instanceof FissionReactorMultiblockData) {
            return candidate instanceof TileEntityFissionReactorCasing;
        }
        if (multiblockData instanceof FusionReactorMultiblockData) {
            return candidate instanceof TileEntityFusionReactorBlock;
        }
        if (multiblockData instanceof TurbineMultiblockData) {
            return candidate instanceof TileEntityTurbineCasing;
        }
        if (multiblockData instanceof MatrixMultiblockData) {
            return candidate instanceof TileEntityInductionCasing
                || candidate instanceof TileEntityInductionCell
                || candidate instanceof TileEntityInductionProvider;
        }
        if (multiblockData instanceof SPSMultiblockData) {
            return candidate instanceof TileEntitySPSCasing;
        }
        if (multiblockData instanceof EvaporationMultiblockData) {
            return candidate instanceof TileEntityThermalEvaporationBlock;
        }
        return false;
    }
}
