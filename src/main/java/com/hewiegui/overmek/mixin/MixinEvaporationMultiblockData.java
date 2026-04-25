package com.hewiegui.overmek.mixin;

import com.hewiegui.overmek.util.CircuitBoardMultiblockHelper;
import com.hewiegui.overmek.util.CircuitBoardOverclockHelper;
import com.hewiegui.overmek.util.ICircuitBoardMultiblockData;
import mekanism.common.content.evaporation.EvaporationMultiblockData;
import mekanism.common.recipe.lookup.monitor.RecipeCacheLookupMonitor;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.multiblock.TileEntityThermalEvaporationBlock;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = EvaporationMultiblockData.class, remap = false)
public abstract class MixinEvaporationMultiblockData implements ICircuitBoardMultiblockData {

    @Shadow
    @Final
    private RecipeCacheLookupMonitor<?> recipeCacheLookupMonitor;
    @Shadow
    public double lastGain;

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
        int extraPasses = CircuitBoardMultiblockHelper.getExtraEvaporationPasses(overmek$ownerTile);
        for (int i = 0; i < extraPasses; i++) {
            recipeCacheLookupMonitor.updateAndProcess();
        }
        CircuitBoardOverclockHelper.tickWarmup(overmek$ownerTile, lastGain > 0);
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
        cir.setReturnValue((int) Math.max(cir.getReturnValueI(), Math.round(cir.getReturnValueI() * CircuitBoardMultiblockHelper.getEffectiveBufferMultiplier(overmek$ownerTile))));
    }

    @Override
    public @Nullable TileEntityMekanism overmek$getOwnerTile() {
        return overmek$ownerTile;
    }
}
