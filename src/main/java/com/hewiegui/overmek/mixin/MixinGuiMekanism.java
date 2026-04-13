package com.hewiegui.overmek.mixin;

import mekanism.client.gui.GuiMekanism;
import net.minecraft.client.gui.GuiGraphics;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiMekanism.class, remap = false)
public abstract class MixinGuiMekanism {

    @Inject(method = "addGuiElements", at = @At("TAIL"))
    private void addCircuitBoardSlotRender(CallbackInfo ci) {
        // 暂时留空，先验证容器侧槽位
        System.out.println("[OverMek] GuiMekanism addGuiElements fired!");
    }
}
