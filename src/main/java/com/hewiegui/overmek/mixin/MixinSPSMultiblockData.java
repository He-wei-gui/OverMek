package com.hewiegui.overmek.mixin;

import com.hewiegui.overmek.util.BoardHostResolver;
import com.hewiegui.overmek.util.CircuitBoardMultiblockHelper;
import com.hewiegui.overmek.util.CircuitBoardOverclockHelper;
import com.hewiegui.overmek.util.ICircuitBoardMultiblockData;
import com.hewiegui.overmek.util.OverMekDebug;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.math.MathUtils;
import mekanism.common.config.MekanismConfig;
import mekanism.common.content.sps.SPSMultiblockData;
import mekanism.common.registries.MekanismGases;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.multiblock.TileEntitySPSCasing;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyArg;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(value = SPSMultiblockData.class, remap = false)
public abstract class MixinSPSMultiblockData implements ICircuitBoardMultiblockData {

    @Shadow public IGasTank inputTank;
    @Shadow public IGasTank outputTank;
    @Shadow public double progress;
    @Shadow public int inputProcessed;
    @Shadow public double lastProcessed;
    @Shadow public mekanism.api.math.FloatingLong lastReceivedEnergy;
    @Shadow public boolean couldOperate;
    @Shadow private AABB deathZone;

    @Unique
    private TileEntityMekanism overmek$ownerTile;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void overmek$captureOwner(TileEntitySPSCasing tile, CallbackInfo ci) {
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
        if (currentHost != overmek$ownerTile && currentHost instanceof TileEntityMekanism mekHost) {
            overmek$ownerTile = mekHost;
        }
        BoardHostResolver.resolveHolder(overmek$ownerTile);
    }

    @Inject(method = "tick", at = @At("TAIL"))
    private void overmek$boostSps(Level world, CallbackInfoReturnable<Boolean> cir) {
        if (overmek$ownerTile == null) {
            return;
        }
        boolean active = lastProcessed > 0 || (couldOperate && !lastReceivedEnergy.isZero());
        CircuitBoardOverclockHelper.tickWarmup(overmek$ownerTile, active);
        if (!active) {
            return;
        }

        long extraProcessed = overmek$processExtraInput();
        if (extraProcessed > 0) {
            lastProcessed += extraProcessed;
        }
        overmek$applyPressureDamage(world, extraProcessed);
    }

    @Inject(method = "getMaxInputGas", at = @At("RETURN"), cancellable = true)
    private void overmek$expandSpsInputBuffer(CallbackInfoReturnable<Long> cir) {
        if (overmek$ownerTile == null) {
            return;
        }
        long base = cir.getReturnValueJ();
        long scaled = Math.round(base * CircuitBoardMultiblockHelper.getEffectiveSpsBufferMultiplier(overmek$ownerTile));
        long stored = inputTank == null ? 0L : inputTank.getStored();
        cir.setReturnValue(Math.max(stored, Math.max(base, scaled)));
    }

    @ModifyArg(
        method = "supplyCoilEnergy",
        at = @At(value = "INVOKE", target = "Lmekanism/api/math/FloatingLong;plusEqual(Lmekanism/api/math/FloatingLong;)Lmekanism/api/math/FloatingLong;"),
        index = 0
    )
    private mekanism.api.math.FloatingLong overmek$reduceSpsEnergyUsageForInternalBuffer(mekanism.api.math.FloatingLong amount) {
        return overmek$adjustSuppliedEnergy(amount);
    }

    @ModifyArg(
        method = "supplyCoilEnergy",
        at = @At(value = "INVOKE", target = "Lmekanism/common/content/sps/SPSMultiblockData$CoilData;receiveEnergy(Lmekanism/api/math/FloatingLong;)V"),
        index = 0
    )
    private mekanism.api.math.FloatingLong overmek$reduceSpsEnergyUsageForCoilDisplay(mekanism.api.math.FloatingLong amount) {
        return overmek$adjustSuppliedEnergy(amount);
    }

