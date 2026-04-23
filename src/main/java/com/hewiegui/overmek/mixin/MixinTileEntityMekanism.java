package com.hewiegui.overmek.mixin;

import com.hewiegui.overmek.util.CircuitBoardOverclockHelper;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.prefab.TileEntityProgressMachine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileEntityMekanism.class, remap = false)
public abstract class MixinTileEntityMekanism {

    @Inject(method = "onUpdateServer", at = @At("TAIL"))
    private void overmek$tickWarmupForProgressMachines(CallbackInfo ci) {
        TileEntityMekanism self = (TileEntityMekanism) (Object) this;
        if (self instanceof TileEntityProgressMachine<?>) {
            CircuitBoardOverclockHelper.tickWarmup(self, self.getActive());
        }
    }
}
