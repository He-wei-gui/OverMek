package com.hewiegui.overmek.mixin;

import com.hewiegui.overmek.item.CircuitBoardItem;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.SimpleContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MekanismTileContainer.class, remap = false)
public abstract class MixinMekanismTileContainer extends AbstractContainerMenu {

    protected MixinMekanismTileContainer() { super(null, 0); }

    @Inject(method = "addSlots", at = @At("TAIL"))
    private void addCircuitBoardSlot(CallbackInfo ci) {
        MekanismTileContainer<?> self = (MekanismTileContainer<?>) (Object) this;
        BlockEntity be = self.getTileEntity();
        if (be == null) return;
        if (!be.getClass().getName().startsWith("mekanism.common.tile")) return;

        // 判断是否是工厂，工厂类名包含 factory
        boolean isFactory = be.getClass().getName().contains("factory");

        // 工厂和普通机器用不同坐标
        int slotX = isFactory ? 176 + 14 : 176;
        int slotY = isFactory ? 115 : 114;

        // 从机器的 PersistentData 恢复已安装的电路板
        SimpleContainer container = new SimpleContainer(1);
        CompoundTag data = be.getPersistentData();
        if (data.contains("OverMekCircuitBoard")) {
            container.setItem(0, ItemStack.of(data.getCompound("OverMekCircuitBoard")));
        }

        addSlot(new Slot(container, 0, slotX, slotY) {
            @Override
            public boolean mayPlace(ItemStack stack) {
                return stack.getItem() instanceof CircuitBoardItem;
            }

            @Override
            public int getMaxStackSize() { return 1; }

            @Override
            public void setChanged() {
                super.setChanged();
                // 槽位变化时保存到机器的 PersistentData
                ItemStack stack = container.getItem(0);
                if (stack.isEmpty()) {
                    be.getPersistentData().remove("OverMekCircuitBoard");
                } else {
                    be.getPersistentData().put("OverMekCircuitBoard", stack.serializeNBT());
                }
                be.setChanged();
                System.out.println("[OverMek] Circuit board saved: " + stack);
            }
        });
    }
}
