package com.hewiegui.overmek.mixin;

import com.hewiegui.overmek.util.CircuitBoardGeneratorHelper;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.math.FloatingLong;
import mekanism.common.capabilities.fluid.BasicFluidTank;
import mekanism.generators.common.config.MekanismGeneratorsConfig;
import mekanism.generators.common.tile.TileEntityHeatGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileEntityHeatGenerator.class, remap = false)
public abstract class MixinTileEntityHeatGenerator {

    @Shadow
    public BasicFluidTank lavaTank;

    @Shadow
    public abstract FloatingLong getProductionRate();

    @Inject(method = "onUpdateServer", at = @At("TAIL"))
    private void overmek$applyCircuitBoardHeatBoost(CallbackInfo ci) {
        TileEntityHeatGenerator self = (TileEntityHeatGenerator) (Object) this;
        FloatingLong baseProduction = getProductionRate();
        boolean active = !baseProduction.isZero();
        CircuitBoardGeneratorHelper.tickGeneratorWarmup(self, active);
        if (!active) {
            CircuitBoardGeneratorHelper.resetFuelRemainder(self);
            return;
        }
        int requestedExtraLava = CircuitBoardGeneratorHelper.takeExtraFuelUnits(self, MekanismGeneratorsConfig.generators.heatGenerationFluidRate.get());
        int actualExtraLava = requestedExtraLava <= 0 ? 0 : lavaTank.extract(requestedExtraLava, Action.EXECUTE, AutomationType.INTERNAL).getAmount();
        double fulfillment = requestedExtraLava <= 0 ? 0.0D : actualExtraLava / (double) requestedExtraLava;
        FloatingLong extraEnergy = CircuitBoardGeneratorHelper.getExtraGeneration(self, baseProduction, fulfillment);
        if (!extraEnergy.isZero()) {
            self.getEnergyContainer().insert(extraEnergy, Action.EXECUTE, AutomationType.INTERNAL);
        }
    }
}
