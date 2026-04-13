package com.hewiegui.overmek.capability;

import net.minecraft.world.SimpleContainer;
import net.minecraft.world.item.ItemStack;

public class CircuitBoardSlotInventory extends SimpleContainer {

    private final ICircuitBoardHolder holder;

    public CircuitBoardSlotInventory(ICircuitBoardHolder holder) {
        super(1);
        // 从 Capability 恢复已安装的电路板
        if (holder.hasCircuitBoard()) {
            setItem(0, holder.getCircuitBoard());
        }
        this.holder = holder;
    }

    @Override
    public void setChanged() {
        super.setChanged();
        holder.setCircuitBoard(getItem(0));
    }
}