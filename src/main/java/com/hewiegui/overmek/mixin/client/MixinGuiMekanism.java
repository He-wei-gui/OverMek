package com.hewiegui.overmek.mixin.client;

import com.hewiegui.overmek.inventory.CircuitBoardContainerSlot;
import com.hewiegui.overmek.util.CircuitBoardOverclockHelper;
import com.hewiegui.overmek.util.CircuitBoardSlotLayoutHelper;
import mekanism.client.gui.GuiMekanism;
import mekanism.client.gui.GuiMekanismTile;
import mekanism.client.gui.VirtualSlotContainerScreen;
import mekanism.common.tile.base.TileEntityMekanism;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiMekanism.class, remap = false)
public abstract class MixinGuiMekanism<CONTAINER extends AbstractContainerMenu> extends VirtualSlotContainerScreen<CONTAINER> {

    protected MixinGuiMekanism(CONTAINER container, Inventory inv, Component title) {
        super(container, inv, title);
    }

    @Inject(method = "renderLabels", at = @At("TAIL"))
    private void overmek$renderCircuitBoardWarmupBar(GuiGraphics guiGraphics, int mouseX, int mouseY, CallbackInfo ci) {
        if (!((Object) this instanceof GuiMekanismTile<?, ?> gui)) {
            return;
        }
        TileEntityMekanism tile = gui.getTileEntity();
        for (Slot slot : menu.slots) {
            if (!(slot instanceof CircuitBoardContainerSlot)) {
                continue;
            }
            ItemStack stack = slot.getItem();
            double warmupRatio = stack.isEmpty() || !CircuitBoardOverclockHelper.canApplyCircuitBoardEffects(tile)
                ? 0.0D
                : CircuitBoardOverclockHelper.getDisplayedWarmupRatio(tile, stack);
            int barX = CircuitBoardSlotLayoutHelper.usesLeftExternalLayout(tile) ? slot.x - 4 : slot.x + 18;
            int barY = slot.y;
            int barHeight = 16;
            int filledHeight = Mth.clamp((int) Math.round(warmupRatio * barHeight), 0, barHeight);

            guiGraphics.fill(barX, barY, barX + 4, barY + barHeight, 0xFF101820);
            guiGraphics.fill(barX + 1, barY + 1, barX + 3, barY + barHeight - 1, 0xFF26313B);
            if (filledHeight > 0) {
                int color = warmupRatio >= 1.0D ? 0xFF4DFFD2 : 0xFFFFB347;
                guiGraphics.fill(barX + 1, barY + barHeight - filledHeight, barX + 3, barY + barHeight, color);
            }
        }
    }
}
