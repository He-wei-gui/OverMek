package com.hewiegui.overmek.mixin;

import com.hewiegui.overmek.util.CircuitBoardMultiblockHelper;
import com.hewiegui.overmek.util.ICircuitBoardMultiblockData;
import mekanism.api.math.FloatingLong;
import mekanism.common.content.matrix.MatrixEnergyContainer;
import mekanism.common.content.matrix.MatrixMultiblockData;
import mekanism.common.tile.base.TileEntityMekanism;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = MatrixEnergyContainer.class, remap = false)
public abstract class MixinMatrixEnergyContainer {

    @Shadow
    @Final
    private MatrixMultiblockData multiblock;

    @Inject(method = "getMaxEnergy", at = @At("RETURN"), cancellable = true)
    private void overmek$boostMatrixStorage(CallbackInfoReturnable<FloatingLong> cir) {
        TileEntityMekanism owner = ((ICircuitBoardMultiblockData) multiblock).overmek$getOwnerTile();
        if (owner != null) {
            cir.setReturnValue(cir.getReturnValue().multiply(CircuitBoardMultiblockHelper.getEffectiveMatrixCapacityMultiplier(owner)));
        }
    }

    @Inject(method = "getMaxTransfer", at = @At("RETURN"), cancellable = true)
    private void overmek$boostMatrixTransfer(CallbackInfoReturnable<FloatingLong> cir) {
        TileEntityMekanism owner = ((ICircuitBoardMultiblockData) multiblock).overmek$getOwnerTile();
        if (owner != null) {
            cir.setReturnValue(cir.getReturnValue().multiply(CircuitBoardMultiblockHelper.getEffectiveMatrixTransferMultiplier(owner)));
        }
    }

    @Inject(method = "getRemainingInput", at = @At("RETURN"), cancellable = true)
    private void overmek$boostMatrixInputRate(CallbackInfoReturnable<FloatingLong> cir) {
        TileEntityMekanism owner = ((ICircuitBoardMultiblockData) multiblock).overmek$getOwnerTile();
        if (owner != null) {
            cir.setReturnValue(cir.getReturnValue().multiply(CircuitBoardMultiblockHelper.getEffectiveMatrixTransferMultiplier(owner)));
        }
    }

    @Inject(method = "getRemainingOutput", at = @At("RETURN"), cancellable = true)
    private void overmek$boostMatrixOutputRate(CallbackInfoReturnable<FloatingLong> cir) {
        TileEntityMekanism owner = ((ICircuitBoardMultiblockData) multiblock).overmek$getOwnerTile();
        if (owner != null) {
            cir.setReturnValue(cir.getReturnValue().multiply(CircuitBoardMultiblockHelper.getEffectiveMatrixTransferMultiplier(owner)));
        }
    }
}
