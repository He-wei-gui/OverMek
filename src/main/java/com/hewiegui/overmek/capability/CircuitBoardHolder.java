package com.hewiegui.overmek.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.CapabilityManager;
import net.minecraftforge.common.capabilities.CapabilityToken;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class CircuitBoardHolder implements ICircuitBoardHolder, ICapabilitySerializable<CompoundTag> {

    public static final Capability<ICircuitBoardHolder> CIRCUIT_BOARD_CAPABILITY =
        CapabilityManager.get(new CapabilityToken<>() {});

    private ItemStack circuitBoard = ItemStack.EMPTY;
    private int warmupProgress;
    private final LazyOptional<ICircuitBoardHolder> optional = LazyOptional.of(() -> this);
    private final BlockEntity blockEntity;

    public CircuitBoardHolder(BlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }

    @Override
    public ItemStack getCircuitBoard() {
        return circuitBoard.copy();
    }

    @Override
    public void setCircuitBoard(ItemStack stack) {
        boolean changedBoard = !ItemStack.isSameItemSameTags(circuitBoard, stack);
        circuitBoard = stack.copy();
        if (changedBoard) {
            warmupProgress = 0;
        }
        if (blockEntity != null) {
            blockEntity.setChanged();
        }
    }

    @Override
    public int getWarmupProgress() {
        return warmupProgress;
    }

    @Override
    public void setWarmupProgress(int progress) {
        int clamped = Math.max(0, progress);
        if (warmupProgress != clamped) {
            warmupProgress = clamped;
            if (blockEntity != null) {
                blockEntity.setChanged();
            }
        }
    }

    @Override
    public @NotNull <T> LazyOptional<T> getCapability(@NotNull Capability<T> cap, @Nullable Direction side) {
        return CIRCUIT_BOARD_CAPABILITY.orEmpty(cap, optional);
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        if (!circuitBoard.isEmpty()) {
            tag.put("CircuitBoard", circuitBoard.serializeNBT());
        }
        if (warmupProgress > 0) {
            tag.putInt("WarmupProgress", warmupProgress);
        }
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag tag) {
        if (tag.contains("CircuitBoard")) {
            circuitBoard = ItemStack.of(tag.getCompound("CircuitBoard"));
        } else {
            circuitBoard = ItemStack.EMPTY;
        }
        warmupProgress = tag.getInt("WarmupProgress");
    }
}
