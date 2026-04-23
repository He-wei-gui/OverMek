package com.hewiegui.overmek.mixin;

import com.hewiegui.overmek.util.CircuitBoardOverclockHelper;
import mekanism.common.tile.prefab.TileEntityAdvancedElectricMachine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileEntityAdvancedElectricMachine.class, remap = false)
public abstract class MixinTileEntityAdvancedElectricMachine {

    @Inject(method = "onUpdateServer", at = @At("TAIL"))
    private void overmek$tickWarmup(CallbackInfo ci) {
        TileEntityAdvancedElectricMachine self = (TileEntityAdvancedElectricMachine) (Object) this;
        CircuitBoardOverclockHelper.tickWarmup(self, self.getActive());
    }
}
