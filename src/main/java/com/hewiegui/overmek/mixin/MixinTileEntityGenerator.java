package com.hewiegui.overmek.mixin;

import com.hewiegui.overmek.util.CircuitBoardOverclockHelper;
import mekanism.generators.common.tile.TileEntityGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileEntityGenerator.class, remap = false)
public abstract class MixinTileEntityGenerator {

    @Inject(method = "onUpdateServer", at = @At("HEAD"))
    private void overmek$syncGeneratorMaxEnergy(CallbackInfo ci) {
        CircuitBoardOverclockHelper.syncAdjustedMaxEnergy((TileEntityGenerator) (Object) this);
    }
}
