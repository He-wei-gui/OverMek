package com.hewiegui.overmek.mixin;

import com.hewiegui.overmek.util.CircuitBoardOverclockHelper;
import mekanism.common.tile.prefab.TileEntityProgressMachine;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TileEntityProgressMachine.class, remap = false)
public abstract class MixinTileEntityProgressMachine {

    @Inject(method = "getTicksRequired", at = @At("RETURN"), cancellable = true)
    private void overmek$adjustTicksRequired(CallbackInfoReturnable<Integer> cir) {
        TileEntityProgressMachine<?> self = (TileEntityProgressMachine<?>) (Object) this;
        cir.setReturnValue(CircuitBoardOverclockHelper.getAdjustedTicksRequired(self, cir.getReturnValueI()));
    }
}
