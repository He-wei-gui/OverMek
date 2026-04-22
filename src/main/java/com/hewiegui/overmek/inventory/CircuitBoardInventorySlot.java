package com.hewiegui.overmek.inventory;

import com.hewiegui.overmek.capability.ICircuitBoardHolder;
import com.hewiegui.overmek.item.CircuitBoardItem;
import com.mojang.logging.LogUtils;
import mekanism.common.inventory.container.slot.ContainerSlotType;
import mekanism.common.inventory.container.slot.InventoryContainerSlot;
import mekanism.common.inventory.container.slot.SlotOverlay;
import mekanism.common.inventory.slot.BasicInventorySlot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.slf4j.Logger;

public class CircuitBoardInventorySlot extends BasicInventorySlot {

    private static final Logger LOGGER = LogUtils.getLogger();

    private final ICircuitBoardHolder holder;
    private final BlockEntity blockEntity;
    private final int slotX;
    private final int slotY;

    public CircuitBoardInventorySlot(ICircuitBoardHolder holder, BlockEntity blockEntity, int x, int y) {
        super(1, manualOnly, manualOnly, stack -> stack.getItem() instanceof CircuitBoardItem, null, x, y);
        this.holder = holder;
        this.blockEntity = blockEntity;
        this.slotX = x;
        this.slotY = y;
        setSlotType(ContainerSlotType.EXTRA);
        setSlotOverlay(SlotOverlay.UPGRADE);
        ItemStack stack = holder.getCircuitBoard();
        if (!stack.isEmpty()) {
            setStackUnchecked(stack);
        }
        LOGGER.debug("OverMek created circuit board inventory slot for {} with initial stack {}", blockEntity.getClass().getName(), stack);
    }

    @Override
    public void onContentsChanged() {
        holder.setCircuitBoard(getStack());
        blockEntity.setChanged();
    }

    @Override
    public InventoryContainerSlot createContainerSlot() {
        InventoryContainerSlot slot = new CircuitBoardContainerSlot(this, slotX, slotY, getSlotType(), getSlotOverlay(), null, this::setStackUnchecked);
        LOGGER.debug(
            "OverMek created main gui circuit board slot {} for {} at ({}, {})",
            slot == null ? -1 : slot.index,
            blockEntity.getClass().getName(),
            slot == null ? -1 : slot.x,
            slot == null ? -1 : slot.y
        );
        return slot;
    }
}
