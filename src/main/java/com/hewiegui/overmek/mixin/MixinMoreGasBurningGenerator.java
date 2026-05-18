package com.hewiegui.overmek.mixin;

import com.hewiegui.overmek.util.CircuitBoardGeneratorHelper;
import com.hewiegui.overmek.util.CircuitBoardOverclockHelper;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.math.FloatingLong;
import mekanism.common.tile.base.TileEntityMekanism;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Pseudo;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Pseudo
@Mixin(targets = "com.jerry.meklg.common.tile.TileEntityLargeGasBurningGenerator", remap = false)
public abstract class MixinMoreGasBurningGenerator {

    @Shadow
    private double gasUsedLastTick;

    @Shadow
    private FloatingLong generationRate;

    @Shadow
    private int maxBurnTicks;

    @Inject(method = "onUpdateServer", at = @At("HEAD"))
    private void overmek$syncAdjustedMaxEnergy(CallbackInfo ci) {
        CircuitBoardOverclockHelper.syncAdjustedMaxEnergy((TileEntityMekanism) (Object) this);
    }

    @Inject(method = "onUpdateServer", at = @At("TAIL"))
    private void overmek$applyCircuitBoardGasBoost(CallbackInfo ci) {
        TileEntityMekanism self = (TileEntityMekanism) (Object) this;

        if (gasUsedLastTick <= 0.0D || generationRate == null || generationRate.isZero() || maxBurnTicks <= 0) {
            CircuitBoardGeneratorHelper.tickGeneratorWarmup(self, false);
            CircuitBoardGeneratorHelper.resetFuelRemainder(self);
            return;
        }
        // 与 meklg 内部 getProductionRate 的语义保持一致：generationRate * gasUsedLastTick * maxBurnTicks
        FloatingLong baseProduction = generationRate.multiply(gasUsedLastTick).multiply((long) maxBurnTicks);
        boolean active = !baseProduction.isZero();
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
        if (extraEnergy.isZero()) {
            return;
        }
        // 放大 maxOutput rate，否则下游（电缆/储能罐）每 tick 只能抽走 baseProduction，
        // 我额外加的能量会卡在容器里抽不出来，GUI 也只能显示到上限。
        // @Pseudo 模式下 @Shadow 不能解析父类方法，所以走反射。
        CircuitBoardOverclockHelper.tryUpdateGeneratorMaxOutput(self, baseProduction.add(extraEnergy));

        IEnergyContainer ec = self.getEnergyContainers(null).isEmpty() ? null : self.getEnergyContainers(null).get(0);
        if (ec != null) {
            FloatingLong needed = ec.getNeeded();
            if (!needed.isZero()) {
                FloatingLong toAdd = extraEnergy.min(needed);
                ec.setEnergy(ec.getEnergy().add(toAdd));
            }
        }
    }

    @Inject(method = "getProductionRate", at = @At("RETURN"), cancellable = true)
    private void overmek$scaleDisplayedProduction(CallbackInfoReturnable<FloatingLong> cir) {
        FloatingLong value = cir.getReturnValue();
        if (value == null || value.isZero()) {
            return;
        }
        double multiplier = CircuitBoardGeneratorHelper.getEffectiveGenerationMultiplier((TileEntityMekanism) (Object) this);
        if (multiplier > 1.0D) {
            cir.setReturnValue(value.multiply(multiplier));
        }
    }
}
