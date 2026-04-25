package com.hewiegui.overmek.util;

import mekanism.common.tile.factory.TileEntityFactory;
import mekanism.common.tier.FactoryTier;
import mekanism.generators.common.tile.fission.TileEntityFissionReactorCasing;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class BoardSlotAnchorRegistry {

    private static final String CHEMICAL_WASHER_CLASS_NAME = "mekanism.common.tile.machine.TileEntityChemicalWasher";

    private BoardSlotAnchorRegistry() {
    }

    public static BoardSlotAnchor resolve(BlockEntity blockEntity) {
        if (blockEntity instanceof TileEntityFissionReactorCasing) {
            return BoardSlotAnchor.REACTOR_LEFT;
        }
        if (usesDefaultLeftAnchor(blockEntity)) {
            return BoardSlotAnchor.DEFAULT_LEFT;
        }
        if (blockEntity instanceof TileEntityFactory<?> factory && factory.tier == FactoryTier.ULTIMATE) {
            return BoardSlotAnchor.ULTIMATE_FACTORY_RIGHT;
        }
        return BoardSlotAnchor.DEFAULT_RIGHT;
    }

    private static boolean usesDefaultLeftAnchor(BlockEntity blockEntity) {
        return blockEntity.getClass().getName().equals(CHEMICAL_WASHER_CLASS_NAME);
    }
}
