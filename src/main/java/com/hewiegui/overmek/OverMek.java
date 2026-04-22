package com.hewiegui.overmek;

import com.hewiegui.overmek.config.OverMekConfig;
import com.hewiegui.overmek.registry.ModCreativeTabs;
import com.hewiegui.overmek.registry.ModItems;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.config.ModConfig;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(OverMek.MODID)
public class OverMek {

    public static final String MODID = "overmek";

    public OverMek(FMLJavaModLoadingContext context) {
        var bus = context.getModEventBus();
        ModLoadingContext.get().registerConfig(ModConfig.Type.COMMON, OverMekConfig.SPEC, "overmek-common.toml");
        ModItems.ITEMS.register(bus);
        ModCreativeTabs.CREATIVE_TABS.register(bus);
    }
}
