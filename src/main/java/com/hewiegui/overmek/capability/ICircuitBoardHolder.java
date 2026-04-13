package com.hewiegui.overmek.capability;

import com.hewiegui.overmek.item.CircuitBoardItem;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;

public interface ICircuitBoardHolder {

    // 获取当前安装的电路板（没有则返回 ItemStack.EMPTY）
    ItemStack getCircuitBoard();

    // 安装电路板
    void setCircuitBoard(ItemStack stack);

    // 获取关联的 BlockEntity 的 PersistentData
    CompoundTag getPersistentData();

    // 是否已安装电路板（动态从 PersistentData 读取）
    default boolean hasCircuitBoard() {
        if (getPersistentData() == null) return false;
        if (!getPersistentData().contains("OverMekCircuitBoard")) return false;
        ItemStack stack = ItemStack.of(getPersistentData().getCompound("OverMekCircuitBoard"));
        return !stack.isEmpty() && stack.getItem() instanceof CircuitBoardItem;
    }

    // 获取超频次数，没有电路板返回 0（动态从 PersistentData 读取）
    default int getOverclockCount() {
        if (getPersistentData() == null) return 0;
        if (!getPersistentData().contains("OverMekCircuitBoard")) return 0;
        ItemStack stack = ItemStack.of(getPersistentData().getCompound("OverMekCircuitBoard"));
        if (stack.isEmpty() || !(stack.getItem() instanceof CircuitBoardItem)) return 0;
        return ((CircuitBoardItem) stack.getItem()).getOverclockCount();
    }

    // 获取电路板等级，没有返回 -1（动态从 PersistentData 读取）
    default int getTier() {
        if (getPersistentData() == null) return -1;
        if (!getPersistentData().contains("OverMekCircuitBoard")) return -1;
        ItemStack stack = ItemStack.of(getPersistentData().getCompound("OverMekCircuitBoard"));
        if (stack.isEmpty() || !(stack.getItem() instanceof CircuitBoardItem)) return -1;
        return ((CircuitBoardItem) stack.getItem()).getTier();
    }
}
