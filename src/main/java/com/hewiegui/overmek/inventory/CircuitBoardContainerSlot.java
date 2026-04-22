package com.hewiegui.overmek.inventory;

import java.util.function.Consumer;
import mekanism.common.inventory.container.slot.ContainerSlotType;
import mekanism.common.inventory.container.slot.InventoryContainerSlot;
import mekanism.common.inventory.container.slot.SlotOverlay;
import mekanism.common.inventory.slot.BasicInventorySlot;
import mekanism.common.inventory.warning.ISupportsWarning;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

public class CircuitBoardContainerSlot extends InventoryContainerSlot {

    public CircuitBoardContainerSlot(BasicInventorySlot slot, int x, int y, ContainerSlotType slotType, @Nullable SlotOverlay slotOverlay,
          @Nullable Consumer<ISupportsWarning<?>> warningAdder, Consumer<ItemStack> uncheckedSetter) {
        super(slot, x, y, slotType, slotOverlay, warningAdder, uncheckedSetter);
    }
}
