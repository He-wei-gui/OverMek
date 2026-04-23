package com.hewiegui.overmek.mixin;

import mekanism.common.tile.factory.TileEntityFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(value = TileEntityFactory.class, remap = false)
public interface AccessorTileEntityFactory {

    @Accessor("ticksRequired")
    int overmek$getSyncedTicksRequired();
}
