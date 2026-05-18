package com.hewiegui.overmek.mixin;

import com.hewiegui.overmek.util.CircuitBoardGeneratorHelper;
import com.hewiegui.overmek.util.CircuitBoardOverclockHelper;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.math.FloatingLong;
import mekanism.common.tile.base.TileEntityMekanism;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(
    targets = {
        "com.jerry.meklg.common.tile.TileEntityLargeWindGenerator"
    },
    remap = false
)
public abstract class MixinMoreGenerator {

    @Unique
    private FloatingLong overmek$storedEnergyAfterEject = FloatingLong.ZERO;
    @Unique
    private boolean overmek$capturedEnergyAfterEject;

    @Inject(
        method = "onUpdateServer",
        at = @At(
            value = "INVOKE",
            target = "Lcom/jerry/meklg/common/tile/TileEntityMoreGenerator;onUpdateServer()V",
            shift = At.Shift.AFTER
        )
    )
    private void overmek$captureEnergyAfterEject(CallbackInfo ci) {
        TileEntityMekanism self = (TileEntityMekanism) (Object) this;
        CircuitBoardOverclockHelper.syncAdjustedMaxEnergy(self);
        overmek$storedEnergyAfterEject = overmek$getStoredEnergy(self);
        overmek$capturedEnergyAfterEject = true;
    }

    @Inject(method = "onUpdateServer", at = @At("TAIL"))
    private void overmek$applyMoreGeneratorBoost(CallbackInfo ci) {
        if (!overmek$capturedEnergyAfterEject) {
            return;
        }
        overmek$capturedEnergyAfterEject = false;
        TileEntityMekanism self = (TileEntityMekanism) (Object) this;
        FloatingLong produced = overmek$getStoredEnergy(self).subtract(overmek$storedEnergyAfterEject);
        boolean active = !produced.isZero() && produced.greaterThan(FloatingLong.ZERO);
        CircuitBoardGeneratorHelper.tickGeneratorWarmup(self, active);
        if (!active) {
            CircuitBoardGeneratorHelper.resetFuelRemainder(self);
            return;
        }
        FloatingLong extraEnergy = CircuitBoardGeneratorHelper.getExtraGeneration(self, produced, 1.0D);
        if (!extraEnergy.isZero()) {
            IEnergyContainer energyContainer = overmek$getEnergyContainer(self);
            if (energyContainer != null) {
                energyContainer.insert(extraEnergy, Action.EXECUTE, AutomationType.INTERNAL);
            }
        }
    }

    @Unique
    private static FloatingLong overmek$getStoredEnergy(TileEntityMekanism tile) {
        IEnergyContainer energyContainer = overmek$getEnergyContainer(tile);
        return energyContainer == null ? FloatingLong.ZERO : energyContainer.getEnergy().copy();
    }

    @Unique
    private static IEnergyContainer overmek$getEnergyContainer(TileEntityMekanism tile) {
        var energyContainers = tile.getEnergyContainers(null);
        return energyContainers.isEmpty() ? null : energyContainers.get(0);
    }
}
