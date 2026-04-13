package com.hewiegui.overmek.mixin;

import com.hewiegui.overmek.capability.CircuitBoardHolder;
import com.mojang.blaze3d.vertex.PoseStack;
import mekanism.client.gui.GuiMekanism;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiMekanism.class, remap = false)
public abstract class MixinGuiMekanism {

    @Shadow
    protected abstract BlockEntity getTileEntity();

    @Inject(method = "renderBg", at = @At("TAIL"))
    private void renderCircuitBoardSlot(GuiGraphics graphics, float partialTick,
                                         int mouseX, int mouseY, CallbackInfo ci) {
        BlockEntity be = getTileEntity();
        if (be == null) return;

        be.getCapability(CircuitBoardHolder.CIRCUIT_BOARD_CAPABILITY).ifPresent(holder -> {
            // 在升级槽旁边画一个槽位边框
            // 坐标和容器侧保持一致：x=176+14, y=96
            int slotX = 176 + 14;
            int slotY = 96;
            graphics.fill(slotX - 1, slotY - 1, slotX + 17, slotY + 17, 0xFF555555);
            graphics.fill(slotX, slotY, slotX + 16, slotY + 16, 0xFF1A1A1A);
        });
    }
}