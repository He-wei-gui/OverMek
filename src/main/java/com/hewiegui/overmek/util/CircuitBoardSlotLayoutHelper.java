package com.hewiegui.overmek.util;

import mekanism.common.tile.factory.TileEntityFactory;
import mekanism.common.tier.FactoryTier;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class CircuitBoardSlotLayoutHelper {

    private static final int DEFAULT_GUI_WIDTH = 176;
    private static final int ULTIMATE_FACTORY_GUI_WIDTH = 210;
    private static final int EXTERNAL_SLOT_GAP = 1;
    private static final int EXTERNAL_SLOT_SIZE = 18;
    private static final int EXTERNAL_SLOT_Y = 66;
    private static final String CHEMICAL_WASHER_CLASS_NAME = "mekanism.common.tile.machine.TileEntityChemicalWasher";

    private CircuitBoardSlotLayoutHelper() {
    }

    public static int getSlotX(BlockEntity blockEntity) {
        return usesLeftExternalLayout(blockEntity)
            ? -(EXTERNAL_SLOT_SIZE + EXTERNAL_SLOT_GAP)
            : getGuiWidth(blockEntity) + EXTERNAL_SLOT_GAP;
    }

    public static int getSlotY(BlockEntity blockEntity) {
        return EXTERNAL_SLOT_Y;
    }

    public static boolean usesLeftExternalLayout(BlockEntity blockEntity) {
        return blockEntity.getClass().getName().equals(CHEMICAL_WASHER_CLASS_NAME);
    }

    public static String getLayoutName(BlockEntity blockEntity) {
        return usesLeftExternalLayout(blockEntity) ? "external_left" : "external_right";
    }

    private static int getGuiWidth(BlockEntity blockEntity) {
        if (blockEntity instanceof TileEntityFactory<?> factory && factory.tier == FactoryTier.ULTIMATE) {
            return ULTIMATE_FACTORY_GUI_WIDTH;
        }
        return DEFAULT_GUI_WIDTH;
    }
}