    private long overmek$processExtraInput() {
        double throughputMultiplier = CircuitBoardMultiblockHelper.getEffectiveSpsThroughputMultiplier(overmek$ownerTile);
        if (throughputMultiplier <= 1.0D || inputTank.isEmpty() || outputTank.getNeeded() <= 0) {
            OverMekDebug.logSpsExtra(overmek$ownerTile, lastProcessed, 0L, 0L, inputTank.getStored(), outputTank.getNeeded());
            return 0L;
        }
        int inputPerAntimatter = MekanismConfig.general.spsInputPerAntimatter.get();
        long outputRoom = Math.max(0L, (inputPerAntimatter - inputProcessed) + inputPerAntimatter * Math.max(0L, outputTank.getNeeded() - 1));
        double baselineProcessed = lastProcessed;
        if (baselineProcessed <= 0.0D && !lastReceivedEnergy.isZero()) {
            baselineProcessed = lastReceivedEnergy.doubleValue() / MekanismConfig.general.spsEnergyPerInput.get().doubleValue();
        }
        long requestedExtra = Math.max(0L, Math.round(baselineProcessed * (throughputMultiplier - 1.0D)));
        long extraProcessed = Math.min(Math.min(requestedExtra, inputTank.getStored()), outputRoom);
        if (extraProcessed <= 0) {
            OverMekDebug.logSpsExtra(overmek$ownerTile, lastProcessed, requestedExtra, 0L, inputTank.getStored(), outputRoom);
            return 0L;
        }

        long processed = inputTank.shrinkStack(extraProcessed, Action.EXECUTE);
        if (processed <= 0) {
            OverMekDebug.logSpsExtra(overmek$ownerTile, lastProcessed, requestedExtra, 0L, inputTank.getStored(), outputRoom);
            return 0L;
        }

        int previousInputProcessed = inputProcessed;
        inputProcessed += MathUtils.clampToInt(processed);
        if (inputProcessed >= inputPerAntimatter) {
            GasStack toAdd = MekanismGases.ANTIMATTER.getStack(inputProcessed / inputPerAntimatter);
            outputTank.insert(toAdd, Action.EXECUTE, AutomationType.INTERNAL);
            inputProcessed %= inputPerAntimatter;
        }

        double pressureMultiplier = CircuitBoardMultiblockHelper.getEffectiveSpsPressureMultiplier(overmek$ownerTile);
        double stabilityMultiplier = CircuitBoardMultiblockHelper.getEffectiveSpsStabilityMultiplier(overmek$ownerTile);
        long waste = Math.max(0L, Math.round(processed * Math.max(0.0D, pressureMultiplier - 1.0D) / Math.max(1.0D, stabilityMultiplier)));
        if (waste > 0) {
            inputTank.shrinkStack(Math.min(waste, inputTank.getStored()), Action.EXECUTE);
        }

        if (previousInputProcessed != inputProcessed) {
            ((SPSMultiblockData) (Object) this).markDirty();
        }
        OverMekDebug.logSpsExtra(overmek$ownerTile, lastProcessed, requestedExtra, processed, inputTank.getStored(), outputRoom);
        return processed;
    }

    private void overmek$applyPressureDamage(Level world, long extraProcessed) {
        if (deathZone == null || lastReceivedEnergy.isZero()) {
            return;
        }
        double pressureMultiplier = CircuitBoardMultiblockHelper.getEffectiveSpsPressureMultiplier(overmek$ownerTile);
        double stabilityMultiplier = CircuitBoardMultiblockHelper.getEffectiveSpsStabilityMultiplier(overmek$ownerTile);
        if (pressureMultiplier <= 1.0D && stabilityMultiplier <= 1.0D && extraProcessed <= 0) {
            return;
        }
        if (world.getRandom().nextInt(10) != 0) {
            return;
        }
        float damage = (float) (lastReceivedEnergy.floatValue() / 1_000F * Math.max(1.0D, pressureMultiplier / Math.max(1.0D, stabilityMultiplier)));
        for (Entity entity : world.getEntitiesOfClass(Entity.class, deathZone)) {
            entity.hurt(entity.damageSources().magic(), damage);
        }
    }

    @Unique
    private mekanism.api.math.FloatingLong overmek$adjustSuppliedEnergy(mekanism.api.math.FloatingLong amount) {
        if (overmek$ownerTile == null || amount == null || amount.isZero()) {
            return amount;
        }
        double energyUsageMultiplier = CircuitBoardMultiblockHelper.getEffectiveSpsEnergyUsageMultiplier(overmek$ownerTile);
        if (energyUsageMultiplier <= 0.0001D || Math.abs(energyUsageMultiplier - 1.0D) < 0.0005D) {
            OverMekDebug.logSpsEnergy(overmek$ownerTile, amount, amount, energyUsageMultiplier);
            return amount;
        }
        mekanism.api.math.FloatingLong adjusted;
        try {
            adjusted = amount.divide(energyUsageMultiplier);
        } catch (ArithmeticException | IllegalArgumentException ex) {
            OverMekDebug.logSpsEnergy(overmek$ownerTile, amount, amount, energyUsageMultiplier);
            return amount;
        }
        OverMekDebug.logSpsEnergy(overmek$ownerTile, amount, adjusted, energyUsageMultiplier);
        return adjusted;
    }

    @Override
    public @Nullable TileEntityMekanism overmek$getOwnerTile() {
        return overmek$ownerTile;
    }
}
