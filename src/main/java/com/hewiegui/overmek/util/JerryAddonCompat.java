package com.hewiegui.overmek.util;

import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;

public final class JerryAddonCompat {

    private static final String MEKMM_PREFIX = "com.jerry.mekmm.";
    private static final String MEKLG_PREFIX = "com.jerry.meklg.";
    private static final String MEKLM_PREFIX = "com.jerry.meklm.";
    private static final String MEKMM_FACTORY_PREFIX = "com.jerry.mekmm.common.tile.factory.";
    private static final String MEKLG_GENERATOR_PREFIX = "com.jerry.meklg.common.tile.";

    private JerryAddonCompat() {
    }

    public static boolean isJerryAddonTile(@Nullable BlockEntity blockEntity) {
        String className = getClassName(blockEntity);
        return className.startsWith(MEKMM_PREFIX) || className.startsWith(MEKLG_PREFIX) || className.startsWith(MEKLM_PREFIX);
    }

    public static boolean isMoreMachineFactory(@Nullable BlockEntity blockEntity) {
        return getClassName(blockEntity).startsWith(MEKMM_FACTORY_PREFIX);
    }

    public static boolean isMoreGenerator(@Nullable BlockEntity blockEntity) {
        String className = getClassName(blockEntity);
        return className.startsWith(MEKLG_GENERATOR_PREFIX) && className.endsWith("Generator");
    }

    public static boolean isMoreFuelGenerator(@Nullable BlockEntity blockEntity) {
        String className = getClassName(blockEntity);
        return className.equals("com.jerry.meklg.common.tile.TileEntityLargeGasBurningGenerator")
            || className.equals("com.jerry.meklg.common.tile.TileEntityLargeHeatGenerator");
    }

    private static String getClassName(@Nullable BlockEntity blockEntity) {
        return blockEntity == null ? "" : blockEntity.getClass().getName();
    }
}
