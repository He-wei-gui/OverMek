package com.hewiegui.overmek.util;

import com.hewiegui.overmek.capability.ICircuitBoardHolder;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public final class CircuitBoardTileAccessHelper {

    private CircuitBoardTileAccessHelper() {
    }

    public static BlockEntity resolveBoardHost(BlockEntity blockEntity) {
        return BoardHostResolver.resolveHost(blockEntity);
    }

    @Nullable
    public static ICircuitBoardHolder resolveHolder(BlockEntity blockEntity) {
        return BoardHostResolver.resolveHolder(blockEntity);
    }
}
