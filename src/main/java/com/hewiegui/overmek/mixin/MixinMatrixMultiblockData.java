package com.hewiegui.overmek.mixin;

import com.hewiegui.overmek.util.CircuitBoardOverclockHelper;
import com.hewiegui.overmek.util.ICircuitBoardMultiblockData;
import mekanism.api.math.FloatingLong;
import mekanism.common.content.matrix.MatrixMultiblockData;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.multiblock.TileEntityInductionCasing;
import net.minecraft.world.level.Level;
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

    @Inject(method = "tick", at = @At("TAIL"))
    private void overmek$tickMatrixWarmup(Level world, CallbackInfoReturnable<Boolean> cir) {
        if (overmek$ownerTile == null) {
            return;
        }
        boolean active = !getLastInput().isZero() || !getLastOutput().isZero();
        CircuitBoardOverclockHelper.tickWarmup(overmek$ownerTile, active);
    }

    @Override
    public @Nullable TileEntityMekanism overmek$getOwnerTile() {
        return overmek$ownerTile;
    }
}
