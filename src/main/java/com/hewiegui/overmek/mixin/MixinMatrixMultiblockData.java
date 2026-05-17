package com.hewiegui.overmek.mixin;

import com.hewiegui.overmek.util.BoardHostResolver;
import com.hewiegui.overmek.util.CircuitBoardMultiblockHelper;
import com.hewiegui.overmek.util.CircuitBoardOverclockHelper;
import com.hewiegui.overmek.util.ICircuitBoardMultiblockData;
import mekanism.api.math.FloatingLong;
import mekanism.common.content.matrix.MatrixMultiblockData;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.multiblock.TileEntityInductionCasing;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MatrixMultiblockData.class, remap = false)
public abstract class MixinMatrixMultiblockData implements ICircuitBoardMultiblockData {

    @Shadow public abstract FloatingLong getLastInput();
    @Shadow public abstract FloatingLong getLastOutput();

    @Unique
    private TileEntityMekanism overmek$ownerTile;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void overmek$captureOwner(TileEntityInductionCasing tile, CallbackInfo ci) {
        overmek$ownerTile = tile;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void overmek$resyncOwner(Level world, CallbackInfoReturnable<Boolean> cir) {
        var multiblock = (mekanism.common.lib.multiblock.MultiblockData) (Object) this;
        if (multiblock.locations == null || multiblock.locations.isEmpty()) {
            return;
        }
        if (overmek$ownerTile == null) {
            return;
        }
        BlockEntity currentHost = BoardHostResolver.resolveHost(overmek$ownerTile);
        if (currentHost != overmek$ownerTile && currentHost instanceof TileEntityMekanism) {
            overmek$ownerTile = (TileEntityMekanism) currentHost;
        }
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void overmek$tickMatrixWarmup(Level world, CallbackInfoReturnable<Boolean> cir) {
        if (overmek$ownerTile == null) {
            return;
        }
        boolean active = !getLastInput().isZero() || !getLastOutput().isZero();
        CircuitBoardOverclockHelper.tickWarmup(overmek$ownerTile, active);
    }

    @Inject(method = "getLastInput", at = @At("RETURN"), cancellable = true)
    private void overmek$boostDisplayedInput(CallbackInfoReturnable<FloatingLong> cir) {
        if (overmek$ownerTile == null) {
            return;
        }
        double multiplier = CircuitBoardMultiblockHelper.getEffectiveMatrixTransferMultiplier(overmek$ownerTile);
        if (multiplier > 1.0D) {
            cir.setReturnValue(cir.getReturnValue().multiply(multiplier));
        }
    }

    @Inject(method = "getLastOutput", at = @At("RETURN"), cancellable = true)
    private void overmek$boostDisplayedOutput(CallbackInfoReturnable<FloatingLong> cir) {
        if (overmek$ownerTile == null) {
            return;
        }
        double multiplier = CircuitBoardMultiblockHelper.getEffectiveMatrixTransferMultiplier(overmek$ownerTile);
        if (multiplier > 1.0D) {
            cir.setReturnValue(cir.getReturnValue().multiply(multiplier));
        }
    }

    @Override
    public @Nullable TileEntityMekanism overmek$getOwnerTile() {
        return overmek$ownerTile;
    }
}
