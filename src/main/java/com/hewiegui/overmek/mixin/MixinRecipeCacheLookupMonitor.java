package com.hewiegui.overmek.mixin;

import com.hewiegui.overmek.util.CircuitBoardOverclockHelper;
import mekanism.api.recipes.MekanismRecipe;
import mekanism.api.recipes.cache.CachedRecipe;
import mekanism.common.recipe.lookup.IRecipeLookupHandler;
import mekanism.common.recipe.lookup.monitor.RecipeCacheLookupMonitor;
import mekanism.common.tile.base.TileEntityMekanism;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = RecipeCacheLookupMonitor.class, remap = false)
public abstract class MixinRecipeCacheLookupMonitor<RECIPE extends MekanismRecipe> {

    @Shadow
    @Final
    private IRecipeLookupHandler<RECIPE> handler;

    @Shadow
    protected CachedRecipe<RECIPE> cachedRecipe;

    @Inject(method = "updateAndProcess()Z", at = @At("RETURN"))
    private void overmek$applyExtraRecipePasses(CallbackInfoReturnable<Boolean> cir) {
        if (!cir.getReturnValueZ() || !(handler instanceof TileEntityMekanism tile) || cachedRecipe == null) {
            return;
        }
        int extraPasses = CircuitBoardOverclockHelper.getExtraRecipePasses(tile);
        for (int i = 0; i < extraPasses; i++) {
            cachedRecipe.process();
        }
    }
}
