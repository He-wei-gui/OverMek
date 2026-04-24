package com.hewiegui.overmek.mixin;

import com.hewiegui.overmek.capability.CircuitBoardHolder;
import com.hewiegui.overmek.inventory.CircuitBoardInventorySlot;
import com.hewiegui.overmek.util.CircuitBoardOverclockHelper;
import com.hewiegui.overmek.util.CircuitBoardSlotLayoutHelper;
import com.mojang.logging.LogUtils;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MekanismTileContainer.class, remap = false)
public abstract class MixinMekanismTileContainer extends AbstractContainerMenu {

    private static final Logger overmek$logger = LogUtils.getLogger();

    protected MixinMekanismTileContainer() {
        super(null, 0);
    }

    @Inject(method = "addSlots", at = @At("TAIL"))
    private void addCircuitBoardSlot(CallbackInfo ci) {
        MekanismTileContainer<?> self = (MekanismTileContainer<?>) (Object) this;
        BlockEntity be = self.getTileEntity();
        if (be == null) {
            overmek$logger.debug("OverMek skipped circuit board slot because tile entity is null for container {}", self.getClass().getName());
            return;
        }
        var holder = be.getCapability(CircuitBoardHolder.CIRCUIT_BOARD_CAPABILITY).resolve().orElse(null);
        if (!CircuitBoardOverclockHelper.shouldExposeCircuitBoardSlot(be, holder)) {
            overmek$logger.debug(
                "OverMek skipped circuit board slot for {}. supported={}, hasBoard={}",
                be.getClass().getName(),
                CircuitBoardOverclockHelper.canApplyCircuitBoardEffects(be),
                holder != null && holder.hasCircuitBoard()
            );
            return;
        }

        if (holder == null) {
            overmek$logger.debug(
                "OverMek could not resolve circuit board capability for {}. registered={}",
                be.getClass().getName(),
                CircuitBoardHolder.CIRCUIT_BOARD_CAPABILITY.isRegistered()
            );
            return;
        }

        int slotX = CircuitBoardSlotLayoutHelper.getSlotX(be);
        int slotY = CircuitBoardSlotLayoutHelper.getSlotY(be);
        CircuitBoardInventorySlot inventorySlot = new CircuitBoardInventorySlot(holder, be, slotX, slotY);
        var containerSlot = inventorySlot.createContainerSlot();
        if (containerSlot == null) {
            overmek$logger.debug("OverMek failed to create a container slot for {}", be.getClass().getName());
            return;
        }
        addSlot(containerSlot);
        overmek$logger.debug(
            "OverMek added circuit board slot to container {} for {}. slotIndex={}, totalSlots={}, pos=({}, {}), layout={}",
            self.getClass().getName(),
            be.getClass().getName(),
            containerSlot.index,
            slots.size(),
            slotX,
            slotY,
            CircuitBoardSlotLayoutHelper.getLayoutName(be)
        );
    }
}
