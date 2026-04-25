package com.hewiegui.overmek.mixin.client;

import com.hewiegui.overmek.util.CircuitBoardOverclockHelper;
import com.hewiegui.overmek.util.BoardSlotDisplayState;
import com.hewiegui.overmek.util.BoardTooltipCategory;
import com.hewiegui.overmek.inventory.CircuitBoardContainerSlot;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import mekanism.client.gui.GuiMekanism;
import mekanism.client.gui.GuiMekanismTile;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.tile.base.TileEntityMekanism;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = GuiMekanismTile.class, remap = false)
public abstract class MixinGuiMekanismTile<TILE extends TileEntityMekanism, CONTAINER extends MekanismTileContainer<TILE>> extends GuiMekanism<CONTAINER> {

    private static final DecimalFormat OVERMEK_DECIMAL = new DecimalFormat("0.##");

    @Shadow
    @Final
    protected TILE tile;

    protected MixinGuiMekanismTile(CONTAINER container, Inventory inv, Component title) {
        super(container, inv, title);
    }

    @Inject(method = "renderTooltip", at = @At("HEAD"), cancellable = true)
    private void overmek$renderCircuitBoardTooltip(GuiGraphics guiGraphics, int mouseX, int mouseY, CallbackInfo ci) {
        Slot hoveredSlot = getSlotUnderMouse();
        if (!(hoveredSlot instanceof CircuitBoardContainerSlot)) {
            return;
        }

        ItemStack stack = hoveredSlot.getItem();
        if (stack.isEmpty()) {
            displayTooltips(guiGraphics, mouseX, mouseY, overmek$getEmptyTooltip());
        } else {
            List<Component> tooltip = new ArrayList<>(Screen.getTooltipFromItem(Minecraft.getInstance(), stack));
            tooltip.addAll(overmek$getStatusLines(stack));
            guiGraphics.renderTooltip(font, tooltip, stack.getTooltipImage(), stack, mouseX, mouseY);
        }
        ci.cancel();
    }

    private List<Component> overmek$getEmptyTooltip() {
        List<Component> tooltip = new ArrayList<>();
        tooltip.add(Component.translatable("tooltip.overmek.circuit_board_slot"));
        tooltip.add(Component.translatable("tooltip.overmek.no_circuit_board"));
        return tooltip;
    }

    private List<Component> overmek$getStatusLines(ItemStack stack) {
        List<Component> lines = new ArrayList<>();
        if (!CircuitBoardOverclockHelper.canApplyCircuitBoardEffects(tile)) {
            lines.add(Component.translatable("tooltip.overmek.machine_disabled"));
            return lines;
        }
        BoardSlotDisplayState displayState = CircuitBoardOverclockHelper.getDisplayState(tile, stack);
        if (!displayState.compatibleBoard()) {
            lines.add(Component.translatable("tooltip.overmek.board_incompatible"));
            return lines;
        }
        switch (displayState.supportProfile().tooltipCategory()) {
            case FISSION -> {
                lines.add(Component.translatable("tooltip.overmek.current_cooling_multiplier", OVERMEK_DECIMAL.format(displayState.coolingMultiplier())));
                lines.add(Component.translatable("tooltip.overmek.current_stability_multiplier", OVERMEK_DECIMAL.format(displayState.stabilityMultiplier())));
                lines.add(Component.translatable("tooltip.overmek.current_capacity_multiplier", OVERMEK_DECIMAL.format(displayState.capacityMultiplier())));
            }
            case POWER_MULTIBLOCK -> {
                if (displayState.matrixCapacityMultiplier() > 1.0D) {
                    lines.add(Component.translatable("tooltip.overmek.current_matrix_capacity_multiplier", OVERMEK_DECIMAL.format(displayState.matrixCapacityMultiplier())));
                    lines.add(Component.translatable("tooltip.overmek.current_matrix_transfer_multiplier", OVERMEK_DECIMAL.format(displayState.matrixTransferMultiplier())));
                } else {
                    lines.add(Component.translatable("tooltip.overmek.current_generation_multiplier", OVERMEK_DECIMAL.format(displayState.generationMultiplier())));
                    lines.add(Component.translatable("tooltip.overmek.current_fuel_multiplier", OVERMEK_DECIMAL.format(displayState.fuelMultiplier())));
                    lines.add(Component.translatable("tooltip.overmek.current_capacity_multiplier", OVERMEK_DECIMAL.format(displayState.capacityMultiplier())));
                }
            }
            case EVAPORATION_MULTIBLOCK -> {
                lines.add(Component.translatable("tooltip.overmek.current_throughput_multiplier", OVERMEK_DECIMAL.format(displayState.throughputMultiplier())));
                lines.add(Component.translatable("tooltip.overmek.current_capacity_multiplier", OVERMEK_DECIMAL.format(displayState.capacityMultiplier())));
            }
            case SPS_MULTIBLOCK -> {
                lines.add(Component.translatable("tooltip.overmek.current_throughput_multiplier", OVERMEK_DECIMAL.format(displayState.throughputMultiplier())));
                lines.add(Component.translatable("tooltip.overmek.current_stability_multiplier", OVERMEK_DECIMAL.format(displayState.stabilityMultiplier())));
                lines.add(Component.translatable("tooltip.overmek.current_capacity_multiplier", OVERMEK_DECIMAL.format(displayState.capacityMultiplier())));
                lines.add(Component.translatable("tooltip.overmek.current_pressure_multiplier", OVERMEK_DECIMAL.format(displayState.pressureMultiplier())));
                lines.add(Component.translatable("tooltip.overmek.current_energy_multiplier", OVERMEK_DECIMAL.format(displayState.energyMultiplier())));
            }
            case GENERATOR -> {
                lines.add(Component.translatable("tooltip.overmek.current_generation_multiplier", OVERMEK_DECIMAL.format(displayState.generationMultiplier())));
                if (displayState.fuelMultiplier() > 1.0D) {
                    lines.add(Component.translatable("tooltip.overmek.current_fuel_multiplier", OVERMEK_DECIMAL.format(displayState.fuelMultiplier())));
                }
                lines.add(Component.translatable("tooltip.overmek.current_capacity_multiplier", OVERMEK_DECIMAL.format(displayState.capacityMultiplier())));
            }
            case PROCESSING -> {
                lines.add(Component.translatable("tooltip.overmek.current_speed_multiplier", OVERMEK_DECIMAL.format(displayState.speedMultiplier())));
                lines.add(Component.translatable("tooltip.overmek.current_overclock_bonus", OVERMEK_DECIMAL.format(displayState.overclockBonus())));
                lines.add(Component.translatable("tooltip.overmek.current_energy_multiplier", OVERMEK_DECIMAL.format(displayState.energyMultiplier())));
                if (displayState.ticksRequired() > 0) {
                    lines.add(Component.translatable("tooltip.overmek.current_ticks_required", displayState.ticksRequired()));
                }
            }
            case UNSUPPORTED -> {
                return lines;
            }
        }
        lines.add(Component.translatable("tooltip.overmek.current_warmup", OVERMEK_DECIMAL.format(displayState.warmupRatio() * 100.0D)));
        return lines;
    }
}
