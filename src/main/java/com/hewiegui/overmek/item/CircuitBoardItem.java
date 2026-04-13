package com.hewiegui.overmek.item;

import net.minecraft.network.chat.Component;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class CircuitBoardItem extends Item {

    private final int tier;          // 0=基础 1=进阶 2=精英 3=终极
    private final int overclockCount; // 超频次数

    public CircuitBoardItem(int tier, int overclockCount) {
        super(new Item.Properties().stacksTo(1));
        this.tier = tier;
        this.overclockCount = overclockCount;
    }

    public int getTier() { return tier; }
    public int getOverclockCount() { return overclockCount; }

    @Override
    public void appendHoverText(ItemStack stack, @Nullable Level level,
                                List<Component> tooltips, TooltipFlag flag) {
        tooltips.add(Component.translatable("tooltip.overmek.overclock_count", overclockCount));
        if (tier == 3) {
            tooltips.add(Component.translatable("tooltip.overmek.parallel_hint"));
        }
    }
}
