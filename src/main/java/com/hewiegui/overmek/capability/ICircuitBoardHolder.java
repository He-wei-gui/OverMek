package com.hewiegui.overmek.capability;

import com.hewiegui.overmek.item.CircuitBoardItem;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.common.capabilities.AutoRegisterCapability;

@AutoRegisterCapability
public interface ICircuitBoardHolder {

    ItemStack getCircuitBoard();

    void setCircuitBoard(ItemStack stack);

    int getWarmupProgress();

    void setWarmupProgress(int progress);

    double getGeneratorFuelRemainder();

    void setGeneratorFuelRemainder(double remainder);

    default boolean hasCircuitBoard() {
        ItemStack stack = getCircuitBoard();
        return !stack.isEmpty() && stack.getItem() instanceof CircuitBoardItem;
    }

    default int getOverclockCount() {
        ItemStack stack = getCircuitBoard();
        if (stack.isEmpty() || !(stack.getItem() instanceof CircuitBoardItem)) {
            return 0;
        }
        return ((CircuitBoardItem) stack.getItem()).getOverclockCount();
    }

    default int getTier() {
        ItemStack stack = getCircuitBoard();
        if (stack.isEmpty() || !(stack.getItem() instanceof CircuitBoardItem)) {
            return -1;
        }
        return ((CircuitBoardItem) stack.getItem()).getTier();
    }

    default double getWarmupRatio(int maxWarmupProgress) {
        if (!hasCircuitBoard() || maxWarmupProgress <= 0) {
            return 1.0D;
        }
        return Math.min(1.0D, Math.max(0.0D, getWarmupProgress() / (double) maxWarmupProgress));
    }
}
