package com.hewiegui.overmek.capability;

import com.hewiegui.overmek.item.CircuitBoardItem;
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

public class CircuitBoardHolder implements ICircuitBoardHolder,
        ICapabilitySerializable<CompoundTag> {

    // Capability Token，全局唯一
    public static final Capability<ICircuitBoardHolder> CIRCUIT_BOARD_CAPABILITY =
        CapabilityManager.get(new CapabilityToken<>() {});

    private ItemStack circuitBoard = ItemStack.EMPTY;
    private final LazyOptional<ICircuitBoardHolder> optional =
        LazyOptional.of(() -> this);

    // 关联的 BlockEntity
    private final BlockEntity blockEntity;

    public CircuitBoardHolder(BlockEntity blockEntity) {
        this.blockEntity = blockEntity;
    }

    @Override
    public ItemStack getCircuitBoard() { return circuitBoard; }

    @Override
    public void setCircuitBoard(ItemStack stack) { this.circuitBoard = stack; }

    @Override
    public CompoundTag getPersistentData() {
        return blockEntity != null ? blockEntity.getPersistentData() : null;
    }

    // Capability 提供
    @Override
    public @NotNull <T> LazyOptional<T> getCapability(
            @NotNull Capability<T> cap, @Nullable Direction side) {
        return CIRCUIT_BOARD_CAPABILITY.orEmpty(cap, optional);
    }

    // NBT 序列化（存档保存）
    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        if (!circuitBoard.isEmpty()) {
            tag.put("CircuitBoard", circuitBoard.serializeNBT());
        }
        return tag;
    }

    // NBT 反序列化（存档读取）
    @Override
    public void deserializeNBT(CompoundTag tag) {
        if (tag.contains("CircuitBoard")) {
            circuitBoard = ItemStack.of(tag.getCompound("CircuitBoard"));
        }
    }
}
