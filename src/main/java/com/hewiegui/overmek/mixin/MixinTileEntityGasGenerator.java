package com.hewiegui.overmek.mixin;

import com.hewiegui.overmek.util.CircuitBoardGeneratorHelper;
import mekanism.api.Action;
import mekanism.api.math.FloatingLong;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.generators.common.tile.TileEntityGasGenerator;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileEntityGasGenerator.class, remap = false)
public abstract class MixinTileEntityGasGenerator {

    @Shadow
    private double gasUsedLastTick;

    @Shadow
    abstract FloatingLong getProductionRate();

    @Inject(method = "onUpdateServer", at = @At("TAIL"))
    private void overmek$applyCircuitBoardGasBoost(CallbackInfo ci) {
        TileEntityGasGenerator self = (TileEntityGasGenerator) (Object) this;
        FloatingLong baseProduction = getProductionRate();
        boolean active = gasUsedLastTick > 0.0D && !baseProduction.isZero();
        CircuitBoardGeneratorHelper.tickGeneratorWarmup(self, active);
        if (!active) {
            CircuitBoardGeneratorHelper.resetFuelRemainder(self);
            return;
        }
        IGasTank fuelTank = self.getGasTanks(null).isEmpty() ? null : self.getGasTanks(null).get(0);
        if (fuelTank == null) {
            return;
        }
        int requestedExtraFuel = CircuitBoardGeneratorHelper.takeExtraFuelUnits(self, gasUsedLastTick);
        long actualExtraFuel = requestedExtraFuel <= 0 ? 0 : fuelTank.shrinkStack(requestedExtraFuel, Action.EXECUTE);
        double fulfillment = requestedExtraFuel <= 0 ? 0.0D : actualExtraFuel / (double) requestedExtraFuel;
        FloatingLong extraEnergy = CircuitBoardGeneratorHelper.getExtraGeneration(self, baseProduction, fulfillment);
        if (!extraEnergy.isZero()) {
            self.getEnergyContainer().insert(extraEnergy, Action.EXECUTE, mekanism.api.AutomationType.INTERNAL);
        }
    }
}
