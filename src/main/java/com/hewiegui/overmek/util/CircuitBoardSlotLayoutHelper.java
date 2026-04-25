package com.hewiegui.overmek.util;

import net.minecraft.world.level.block.entity.BlockEntity;

public final class CircuitBoardSlotLayoutHelper {

    private CircuitBoardSlotLayoutHelper() {
    }

    public static int getSlotX(BlockEntity blockEntity) {
        return BoardSlotAnchorRegistry.resolve(blockEntity).getSlotX();
    }

    public static int getSlotY(BlockEntity blockEntity) {
        return BoardSlotAnchorRegistry.resolve(blockEntity).getSlotY();
    }

    public static boolean usesLeftExternalLayout(BlockEntity blockEntity) {
        return BoardSlotAnchorRegistry.resolve(blockEntity).isLeftSide();
    }

    public static String getLayoutName(BlockEntity blockEntity) {
        return BoardSlotAnchorRegistry.resolve(blockEntity).getLayoutName();
    }
}
