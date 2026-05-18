package com.hewiegui.overmek.mixin;

import com.hewiegui.overmek.util.CircuitBoardOverclockHelper;
import mekanism.common.tile.base.TileEntityMekanism;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.jerry.mekmm.common.tile.factory.TileEntityMoreMachineFactory", remap = false)
public abstract class MixinMoreMachineFactory {

    @Inject(method = "getTicksRequired", at = @At("RETURN"), cancellable = true)
    private void overmek$adjustMoreMachineFactoryTicks(CallbackInfoReturnable<Integer> cir) {
        cir.setReturnValue(CircuitBoardOverclockHelper.getAdjustedTicksRequired((TileEntityMekanism) (Object) this, cir.getReturnValueI()));
    }
}
