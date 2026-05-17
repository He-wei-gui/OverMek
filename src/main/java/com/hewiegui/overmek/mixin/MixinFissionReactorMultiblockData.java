package com.hewiegui.overmek.mixin;

import com.hewiegui.overmek.util.BoardHostResolver;
import com.hewiegui.overmek.util.CircuitBoardMultiblockHelper;
import com.hewiegui.overmek.util.ICircuitBoardMultiblockData;
import mekanism.api.Action;
import mekanism.api.AutomationType;
import mekanism.api.chemical.gas.GasStack;
import mekanism.api.chemical.gas.IGasTank;
import mekanism.api.chemical.gas.attribute.GasAttributes;
import mekanism.common.capabilities.fluid.VariableCapacityFluidTank;
import mekanism.common.capabilities.heat.VariableHeatCapacitor;
import mekanism.common.lib.multiblock.MultiblockData;
import mekanism.common.registries.MekanismGases;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.generators.common.config.MekanismGeneratorsConfig;
import mekanism.generators.common.content.fission.FissionReactorMultiblockData;
import mekanism.generators.common.tile.fission.TileEntityFissionReactorCasing;
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

@Mixin(value = FissionReactorMultiblockData.class, remap = false)
public abstract class MixinFissionReactorMultiblockData implements ICircuitBoardMultiblockData {

    @Shadow public IGasTank gasCoolantTank;
    @Shadow public VariableCapacityFluidTank fluidCoolantTank;
    @Shadow public IGasTank heatedCoolantTank;
    @Shadow public VariableHeatCapacitor heatCapacitor;
    @Shadow public long lastBoilRate;
    @Shadow public double lastBurnRate;
    @Shadow public double reactorDamage;
    @Shadow public abstract boolean isActive();

    @Unique
    private TileEntityMekanism overmek$ownerTile;

    @Inject(method = "<init>", at = @At("TAIL"))
    private void overmek$captureOwner(TileEntityFissionReactorCasing tile, CallbackInfo ci) {
        overmek$ownerTile = tile;
    }

