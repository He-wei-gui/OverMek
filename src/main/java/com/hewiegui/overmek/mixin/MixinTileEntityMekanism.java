package com.hewiegui.overmek.mixin;

import com.hewiegui.overmek.util.CircuitBoardOverclockHelper;
import com.hewiegui.overmek.util.ICircuitBoardDisplayData;
import mekanism.api.Upgrade;
import mekanism.common.inventory.container.MekanismContainer;
import mekanism.common.inventory.container.sync.SyncableInt;
import mekanism.common.tile.base.TileEntityMekanism;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = TileEntityMekanism.class, remap = false)
public abstract class MixinTileEntityMekanism implements ICircuitBoardDisplayData {

    @Unique
    private int overmek$syncedWarmupProgress;

    @Inject(method = "addContainerTrackers", at = @At("TAIL"))
    private void overmek$trackCircuitBoardWarmup(MekanismContainer container, CallbackInfo ci) {
        TileEntityMekanism self = (TileEntityMekanism) (Object) this;
        container.track(SyncableInt.create(
            () -> CircuitBoardOverclockHelper.getWarmupProgress(self),
            this::overmek$setSyncedWarmupProgress
        ));
    }

    @Override
    public int overmek$getSyncedWarmupProgress() {
        return overmek$syncedWarmupProgress;
    }

    @Override
    public void overmek$setSyncedWarmupProgress(int progress) {
        overmek$syncedWarmupProgress = Math.max(0, progress);
    }

    @Inject(method = "recalculateUpgrades", at = @At("TAIL"))
    private void overmek$resetWarmupOnSpeedUpgrade(Upgrade upgrade, CallbackInfo ci) {
        if (upgrade == Upgrade.SPEED) {
            TileEntityMekanism self = (TileEntityMekanism) (Object) this;
            CircuitBoardOverclockHelper.resetWarmup(self);
        }
    }
}
