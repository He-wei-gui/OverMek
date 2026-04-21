package com.hewiegui.overmek.mixin;

import com.hewiegui.overmek.capability.CircuitBoardHolder;
import mekanism.common.tile.base.TileEntityMekanism;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

@Mixin(value = TileEntityMekanism.class, remap = false)
public abstract class MixinTileEntityMekanism {

    private static final int MAX_OVERCLOCK_BONUS = 10;

    private static Field operatingTicksField;
    private static Field factoryProgressField;

    static {
        try {
            Class<?> progressMachineClass = Class.forName("mekanism.common.tile.prefab.TileEntityProgressMachine");
            Field field = progressMachineClass.getDeclaredField("operatingTicks");
            field.setAccessible(true);
            operatingTicksField = field;
        } catch (Exception e) {
            operatingTicksField = null;
        }

        try {
            Class<?> factoryClass = Class.forName("mekanism.common.tile.factory.TileEntityFactory");
            Field field = factoryClass.getDeclaredField("progress");
            field.setAccessible(true);
            factoryProgressField = field;
        } catch (Exception e) {
            factoryProgressField = null;
        }
    }

    private int getOverclockBonus(TileEntityMekanism self) {
        var holder = self.getCapability(CircuitBoardHolder.CIRCUIT_BOARD_CAPABILITY).resolve().orElse(null);
        if (holder == null || !holder.hasCircuitBoard()) {
            return 0;
        }

        int overclockCount = holder.getOverclockCount();
        int tier = holder.getTier();

        float tierMultiplier = switch (tier) {
            case 0 -> 1.0f;
            case 1 -> 1.5f;
            case 2 -> 2.0f;
            case 3 -> 3.0f;
            default -> 1.0f;
        };

        int rawBonus = (int) (overclockCount * tierMultiplier);
        return Math.min(rawBonus, MAX_OVERCLOCK_BONUS);
    }

    @Inject(method = "onUpdateServer", at = @At("HEAD"), remap = false)
    private void applyOverclock(CallbackInfo ci) {
        TileEntityMekanism self = (TileEntityMekanism) (Object) this;
        if (operatingTicksField == null && factoryProgressField == null) {
            return;
        }

        int bonus = getOverclockBonus(self);
        if (bonus == 0) {
            return;
        }

        if (operatingTicksField != null) {
            try {
                int currentTicks = operatingTicksField.getInt(self);
                operatingTicksField.setInt(self, Mth.clamp(currentTicks + bonus, 0, Integer.MAX_VALUE));
                return;
            } catch (IllegalArgumentException ignored) {
            } catch (IllegalAccessException ignored) {
            }
        }

        if (factoryProgressField != null) {
            try {
                int[] progress = (int[]) factoryProgressField.get(self);
                if (progress != null) {
                    for (int i = 0; i < progress.length; i++) {
                        progress[i] = Mth.clamp(progress[i] + bonus, 0, Integer.MAX_VALUE);
                    }
                }
            } catch (IllegalArgumentException ignored) {
            } catch (IllegalAccessException ignored) {
            }
        }
    }
}
