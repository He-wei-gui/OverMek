package com.hewiegui.overmek.mixin;

import com.hewiegui.overmek.util.CircuitBoardGeneratorHelper;
import com.hewiegui.overmek.util.CircuitBoardMultiblockHelper;
import com.hewiegui.overmek.util.ICircuitBoardMultiblockData;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.energy.IEnergyContainer;
import mekanism.api.math.FloatingLong;
import mekanism.common.config.MekanismConfig;
import mekanism.generators.common.config.MekanismGeneratorsConfig;
import mekanism.generators.common.content.turbine.TurbineMultiblockData;
import mekanism.generators.common.content.turbine.TurbineValidator;
import mekanism.generators.common.tile.turbine.TileEntityTurbineCasing;
import mekanism.common.tile.base.TileEntityMekanism;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = TurbineMultiblockData.class, remap = false)
public abstract class MixinTurbineMultiblockData implements ICircuitBoardMultiblockData {

    @Shadow public IGasTank gasTank;
    @Shadow public IEnergyContainer energyContainer;
    @Shadow public int blades;
    @Shadow public int coils;
    @Shadow public long lastSteamInput;
    @Shadow public long clientFlow;

    @Unique
    private TileEntityMekanism overmek$ownerTile;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void overmek$captureOwner(TileEntityTurbineCasing tile, CallbackInfo ci) {
        overmek$ownerTile = tile;
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void overmek$boostTurbine(Level world, CallbackInfoReturnable<Boolean> cir) {
        if (overmek$ownerTile == null) {
            return;
        }
        boolean active = lastSteamInput > 0;
        CircuitBoardGeneratorHelper.tickGeneratorWarmup(overmek$ownerTile, active);
        if (!active) {
            CircuitBoardGeneratorHelper.resetFuelRemainder(overmek$ownerTile);
            return;
        }
        double fuelMultiplier = CircuitBoardMultiblockHelper.getEffectiveGeneratorFuelMultiplier(overmek$ownerTile);
        if (fuelMultiplier <= 1.0D) {
            return;
        }
        long requestedExtraSteam = Math.max(0L, Math.round(lastSteamInput * (fuelMultiplier - 1.0D)));
        long extraSteam = Math.min(requestedExtraSteam, gasTank.getStored());
        if (extraSteam <= 0) {
            return;
        }
        gasTank.shrinkStack(extraSteam, Action.EXECUTE);
        FloatingLong energyMultiplier = MekanismConfig.general.maxEnergyPerSteam.get().divide(TurbineValidator.MAX_BLADES)
            .multiply(Math.min(blades, coils * MekanismGeneratorsConfig.generators.turbineBladesPerCoil.get()));
        energyContainer.insert(energyMultiplier.multiply(extraSteam), Action.EXECUTE, AutomationType.INTERNAL);
        clientFlow += extraSteam;
    }

    @Inject(method = "getProductionRate", at = @At("RETURN"), cancellable = true)
    private void overmek$boostDisplayedProductionRate(CallbackInfoReturnable<FloatingLong> cir) {
        if (overmek$ownerTile == null) {
            return;
        }
        cir.setReturnValue(cir.getReturnValue().multiply(CircuitBoardMultiblockHelper.getEffectiveGeneratorGenerationMultiplier(overmek$ownerTile)));
    }

    @Inject(method = "getMaxProduction", at = @At("RETURN"), cancellable = true)
    private void overmek$boostDisplayedMaxProduction(CallbackInfoReturnable<FloatingLong> cir) {
        if (overmek$ownerTile == null) {
            return;
        }
        cir.setReturnValue(cir.getReturnValue().multiply(CircuitBoardMultiblockHelper.getEffectiveGeneratorGenerationMultiplier(overmek$ownerTile)));
    }

    @Inject(method = "getEnergyCapacity", at = @At("RETURN"), cancellable = true)
    private void overmek$expandTurbineEnergyBuffer(CallbackInfoReturnable<FloatingLong> cir) {
        int tier = overmek$ownerTile == null ? -1 : com.hewiegui.overmek.util.CircuitBoardOverclockHelper.getInstalledTier(overmek$ownerTile);
        if (tier < 0) {
            return;
        }
        FloatingLong base = cir.getReturnValue();
        FloatingLong scaled = base.multiply(CircuitBoardMultiblockHelper.getEffectiveBufferMultiplier(overmek$ownerTile));
        FloatingLong stored = energyContainer == null ? null : energyContainer.getEnergy();
        if (stored != null && stored.greaterThan(scaled)) {
            scaled = stored;
        }
        cir.setReturnValue(scaled);
    }

    @Inject(method = "getSteamCapacity", at = @At("RETURN"), cancellable = true)
    private void overmek$expandTurbineSteamBuffer(CallbackInfoReturnable<Long> cir) {
        int tier = overmek$ownerTile == null ? -1 : com.hewiegui.overmek.util.CircuitBoardOverclockHelper.getInstalledTier(overmek$ownerTile);
        if (tier < 0) {
            return;
        }
        long base = cir.getReturnValueJ();
        long scaled = Math.round(base * CircuitBoardMultiblockHelper.getEffectiveBufferMultiplier(overmek$ownerTile));
        long stored = gasTank == null ? 0L : gasTank.getStored();
        cir.setReturnValue(Math.max(stored, Math.max(base, scaled)));
    }

    @Override
    public @Nullable TileEntityMekanism overmek$getOwnerTile() {
        return overmek$ownerTile;
    }
}
