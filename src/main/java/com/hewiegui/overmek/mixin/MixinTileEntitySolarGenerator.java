package com.hewiegui.overmek.mixin;

import com.hewiegui.overmek.util.CircuitBoardGeneratorHelper;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.math.FloatingLong;
import mekanism.generators.common.tile.TileEntitySolarGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileEntitySolarGenerator.class, remap = false)
public abstract class MixinTileEntitySolarGenerator {

    @Shadow
    public abstract FloatingLong getProductionRate();

    @Inject(method = "onUpdateServer", at = @At("TAIL"))
    private void overmek$applyCircuitBoardSolarBoost(CallbackInfo ci) {
        TileEntitySolarGenerator self = (TileEntitySolarGenerator) (Object) this;
        FloatingLong baseProduction = getProductionRate();
        boolean active = !baseProduction.isZero();
        CircuitBoardGeneratorHelper.tickGeneratorWarmup(self, active);
        if (!active) {
            CircuitBoardGeneratorHelper.resetFuelRemainder(self);
            return;
        }
        FloatingLong extraEnergy = CircuitBoardGeneratorHelper.getExtraGeneration(self, baseProduction, 1.0D);
        if (!extraEnergy.isZero()) {
            self.getEnergyContainer().insert(extraEnergy, Action.EXECUTE, AutomationType.INTERNAL);
        }
    }
}
