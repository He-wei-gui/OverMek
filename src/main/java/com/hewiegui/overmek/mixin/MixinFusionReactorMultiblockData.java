package com.hewiegui.overmek.mixin;

import com.hewiegui.overmek.util.CircuitBoardGeneratorHelper;
import com.hewiegui.overmek.util.CircuitBoardMultiblockHelper;
import com.hewiegui.overmek.util.ICircuitBoardMultiblockData;
import mekanism.api.Action;
import mekanism.api.math.FloatingLong;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.generators.common.config.MekanismGeneratorsConfig;
import mekanism.generators.common.content.fusion.FusionReactorMultiblockData;
import mekanism.generators.common.tile.fusion.TileEntityFusionReactorBlock;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.util.MekanismUtils;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = FusionReactorMultiblockData.class, remap = false)
public abstract class MixinFusionReactorMultiblockData implements ICircuitBoardMultiblockData {

    @Shadow public IGasTank fuelTank;
    @Shadow public abstract boolean isBurning();
    @Shadow public abstract double getPlasmaTemp();
    @Shadow public abstract void setPlasmaTemp(double temp);
    @Shadow public abstract int getInjectionRate();
    @Shadow private long lastBurned;

    @Unique
    private TileEntityMekanism overmek$ownerTile;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void overmek$captureOwner(TileEntityFusionReactorBlock tile, CallbackInfo ci) {
        overmek$ownerTile = tile;
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lmekanism/generators/common/content/fusion/FusionReactorMultiblockData;transferHeat()V"))
    private void overmek$boostFusionBeforeTransfer(Level world, CallbackInfoReturnable<Boolean> cir) {
        if (overmek$ownerTile == null) {
            return;
        }
        CircuitBoardGeneratorHelper.tickGeneratorWarmup(overmek$ownerTile, isBurning());
        if (!isBurning()) {
            CircuitBoardGeneratorHelper.resetFuelRemainder(overmek$ownerTile);
            return;
        }
        double baseFuelUse = Math.max(1.0D, Math.max(lastBurned, getInjectionRate()));
        double fuelMultiplier = CircuitBoardMultiblockHelper.getEffectiveGeneratorFuelMultiplier(overmek$ownerTile);
        if (fuelMultiplier <= 1.0D) {
            return;
        }
        long requestedExtra = Math.max(0L, Math.round(baseFuelUse * (fuelMultiplier - 1.0D)));
        long extraBurn = Math.min(requestedExtra, fuelTank.getStored());
        if (extraBurn <= 0) {
            return;
        }
        MekanismUtils.logMismatchedStackSize(fuelTank.shrinkStack(extraBurn, Action.EXECUTE), extraBurn);
        setPlasmaTemp(getPlasmaTemp() + MekanismGeneratorsConfig.generators.energyPerFusionFuel.get().multiply(extraBurn).divide(100).doubleValue());
        lastBurned += extraBurn;
    }

    @Inject(method = "getMaxWater", at = @At("RETURN"), cancellable = true)
    private void overmek$expandFusionWaterBuffer(CallbackInfoReturnable<Integer> cir) {
        int tier = overmek$ownerTile == null ? -1 : com.hewiegui.overmek.util.CircuitBoardOverclockHelper.getInstalledTier(overmek$ownerTile);
        if (tier < 0) {
            return;
        }
        int base = cir.getReturnValueI();
        long scaled = Math.round((long) base * CircuitBoardMultiblockHelper.getEffectiveBufferMultiplier(overmek$ownerTile));
        long finalValue = Math.max(base, scaled);
        cir.setReturnValue((int) Math.min(Integer.MAX_VALUE, finalValue));
    }

    @Inject(method = "getMaxSteam", at = @At("RETURN"), cancellable = true)
    private void overmek$expandFusionSteamBuffer(CallbackInfoReturnable<Long> cir) {
        int tier = overmek$ownerTile == null ? -1 : com.hewiegui.overmek.util.CircuitBoardOverclockHelper.getInstalledTier(overmek$ownerTile);
        if (tier < 0) {
            return;
        }
        long base = cir.getReturnValueJ();
        long scaled = Math.round(base * CircuitBoardMultiblockHelper.getEffectiveBufferMultiplier(overmek$ownerTile));
        cir.setReturnValue(Math.max(base, scaled));
    }

    @Override
    public @Nullable TileEntityMekanism overmek$getOwnerTile() {
        return overmek$ownerTile;
    }
}
