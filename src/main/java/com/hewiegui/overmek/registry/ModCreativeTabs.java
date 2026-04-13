package com.hewiegui.overmek.registry;

import com.hewiegui.overmek.OverMek;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;
import net.minecraft.core.registries.Registries;

public class ModCreativeTabs {

    public static final DeferredRegister<CreativeModeTab> CREATIVE_TABS =
        DeferredRegister.create(Registries.CREATIVE_MODE_TAB, OverMek.MODID);

    public static final RegistryObject<CreativeModeTab> OVERMEK_TAB =
        CREATIVE_TABS.register("overmek_tab", () ->
            CreativeModeTab.builder()
                .title(Component.translatable("itemGroup.overmek"))
                .icon(() -> new ItemStack(ModItems.ULTIMATE_CIRCUIT_BOARD.get()))
                .displayItems((params, output) -> {
                    output.accept(ModItems.BASIC_CIRCUIT_BOARD.get());
                    output.accept(ModItems.ADVANCED_CIRCUIT_BOARD.get());
                    output.accept(ModItems.ELITE_CIRCUIT_BOARD.get());
                    output.accept(ModItems.ULTIMATE_CIRCUIT_BOARD.get());
                })
                .build()
        );
}
