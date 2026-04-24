package com.hewiegui.overmek.mixin;

import com.hewiegui.overmek.util.CircuitBoardGeneratorHelper;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.math.FloatingLong;
import mekanism.common.capabilities.fluid.BasicFluidTank;
import mekanism.generators.common.config.MekanismGeneratorsConfig;
import mekanism.generators.common.tile.TileEntityBioGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileEntityBioGenerator.class, remap = false)
public abstract class MixinTileEntityBioGenerator {

    @Shadow
    public BasicFluidTank bioFuelTank;

    @Inject(method = "onUpdateServer", at = @At("TAIL"))
    private void overmek$applyCircuitBoardBioBoost(CallbackInfo ci) {
        TileEntityBioGenerator self = (TileEntityBioGenerator) (Object) this;
        boolean active = self.getActive();
        CircuitBoardGeneratorHelper.tickGeneratorWarmup(self, active);
        if (!active) {
            CircuitBoardGeneratorHelper.resetFuelRemainder(self);
            return;
        }
        int requestedExtraFuel = CircuitBoardGeneratorHelper.takeExtraFuelUnits(self, 1.0D);
        int actualExtraFuel = requestedExtraFuel <= 0 ? 0 : bioFuelTank.extract(requestedExtraFuel, Action.EXECUTE, AutomationType.INTERNAL).getAmount();
        double fulfillment = requestedExtraFuel <= 0 ? 0.0D : actualExtraFuel / (double) requestedExtraFuel;
        FloatingLong extraEnergy = CircuitBoardGeneratorHelper.getExtraGeneration(self, MekanismGeneratorsConfig.generators.bioGeneration.get(), fulfillment);
        if (!extraEnergy.isZero()) {
            self.getEnergyContainer().insert(extraEnergy, Action.EXECUTE, AutomationType.INTERNAL);
        }
    }
}
