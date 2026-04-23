package com.hewiegui.overmek.mixin;

import com.hewiegui.overmek.util.CircuitBoardOverclockHelper;
import mekanism.api.math.FloatingLong;
import mekanism.common.capabilities.energy.MachineEnergyContainer;
import mekanism.common.tile.base.TileEntityMekanism;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MachineEnergyContainer.class, remap = false)
public abstract class MixinMachineEnergyContainer {

    @Shadow
    @Final
    protected TileEntityMekanism tile;

    @Inject(method = "getEnergyPerTick", at = @At("RETURN"), cancellable = true)
    private void overmek$adjustEnergyPerTick(CallbackInfoReturnable<FloatingLong> cir) {
        cir.setReturnValue(CircuitBoardOverclockHelper.getAdjustedEnergyPerTick(tile, cir.getReturnValue()));
    }

    @Inject(method = "getMaxEnergy", at = @At("RETURN"), cancellable = true)
    private void overmek$adjustMaxEnergy(CallbackInfoReturnable<FloatingLong> cir) {
        cir.setReturnValue(CircuitBoardOverclockHelper.getAdjustedMaxEnergy(tile, cir.getReturnValue()));
    }
}