    @Inject(method = "tick", at = @At("HEAD"))
    private void overmek$resyncOwner(Level world, CallbackInfoReturnable<Boolean> cir) {
        var multiblock = (MultiblockData) (Object) this;
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

    @Inject(method = "tick", at = @At(value = "INVOKE", target = "Lmekanism/generators/common/content/fission/FissionReactorMultiblockData;handleCoolant()V", shift = At.Shift.AFTER))
    private void overmek$enhanceFissionCooling(Level world, CallbackInfoReturnable<Boolean> cir) {
        if (overmek$ownerTile == null) {
            return;
        }

        boolean active = isActive() && lastBurnRate > 0;
        com.hewiegui.overmek.util.CircuitBoardOverclockHelper.tickWarmup(overmek$ownerTile, active);
        overmek$updateHeatCapacity();

        if (reactorDamage > 0) {
            double stability = CircuitBoardMultiblockHelper.getEffectiveFissionStabilityMultiplier(overmek$ownerTile);
            if (stability > 1.0D) {
                reactorDamage = Math.max(0.0D, reactorDamage - 0.0004D * (stability - 1.0D));
            }
        }

        if (!active || lastBoilRate <= 0) {
            return;
        }

        double efficiency = CircuitBoardMultiblockHelper.getEffectiveFissionEfficiencyMultiplier(overmek$ownerTile);
        long requestedExtraBoil = Math.max(0L, Math.round(lastBoilRate * (efficiency - 1.0D)));
        if (requestedExtraBoil <= 0) {
            return;
        }

        long insertedExtra = overmek$insertExtraHeatedCoolant(requestedExtraBoil);
        if (insertedExtra > 0) {
            lastBoilRate += insertedExtra;
            ((MultiblockData) (Object) this).markDirty();
        }
    }

    @Inject(method = "getActualBurnRate", at = @At("RETURN"), cancellable = true)
    private void overmek$boostDisplayedBurnRate(CallbackInfoReturnable<Double> cir) {
        if (overmek$ownerTile == null) {
            return;
        }
        double efficiency = CircuitBoardMultiblockHelper.getEffectiveFissionEfficiencyMultiplier(overmek$ownerTile);
        if (efficiency > 1.0D) {
            cir.setReturnValue(cir.getReturnValueD() * efficiency);
        }
    }

    @Inject(method = "getHeatingRate", at = @At("RETURN"), cancellable = true)
    private void overmek$boostDisplayedHeatingRate(CallbackInfoReturnable<Long> cir) {
        if (overmek$ownerTile == null) {
            return;
        }
        double efficiency = CircuitBoardMultiblockHelper.getEffectiveFissionEfficiencyMultiplier(overmek$ownerTile);
        if (efficiency > 1.0D) {
            cir.setReturnValue(Math.round(cir.getReturnValueJ() * efficiency));
        }
    }

    @Inject(method = "getCoolantCapacity", at = @At("RETURN"), cancellable = true)
    private void overmek$expandFissionCoolantCapacity(CallbackInfoReturnable<Long> cir) {
        if (overmek$ownerTile == null) {
            return;
        }
        double bufferMultiplier = CircuitBoardMultiblockHelper.getEffectiveBufferMultiplier(overmek$ownerTile);
        long base = cir.getReturnValueJ();
        long scaled = Math.round(base * bufferMultiplier);
        long stored = 0L;
        if (gasCoolantTank != null && !gasCoolantTank.isEmpty()) {
            stored = gasCoolantTank.getStored();
        } else if (fluidCoolantTank != null && !fluidCoolantTank.isEmpty()) {
            stored = fluidCoolantTank.getFluidAmount();
        }
        cir.setReturnValue(Math.max(stored, Math.max(base, scaled)));
    }

    private void overmek$updateHeatCapacity() {
        if (overmek$ownerTile == null || heatCapacitor == null) {
            return;
        }
        double stability = CircuitBoardMultiblockHelper.getEffectiveFissionStabilityMultiplier(overmek$ownerTile);
        double baseHeatCapacity = MekanismGeneratorsConfig.generators.fissionCasingHeatCapacity.get()
            * ((MultiblockData) (Object) this).locations.size();
        double targetCapacity = baseHeatCapacity * stability;
        double current = heatCapacitor.getHeatCapacity();
        if (Math.abs(current - targetCapacity) < 1.0E-6D) {
            return;
        }
        double currentHeat = heatCapacitor.getHeat();
        double scaledHeat = current > 0 ? currentHeat * (targetCapacity / current) : currentHeat;
        heatCapacitor.setHeatCapacity(targetCapacity, false);
        heatCapacitor.setHeat(scaledHeat);
    }

    private long overmek$insertExtraHeatedCoolant(long requestedExtraBoil) {
        if (!gasCoolantTank.isEmpty()) {
            GasStack coolantStack = gasCoolantTank.getStack();
            GasAttributes.CooledCoolant cooledCoolant = coolantStack.get(GasAttributes.CooledCoolant.class);
            if (cooledCoolant != null) {
                return overmek$insertHeatedCoolant(cooledCoolant.getHeatedGas().getStack(requestedExtraBoil), requestedExtraBoil);
            }
        }
        if (!fluidCoolantTank.isEmpty()) {
            return overmek$insertHeatedCoolant(MekanismGases.STEAM.getStack(requestedExtraBoil), requestedExtraBoil);
        }
        return 0L;
    }

    private long overmek$insertHeatedCoolant(GasStack stack, long requestedAmount) {
        GasStack remainder = heatedCoolantTank.insert(stack, Action.EXECUTE, AutomationType.INTERNAL);
        return Math.max(0L, requestedAmount - remainder.getAmount());
    }

    @Override
    public @Nullable TileEntityMekanism overmek$getOwnerTile() {
        return overmek$ownerTile;
    }
}
