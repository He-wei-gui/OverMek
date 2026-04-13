package com.hewiegui.overmek;

import com.hewiegui.overmek.registry.ModCreativeTabs;
import com.hewiegui.overmek.registry.ModItems;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

@Mod(OverMek.MODID)
public class OverMek {

    public static final String MODID = "overmek";

    public OverMek(FMLJavaModLoadingContext context) {
        var bus = context.getModEventBus();
        ModItems.ITEMS.register(bus);
        ModCreativeTabs.CREATIVE_TABS.register(bus);
    }
}
