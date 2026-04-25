package com.hewiegui.overmek.mixin.compat.moremachine;

import com.hewiegui.overmek.util.CircuitBoardOverclockHelper;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.util.MekanismUtils;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.jerry.mekmm.common.tile.machine.TileEntityAmbientGasCollector", remap = false)
public abstract class MixinTileEntityAmbientGasCollector {

    @Shadow
    public int ticksRequired;

    @Shadow
    @Final
    private static int BASE_TICKS_REQUIRED;

    @Inject(method = "onUpdateServer", at = @At("HEAD"))
    private void overmek$adjustTicksRequired(CallbackInfo ci) {
        TileEntityMekanism self = (TileEntityMekanism) (Object) this;
        ticksRequired = CircuitBoardOverclockHelper.getAdjustedTicksRequired(self, MekanismUtils.getTicks(self, BASE_TICKS_REQUIRED));
    }
}
