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

    @Shadow
    public abstract FloatingLong getEnergy();

    private double overmek$getCapacityMultiplier() {
        TileEntityMekanism owner = ((ICircuitBoardMultiblockData) multiblock).overmek$getOwnerTile();
        if (owner == null) {
            return 1.0D;
        }
        return CircuitBoardMultiblockHelper.getEffectiveMatrixCapacityMultiplier(owner);
    }

    private double overmek$getTransferMultiplier() {
        TileEntityMekanism owner = ((ICircuitBoardMultiblockData) multiblock).overmek$getOwnerTile();
        if (owner == null) {
            return 1.0D;
        }
        return CircuitBoardMultiblockHelper.getEffectiveMatrixTransferMultiplier(owner);
    }

    @Inject(method = "getMaxEnergy", at = @At("RETURN"), cancellable = true)
    private void overmek$boostMatrixStorage(CallbackInfoReturnable<FloatingLong> cir) {
        double multiplier = overmek$getCapacityMultiplier();
        FloatingLong base = cir.getReturnValue();
        FloatingLong adjusted = multiplier > 1.0D ? base.multiply(multiplier) : base;
        FloatingLong stored = getEnergy();
        if (stored != null && stored.greaterThan(adjusted)) {
            adjusted = stored;
        }
        cir.setReturnValue(adjusted);
    }

    @Inject(method = "getMaxTransfer", at = @At("RETURN"), cancellable = true)
    private void overmek$boostMatrixTransfer(CallbackInfoReturnable<FloatingLong> cir) {
        double multiplier = overmek$getTransferMultiplier();
        if (multiplier > 1.0D) {
            cir.setReturnValue(cir.getReturnValue().multiply(multiplier));
        }
    }

    @Inject(method = "getRemainingInput", at = @At("RETURN"), cancellable = true)
    private void overmek$boostMatrixInputRate(CallbackInfoReturnable<FloatingLong> cir) {
        double multiplier = overmek$getTransferMultiplier();
        if (multiplier > 1.0D) {
            cir.setReturnValue(cir.getReturnValue().multiply(multiplier));
        }
    }

    @Inject(method = "getRemainingOutput", at = @At("RETURN"), cancellable = true)
    private void overmek$boostMatrixOutputRate(CallbackInfoReturnable<FloatingLong> cir) {
        double multiplier = overmek$getTransferMultiplier();
        if (multiplier > 1.0D) {
            cir.setReturnValue(cir.getReturnValue().multiply(multiplier));
        }
    }
}
