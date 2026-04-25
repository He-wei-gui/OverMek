package com.hewiegui.overmek.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.util.MekanismUtils;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.fml.ModList;

public final class MoreMachineCompatHelper {

    private static final String MOD_ID = "mekmm";
    private static final String MEKMM_MACHINE_PREFIX = "com.jerry.mekmm.common.tile.machine.";
    private static final String MEKLM_MACHINE_PREFIX = "com.jerry.meklm.common.tile.machine.";
    private static final String MORE_MACHINE_FACTORY_CLASS = "com.jerry.mekmm.common.tile.factory.TileEntityMoreMachineFactory";
    private static final String ADVANCED_FACTORY_CLASS = "com.jerry.mekaf.common.tile.base.TileEntityAdvancedFactoryBase";
    private static final String AMBIENT_GAS_COLLECTOR_CLASS = "com.jerry.mekmm.common.tile.machine.TileEntityAmbientGasCollector";

    private MoreMachineCompatHelper() {
    }

    public static boolean isAvailable() {
        return ModList.get().isLoaded(MOD_ID);
    }

    public static boolean isSupportedProcessingMachine(BlockEntity blockEntity) {
        if (!isAvailable()) {
            return false;
        }
        String className = blockEntity.getClass().getName();
        return className.startsWith(MEKMM_MACHINE_PREFIX)
            || className.startsWith(MEKLM_MACHINE_PREFIX)
            || hasSuperclassNamed(blockEntity.getClass(), MORE_MACHINE_FACTORY_CLASS)
            || hasSuperclassNamed(blockEntity.getClass(), ADVANCED_FACTORY_CLASS);
    }

    public static boolean isCustomFactory(BlockEntity blockEntity) {
        return isAvailable() && (hasSuperclassNamed(blockEntity.getClass(), MORE_MACHINE_FACTORY_CLASS) || hasSuperclassNamed(blockEntity.getClass(), ADVANCED_FACTORY_CLASS));
    }

    public static boolean isAmbientGasCollector(BlockEntity blockEntity) {
        return isAvailable() && blockEntity.getClass().getName().equals(AMBIENT_GAS_COLLECTOR_CLASS);
    }

    public static int getCurrentTicksRequired(TileEntityMekanism tile) {
        if (!isSupportedProcessingMachine(tile)) {
            return -1;
        }
        try {
            Method method = tile.getClass().getMethod("getTicksRequired");
            Object value = method.invoke(tile);
            return value instanceof Integer integer ? integer : -1;
        } catch (ReflectiveOperationException ignored) {
            return readIntField(tile.getClass(), tile, "ticksRequired");
        }
    }

    public static int getBaseTicksRequired(TileEntityMekanism tile) {
        if (!isSupportedProcessingMachine(tile)) {
            return -1;
        }
        int baseTicks = readStaticIntField(tile.getClass(), "BASE_TICKS_REQUIRED");
        return baseTicks <= 0 ? -1 : MekanismUtils.getTicks(tile, baseTicks);
    }

    public static boolean isUltimateLikeFactory(BlockEntity blockEntity) {
        if (!isCustomFactory(blockEntity)) {
            return false;
        }
        try {
            Field tierField = findField(blockEntity.getClass(), "tier");
            if (tierField == null) {
                return false;
            }
            tierField.setAccessible(true);
            Object tier = tierField.get(blockEntity);
            if (!(tier instanceof Enum<?> enumValue)) {
                return false;
            }
            return enumValue.name().equals("ULTIMATE") || enumValue.ordinal() >= 3;
        } catch (ReflectiveOperationException ignored) {
            return false;
        }
    }

    private static boolean hasSuperclassNamed(Class<?> type, String fqcn) {
        Class<?> current = type;
        while (current != null) {
            if (current.getName().equals(fqcn)) {
                return true;
            }
            current = current.getSuperclass();
        }
        return false;
    }

    private static int readIntField(Class<?> type, Object instance, String fieldName) {
        try {
            Field field = findField(type, fieldName);
            if (field == null) {
                return -1;
            }
            field.setAccessible(true);
            return field.getInt(instance);
        } catch (ReflectiveOperationException ignored) {
            return -1;
        }
    }

    private static int readStaticIntField(Class<?> type, String fieldName) {
        try {
            Field field = findField(type, fieldName);
            if (field == null) {
                return -1;
            }
            field.setAccessible(true);
            return field.getInt(null);
        } catch (ReflectiveOperationException ignored) {
            return -1;
        }
    }

    private static Field findField(Class<?> type, String fieldName) {
        Class<?> current = type;
        while (current != null) {
            try {
                return current.getDeclaredField(fieldName);
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        return null;
    }
}
