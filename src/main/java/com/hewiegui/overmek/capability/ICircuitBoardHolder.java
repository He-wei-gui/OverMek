package com.hewiegui.overmek.capability;

import com.hewiegui.overmek.item.CircuitBoardItem;
import net.minecraft.world.item.ItemStack;

public interface ICircuitBoardHolder {

    // 获取当前安装的电路板（没有则返回 ItemStack.EMPTY）
    ItemStack getCircuitBoard();

    // 安装电路板
    void setCircuitBoard(ItemStack stack);

    // 是否已安装电路板
    default boolean hasCircuitBoard() {
        return !getCircuitBoard().isEmpty()
            && getCircuitBoard().getItem() instanceof CircuitBoardItem;
    }

    // 获取超频次数，没有电路板返回 0
    default int getOverclockCount() {
        if (!hasCircuitBoard()) return 0;
        return ((CircuitBoardItem) getCircuitBoard().getItem()).getOverclockCount();
    }

    // 获取电路板等级，没有返回 -1
    default int getTier() {
        if (!hasCircuitBoard()) return -1;
        return ((CircuitBoardItem) getCircuitBoard().getItem()).getTier();
    }
}
