package com.hewiegui.overmek.mixin;

import com.hewiegui.overmek.capability.CircuitBoardHolder;
import com.hewiegui.overmek.item.CircuitBoardItem;
import com.hewiegui.overmek.capability.CircuitBoardSlotInventory;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MekanismTileContainer.class)
public abstract class MixinMekanismTileContainer {

    @Shadow
    protected abstract Slot addSlot(Slot slot);

    @Shadow(remap = false)
    public abstract BlockEntity getTileEntity();

    @Inject(method = "addSlots", at = @At("TAIL"), remap = false)
    private void onAddSlots(CallbackInfo ci) {
        System.out.println("【OverMek Debug】1. 成功注入 addSlots 方法！");

        BlockEntity be = getTileEntity();
        if (be == null) {
            System.out.println("【OverMek Debug】2. 失败：TileEntity 为空");
            return;
        }

        be.getCapability(CircuitBoardHolder.CIRCUIT_BOARD_CAPABILITY).ifPresent(holder -> {
            System.out.println("【OverMek Debug】3. 成功获取 Capability，正在添加 Slot！");

            this.addSlot(new Slot(new CircuitBoardSlotInventory(holder), 0, 8, 20) {
                @Override
                public boolean mayPlace(ItemStack stack) {
                    return stack.getItem() instanceof CircuitBoardItem;
                }
                @Override
                public int getMaxStackSize() { return 1; }
            });

            System.out.println("【OverMek Debug】4. Slot 添加指令执行完毕！");
        });
    }
}
