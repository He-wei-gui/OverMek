package com.hewiegui.overmek.mixin;

import com.hewiegui.overmek.util.CircuitBoardOverclockHelper;
import mekanism.common.tile.prefab.TileEntityConfigurableMachine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileEntityConfigurableMachine.class, remap = false)
public abstract class MixinTileEntityConfigurableMachine {

    @Inject(method = "onUpdateServer", at = @At("HEAD"))
    private void overmek$syncEnergyCapacity(CallbackInfo ci) {
        TileEntityConfigurableMachine self = (TileEntityConfigurableMachine) (Object) this;
        CircuitBoardOverclockHelper.syncAdjustedMaxEnergy(self);
    }

    @Inject(method = "onUpdateServer", at = @At("TAIL"))
    private void overmek$tickWarmup(CallbackInfo ci) {
        TileEntityConfigurableMachine self = (TileEntityConfigurableMachine) (Object) this;
        CircuitBoardOverclockHelper.tickWarmup(self, self.getActive());
    }
}
