package com.hewiegui.overmek.mixin;

import com.hewiegui.overmek.util.CircuitBoardOverclockHelper;
import mekanism.common.tile.factory.TileEntityFactory;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TileEntityFactory.class, remap = false)
public abstract class MixinTileEntityFactory {

    @Inject(method = "getTicksRequired", at = @At("RETURN"), cancellable = true)
    private void overmek$adjustTicksRequired(CallbackInfoReturnable<Integer> cir) {
        TileEntityFactory<?> self = (TileEntityFactory<?>) (Object) this;
        cir.setReturnValue(CircuitBoardOverclockHelper.getAdjustedTicksRequired(self, cir.getReturnValueI()));
    }

    @Inject(method = "onUpdateServer", at = @At("TAIL"))
    private void overmek$tickWarmup(CallbackInfo ci) {
        TileEntityFactory<?> self = (TileEntityFactory<?>) (Object) this;
        CircuitBoardOverclockHelper.tickWarmup(self, self.getActive());
    }
}
