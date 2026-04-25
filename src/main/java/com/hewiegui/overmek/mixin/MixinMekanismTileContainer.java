package com.hewiegui.overmek.mixin;

import com.hewiegui.overmek.capability.CircuitBoardHolder;
import com.hewiegui.overmek.inventory.CircuitBoardInventorySlot;
import com.hewiegui.overmek.util.CircuitBoardOverclockHelper;
import com.hewiegui.overmek.util.BoardHostResolver;
import com.hewiegui.overmek.util.BoardSlotDisplayState;
import com.hewiegui.overmek.util.OverMekLog;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MekanismTileContainer.class, remap = false)
public abstract class MixinMekanismTileContainer extends AbstractContainerMenu {

    protected MixinMekanismTileContainer() {
        super(null, 0);
    }

    @Inject(method = "addSlots", at = @At("TAIL"))
    private void addCircuitBoardSlot(CallbackInfo ci) {
        MekanismTileContainer<?> self = (MekanismTileContainer<?>) (Object) this;
        BlockEntity be = self.getTileEntity();
        if (be == null) {
            OverMekLog.debug("OverMek skipped circuit board slot because tile entity is null for container {}", self.getClass().getName());
            return;
        }
        BlockEntity host = BoardHostResolver.resolveHost(be);
        var holder = BoardHostResolver.resolveHolder(be);
        if (!CircuitBoardOverclockHelper.shouldExposeCircuitBoardSlot(be, holder)) {
            OverMekLog.debug(
                "OverMek skipped circuit board slot for {}. supported={}, hasBoard={}, host={}",
                be.getClass().getName(),
                CircuitBoardOverclockHelper.canApplyCircuitBoardEffects(be),
                holder != null && holder.hasCircuitBoard(),
                host.getClass().getName()
            );
            return;
        }

        if (holder == null) {
            OverMekLog.debug(
                "OverMek could not resolve circuit board capability for {}. registered={}",
                be.getClass().getName(),
                CircuitBoardHolder.CIRCUIT_BOARD_CAPABILITY.isRegistered()
            );
            return;
        }

        BoardSlotDisplayState displayState = CircuitBoardOverclockHelper.getDisplayState((mekanism.common.tile.base.TileEntityMekanism) be, holder.getCircuitBoard());
        int slotX = displayState.supportProfile().slotAnchor().getSlotX();
        int slotY = displayState.supportProfile().slotAnchor().getSlotY();
        CircuitBoardInventorySlot inventorySlot = new CircuitBoardInventorySlot(holder, host, slotX, slotY);
        var containerSlot = inventorySlot.createContainerSlot();
        if (containerSlot == null) {
            OverMekLog.debug("OverMek failed to create a container slot for {}", be.getClass().getName());
            return;
        }
        addSlot(containerSlot);
        OverMekLog.debug(
            "OverMek added circuit board slot to container {} for {}. slotIndex={}, totalSlots={}, pos=({}, {}), layout={}, host={}",
            self.getClass().getName(),
            be.getClass().getName(),
            containerSlot.index,
            slots.size(),
            slotX,
            slotY,
            displayState.supportProfile().slotAnchor().getLayoutName(),
            host.getClass().getName()
        );
    }
}
