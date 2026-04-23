package com.hewiegui.overmek.item;

import com.hewiegui.overmek.util.CircuitBoardOverclockHelper;
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

    private final int tier;
    private final int overclockCount;

    public CircuitBoardItem(int tier, int overclockCount) {
        super(new Item.Properties().stacksTo(1));
        this.tier = tier;
        this.overclockCount = overclockCount;
    }

    public int getTier() {
        return tier;
    }

    public int getOverclockCount() {
        return overclockCount;
    }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level, List<Component> tooltips, TooltipFlag flag) {
        double baseSpeedMultiplier = CircuitBoardOverclockHelper.getBoardSpeedMultiplier(tier, overclockCount, false);
        double energyProfile = CircuitBoardOverclockHelper.getBoardEnergyUsageMultiplier(tier, overclockCount, false);
        double factoryProfile = CircuitBoardOverclockHelper.getBoardSpeedMultiplier(tier, overclockCount, true);

        tooltips.add(Component.translatable(getRoleTranslationKey()));
        tooltips.add(Component.translatable(getEffectTranslationKey()));
        tooltips.add(Component.translatable("tooltip.overmek.overclock_count", overclockCount));
        tooltips.add(Component.translatable("tooltip.overmek.base_speed_multiplier", OVERMEK_DECIMAL.format(baseSpeedMultiplier)));
        tooltips.add(Component.translatable("tooltip.overmek.energy_usage_profile", OVERMEK_DECIMAL.format(energyProfile)));
        if (CircuitBoardOverclockHelper.hasFactorySpecialization(tier) && factoryProfile > baseSpeedMultiplier) {
            tooltips.add(Component.translatable("tooltip.overmek.factory_speed_profile", OVERMEK_DECIMAL.format(factoryProfile)));
        }
    }

    private String getRoleTranslationKey() {
        return switch (tier) {
            case 0 -> "tooltip.overmek.board_role_basic";
            case 1 -> "tooltip.overmek.board_role_advanced";
            case 2 -> "tooltip.overmek.board_role_elite";
            case 3 -> "tooltip.overmek.board_role_ultimate";
            default -> "tooltip.overmek.board_role_basic";
        };
    }

    private String getEffectTranslationKey() {
        return switch (tier) {
            case 0 -> "tooltip.overmek.board_effect_basic";
            case 1 -> "tooltip.overmek.board_effect_advanced";
            case 2 -> "tooltip.overmek.board_effect_elite";
            case 3 -> "tooltip.overmek.board_effect_ultimate";
            default -> "tooltip.overmek.board_effect_basic";
        };
    }
}
