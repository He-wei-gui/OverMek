package com.hewiegui.overmek.item;

import com.hewiegui.overmek.util.CircuitBoardChannel;
import com.hewiegui.overmek.util.CircuitBoardMachineProfile;
import com.hewiegui.overmek.util.CircuitBoardOverclockHelper;
import com.hewiegui.overmek.util.BoardEffectProfile;
import com.hewiegui.overmek.util.BoardProfileLoader;
import java.text.DecimalFormat;
import java.util.List;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

public class CircuitBoardItem extends Item {

    private static final DecimalFormat OVERMEK_DECIMAL = new DecimalFormat("0.##");

    private final CircuitBoardChannel channel;
    private final int tier;
    private final int overclockCount;

    public CircuitBoardItem(CircuitBoardChannel channel, int tier, int overclockCount) {
        super(new Item.Properties().stacksTo(1));
        this.channel = channel;
        this.tier = tier;
        this.overclockCount = overclockCount;
    }

    public CircuitBoardChannel getChannel() {
        return channel;
    }

    public int getTier() {
        return tier;
    }

    public int getOverclockCount() {
        return overclockCount;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltips, TooltipFlag flag) {
        if (channel != CircuitBoardChannel.STANDARD) {
            appendMultiblockHoverText(tooltips);
            return;
        }
        BoardEffectProfile profile = BoardProfileLoader.getProcessingProfile(tier);
        double baseSpeedMultiplier = profile.getSpeedMultiplier(overclockCount, false, com.hewiegui.overmek.config.OverMekConfig.getMaxOverclockBonus(), 1.0D);
        double energyProfile = profile.getEnergyUsageMultiplier(overclockCount, false, com.hewiegui.overmek.config.OverMekConfig.getMaxOverclockBonus(), 1.0D, com.hewiegui.overmek.config.OverMekConfig.getOverclockEnergyMultiplier());
        double factoryProfile = profile.getSpeedMultiplier(overclockCount, true, com.hewiegui.overmek.config.OverMekConfig.getMaxOverclockBonus(), 1.0D);
        double energyCapacityProfile = profile.energyCapacityFactor();

        tooltips.add(Component.translatable(getRoleTranslationKey()));
        tooltips.add(Component.translatable(getEffectTranslationKey()));
        tooltips.add(Component.translatable("tooltip.overmek.overclock_count", overclockCount));
        tooltips.add(Component.translatable("tooltip.overmek.base_speed_multiplier", OVERMEK_DECIMAL.format(baseSpeedMultiplier)));
        tooltips.add(Component.translatable("tooltip.overmek.energy_usage_profile", OVERMEK_DECIMAL.format(energyProfile)));
        tooltips.add(Component.translatable("tooltip.overmek.energy_capacity_profile", OVERMEK_DECIMAL.format(energyCapacityProfile)));
        tooltips.add(Component.translatable("tooltip.overmek.warmup_profile", profile.warmupTicks()));
        if (CircuitBoardOverclockHelper.hasFactorySpecialization(tier) && factoryProfile > baseSpeedMultiplier) {
            tooltips.add(Component.translatable("tooltip.overmek.factory_speed_profile", OVERMEK_DECIMAL.format(factoryProfile)));
        }
    }

