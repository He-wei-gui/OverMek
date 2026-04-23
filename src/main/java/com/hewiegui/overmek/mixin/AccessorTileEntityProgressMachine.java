package com.hewiegui.overmek.mixin;

import mekanism.common.tile.prefab.TileEntityProgressMachine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = TileEntityProgressMachine.class, remap = false)
public interface AccessorTileEntityProgressMachine {

    @Accessor("baseTicksRequired")
    int overmek$getBaseTicksRequired();
}
