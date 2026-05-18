package com.hewiegui.overmek.mixin;

import com.hewiegui.overmek.util.CircuitBoardGeneratorHelper;
import com.hewiegui.overmek.util.CircuitBoardOverclockHelper;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.math.FloatingLong;
import mekanism.common.capabilities.fluid.BasicFluidTank;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.generators.common.config.MekanismGeneratorsConfig;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Pseudo
@Mixin(targets = "com.jerry.meklg.common.tile.TileEntityLargeHeatGenerator", remap = false)
public abstract class MixinMoreHeatGenerator {

    @Shadow
    public BasicFluidTank lavaTank;

    @Shadow
    private FloatingLong producingEnergy;

    @Inject(method = "onUpdateServer", at = @At("HEAD"))
    private void overmek$syncAdjustedMaxEnergy(CallbackInfo ci) {
        CircuitBoardOverclockHelper.syncAdjustedMaxEnergy((TileEntityMekanism) (Object) this);
    }

    @Inject(method = "onUpdateServer", at = @At("TAIL"))
    private void overmek$applyCircuitBoardHeatBoost(CallbackInfo ci) {
        TileEntityMekanism self = (TileEntityMekanism) (Object) this;
        FloatingLong baseProduction = producingEnergy;
        boolean active = baseProduction != null && !baseProduction.isZero();
        CircuitBoardGeneratorHelper.tickGeneratorWarmup(self, active);

        if (!active) {
            CircuitBoardGeneratorHelper.resetFuelRemainder(self);
            return;
        }
        int baseUnits = MekanismGeneratorsConfig.generators.heatGenerationFluidRate.get();
        int requestedExtraLava = CircuitBoardGeneratorHelper.takeExtraFuelUnits(self, baseUnits);
        int actualExtraLava = (requestedExtraLava <= 0 || lavaTank == null) ? 0
            : lavaTank.extract(requestedExtraLava, Action.EXECUTE, AutomationType.INTERNAL).getAmount();
        double fulfillment = requestedExtraLava <= 0 ? 0.0D : actualExtraLava / (double) requestedExtraLava;
        FloatingLong extraEnergy = CircuitBoardGeneratorHelper.getExtraGeneration(self, baseProduction, fulfillment);
        if (extraEnergy.isZero()) {
            return;
        }
        IEnergyContainer ec = self.getEnergyContainers(null).isEmpty() ? null : self.getEnergyContainers(null).get(0);
        if (ec != null) {
            FloatingLong needed = ec.getNeeded();
            if (!needed.isZero()) {
                FloatingLong toAdd = extraEnergy.min(needed);
                ec.setEnergy(ec.getEnergy().add(toAdd));
            }
        }
        // meklg 在方法主体最后用 (currentEnergy - initialEnergy) 计算 producingEnergy，
        // 覆盖为理论总产值，使 GUI 显示反映加成。
        producingEnergy = baseProduction.add(extraEnergy);
    }
}