    private void appendMultiblockHoverText(List<Component> tooltips) {
        BoardEffectProfile profile = switch (channel) {
            case FISSION -> BoardProfileLoader.getMultiblockProfile(CircuitBoardMachineProfile.FISSION);
            case POWER_MULTIBLOCK -> BoardProfileLoader.getMultiblockProfile(CircuitBoardMachineProfile.POWER_MULTIBLOCK);
            case EVAPORATION_MULTIBLOCK -> BoardProfileLoader.getMultiblockProfile(CircuitBoardMachineProfile.EVAPORATION_MULTIBLOCK);
            case SPS_MULTIBLOCK -> BoardProfileLoader.getMultiblockProfile(CircuitBoardMachineProfile.SPS_MULTIBLOCK);
            case STANDARD -> BoardEffectProfile.unsupported();
        };
        tooltips.add(Component.translatable(getRoleTranslationKey()));
        tooltips.add(Component.translatable(getEffectTranslationKey()));
        switch (channel) {
            case FISSION -> {
                tooltips.add(Component.translatable("tooltip.overmek.fission_efficiency_profile", OVERMEK_DECIMAL.format(profile.speedMultiplier())));
                tooltips.add(Component.translatable("tooltip.overmek.fission_stability_profile", OVERMEK_DECIMAL.format(profile.stabilityMultiplier())));
                tooltips.add(Component.translatable("tooltip.overmek.multiblock_buffer_profile", OVERMEK_DECIMAL.format(profile.bufferMultiplier())));
                tooltips.add(Component.translatable("tooltip.overmek.warmup_profile", profile.warmupTicks()));
            }
            case POWER_MULTIBLOCK -> {
                tooltips.add(Component.translatable("tooltip.overmek.power_generation_profile", OVERMEK_DECIMAL.format(profile.generationMultiplier())));
                tooltips.add(Component.translatable("tooltip.overmek.power_fuel_profile", OVERMEK_DECIMAL.format(profile.fuelMultiplier())));
                tooltips.add(Component.translatable("tooltip.overmek.multiblock_buffer_profile", OVERMEK_DECIMAL.format(profile.bufferMultiplier())));
                tooltips.add(Component.translatable("tooltip.overmek.matrix_capacity_profile", OVERMEK_DECIMAL.format(profile.matrixCapacityMultiplier())));
                tooltips.add(Component.translatable("tooltip.overmek.matrix_transfer_profile", OVERMEK_DECIMAL.format(profile.matrixTransferMultiplier())));
                tooltips.add(Component.translatable("tooltip.overmek.warmup_profile", profile.warmupTicks()));
            }
            case EVAPORATION_MULTIBLOCK -> {
                tooltips.add(Component.translatable("tooltip.overmek.evaporation_throughput_profile", OVERMEK_DECIMAL.format(profile.throughputMultiplier())));
                tooltips.add(Component.translatable("tooltip.overmek.multiblock_buffer_profile", OVERMEK_DECIMAL.format(profile.bufferMultiplier())));
                tooltips.add(Component.translatable("tooltip.overmek.warmup_profile", profile.warmupTicks()));
            }
            case SPS_MULTIBLOCK -> {
                tooltips.add(Component.translatable("tooltip.overmek.sps_throughput_profile", OVERMEK_DECIMAL.format(profile.throughputMultiplier())));
                tooltips.add(Component.translatable("tooltip.overmek.sps_stability_profile", OVERMEK_DECIMAL.format(profile.stabilityMultiplier())));
                tooltips.add(Component.translatable("tooltip.overmek.multiblock_buffer_profile", OVERMEK_DECIMAL.format(profile.bufferMultiplier())));
                tooltips.add(Component.translatable("tooltip.overmek.sps_pressure_profile", OVERMEK_DECIMAL.format(profile.pressureMultiplier())));
                tooltips.add(Component.translatable("tooltip.overmek.sps_energy_profile", OVERMEK_DECIMAL.format(profile.energyUsageFactor())));
                tooltips.add(Component.translatable("tooltip.overmek.warmup_profile", profile.warmupTicks()));
            }
            default -> {
            }
        }
    }

    private String getRoleTranslationKey() {
        if (channel != CircuitBoardChannel.STANDARD) {
            return switch (channel) {
                case FISSION -> "tooltip.overmek.board_role_fission";
                case POWER_MULTIBLOCK -> "tooltip.overmek.board_role_power_multiblock";
                case EVAPORATION_MULTIBLOCK -> "tooltip.overmek.board_role_evaporation";
                case SPS_MULTIBLOCK -> "tooltip.overmek.board_role_sps";
                default -> "tooltip.overmek.board_role_basic";
            };
        }
        return switch (tier) {
            case 0 -> "tooltip.overmek.board_role_basic";
            case 1 -> "tooltip.overmek.board_role_advanced";
            case 2 -> "tooltip.overmek.board_role_elite";
            case 3 -> "tooltip.overmek.board_role_ultimate";
            default -> "tooltip.overmek.board_role_basic";
        };
    }

    private String getEffectTranslationKey() {
        if (channel != CircuitBoardChannel.STANDARD) {
            return switch (channel) {
                case FISSION -> "tooltip.overmek.board_effect_fission";
                case POWER_MULTIBLOCK -> "tooltip.overmek.board_effect_power_multiblock";
                case EVAPORATION_MULTIBLOCK -> "tooltip.overmek.board_effect_evaporation";
                case SPS_MULTIBLOCK -> "tooltip.overmek.board_effect_sps";
                default -> "tooltip.overmek.board_effect_basic";
            };
        }
        return switch (tier) {
            case 0 -> "tooltip.overmek.board_effect_basic";
            case 1 -> "tooltip.overmek.board_effect_advanced";
            case 2 -> "tooltip.overmek.board_effect_elite";
            case 3 -> "tooltip.overmek.board_effect_ultimate";
            default -> "tooltip.overmek.board_effect_basic";
        };
    }
}
