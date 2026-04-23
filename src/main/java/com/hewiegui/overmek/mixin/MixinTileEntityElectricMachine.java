package com.hewiegui.overmek.mixin;

import com.hewiegui.overmek.util.CircuitBoardOverclockHelper;
import mekanism.common.tile.prefab.TileEntityElectricMachine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileEntityElectricMachine.class, remap = false)
public abstract class MixinTileEntityElectricMachine {

    @Inject(method = "onUpdateServer", at = @At("TAIL"))
    private void overmek$tickWarmup(CallbackInfo ci) {
        TileEntityElectricMachine self = (TileEntityElectricMachine) (Object) this;
        CircuitBoardOverclockHelper.tickWarmup(self, self.getActive());
    }
}
