package com.hewiegui.overmek.mixin;

import com.hewiegui.overmek.util.CircuitBoardMultiblockHelper;
import com.hewiegui.overmek.util.CircuitBoardOverclockHelper;
import com.hewiegui.overmek.util.ICircuitBoardMultiblockData;
import com.hewiegui.overmek.util.OverMekDebug;
import mekanism.common.content.evaporation.EvaporationMultiblockData;
import mekanism.common.recipe.lookup.monitor.RecipeCacheLookupMonitor;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.multiblock.TileEntityThermalEvaporationBlock;
import java.util.function.IntSupplier;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EvaporationMultiblockData.class, remap = false)
public abstract class MixinEvaporationMultiblockData implements ICircuitBoardMultiblockData {

    @Shadow
    @Final
    private RecipeCacheLookupMonitor<?> recipeCacheLookupMonitor;
    @Shadow
    public double lastGain;
    @Shadow
    public mekanism.common.capabilities.fluid.BasicFluidTank inputTank;

    @Unique
    private TileEntityMekanism overmek$ownerTile;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void overmek$captureOwner(TileEntityThermalEvaporationBlock tile, CallbackInfo ci) {
        overmek$ownerTile = tile;
    }

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lmekanism/common/recipe/lookup/monitor/RecipeCacheLookupMonitor;updateAndProcess()Z", shift = At.Shift.AFTER))
    private void overmek$accelerateEvaporation(net.minecraft.world.level.Level world, CallbackInfoReturnable<Boolean> cir) {
        if (overmek$ownerTile == null) {
            return;
        }
        CircuitBoardOverclockHelper.tickWarmup(overmek$ownerTile, lastGain > 0);
        double throughputMultiplier = CircuitBoardMultiblockHelper.getEffectiveEvaporationThroughputMultiplier(overmek$ownerTile);
        OverMekDebug.logEvaporationTick(overmek$ownerTile, lastGain, throughputMultiplier);
        if (throughputMultiplier > 1.0D && lastGain > 0) {
            lastGain *= throughputMultiplier;
        }
    }

    @ModifyArg(
        method = "createNewCachedRecipe",
        at = @At(
            value = "INVOKE",
            target = "Lmekanism/api/recipes/cache/CachedRecipe;setBaselineMaxOperations(Ljava/util/function/IntSupplier;)Lmekanism/api/recipes/cache/CachedRecipe;"
        ),
        index = 0
    )
    private IntSupplier overmek$scaleEvaporationOperations(IntSupplier baselineOperations) {
        return () -> {
            int baseline = baselineOperations.getAsInt();
            if (overmek$ownerTile == null || baseline <= 0) {
                return baseline;
            }
            double throughputMultiplier = CircuitBoardMultiblockHelper.getEffectiveEvaporationThroughputMultiplier(overmek$ownerTile);
            if (throughputMultiplier <= 1.0D) {
                OverMekDebug.logEvaporationOperations(overmek$ownerTile, baseline, baseline, throughputMultiplier);
                return baseline;
            }
            long scaled = Math.round(baseline * throughputMultiplier);
            int result = (int) Math.min(Integer.MAX_VALUE, Math.max(baseline, scaled));
            OverMekDebug.logEvaporationOperations(overmek$ownerTile, baseline, result, throughputMultiplier);
            return result;
        };
    }

    @Redirect(
        method = "simulateEnvironment",
        at = @At(
            value = "INVOKE",
            target = "Lmekanism/common/config/CachedDoubleValue;get()D",
            ordinal = 1
        )
    )
    private double overmek$reduceHeatDissipation(mekanism.common.config.value.CachedDoubleValue instance) {
        if (overmek$ownerTile == null) {
            return instance.get();
        }
        double base = instance.get();
        double factor = CircuitBoardMultiblockHelper.getEffectiveHeatDissipationFactor(overmek$ownerTile);
        double adjusted = base * factor;
        OverMekDebug.logEvaporationHeat(overmek$ownerTile, base, adjusted, factor);
        return adjusted;
    }

    @Inject(method = "getMaxFluid", at = @At("RETURN"), cancellable = true)
    private void overmek$expandEvaporationInputBuffer(CallbackInfoReturnable<Integer> cir) {
        if (overmek$ownerTile == null) {
            return;
        }
        int tier = CircuitBoardOverclockHelper.getInstalledTier(overmek$ownerTile);
        if (tier < 0) {
            return;
        }
        int base = cir.getReturnValueI();
        long scaled = Math.round((long) base * CircuitBoardMultiblockHelper.getEffectiveBufferMultiplier(overmek$ownerTile));
        int stored = 0;
        if (inputTank != null && inputTank.getFluid() != null) {
            stored = inputTank.getFluid().getAmount();
        }
        long finalValue = Math.max(stored, Math.max(base, scaled));
        cir.setReturnValue((int) Math.min(Integer.MAX_VALUE, finalValue));
    }

    @Override
    public @Nullable TileEntityMekanism overmek$getOwnerTile() {
        return overmek$ownerTile;
    }
}
