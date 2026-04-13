package com.hewiegui.overmek.mixin;

import com.hewiegui.overmek.capability.CircuitBoardHolder;
import com.hewiegui.overmek.capability.ICircuitBoardHolder;
import mekanism.common.tile.prefab.TileEntityProgressMachine;
import net.minecraft.core.Direction;
import net.minecraft.util.Mth;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.lang.reflect.Field;

@Mixin(value = TileEntityProgressMachine.class, remap = false)
public abstract class MixinTileEntityProgressMachine {

    // 缓存 operatingTicks 字段
    private static Field operatingTicksField;

    static {
        try {
            Field field = TileEntityProgressMachine.class.getDeclaredField("operatingTicks");
            field.setAccessible(true);
            operatingTicksField = field;
        } catch (NoSuchFieldException e) {
            System.out.println("[OverMek] Failed to find operatingTicks field: " + e.getMessage());
        }
    }

    @Inject(method = "onUpdateServer", at = @At("HEAD"), remap = false)
    private void applyOverclock(CallbackInfo ci) {
        TileEntityProgressMachine<?> self = (TileEntityProgressMachine<?>) (Object) this;

        // 尝试从所有方向获取 capability
        for (Direction side : Direction.values()) {
            self.getCapability(CircuitBoardHolder.CIRCUIT_BOARD_CAPABILITY, side)
                .resolve()
                .ifPresent(holder -> {
                    int overclockCount = holder.getOverclockCount();
                    int tier = holder.getTier();
                    System.out.println("[OverMek] Found circuit board holder, tier: " + tier + ", overclockCount: " + overclockCount);

                    // 根据 tier 计算倍率
                    float tierMultiplier = switch (tier) {
                        case 0 -> 1.0f;  // 基础：1x
                        case 1 -> 1.5f;  // 进阶：1.5x
                        case 2 -> 2.0f;  // 精英：2x
                        case 3 -> 3.0f;  // 终极：3x
                        default -> 1.0f;
                    };

                    // 计算增强后的超频加成
                    int rawBonus = (int) (overclockCount * tierMultiplier);
                    // 限制最大超频加成（最大 +10）
                    int bonus = Math.min(rawBonus, 10);

                    if (bonus > 0 && operatingTicksField != null) {
                        try {
                            int currentTicks = operatingTicksField.getInt(self);
                            System.out.println("[OverMek] Before overclock: " + currentTicks + ", bonus: " + bonus + " (tierMultiplier: " + tierMultiplier + ")");
                            operatingTicksField.setInt(self, Mth.clamp(currentTicks + bonus, 0, Integer.MAX_VALUE));
                            int newTicks = operatingTicksField.getInt(self);
                            System.out.println("[OverMek] After overclock: " + newTicks);
                        } catch (IllegalAccessException e) {
                            System.out.println("[OverMek] Failed to access operatingTicks: " + e.getMessage());
                        }
                    }
                });
        }
    }
}
