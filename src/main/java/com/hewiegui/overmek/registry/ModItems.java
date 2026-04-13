package com.hewiegui.overmek.registry;

import com.hewiegui.overmek.OverMek;
import com.hewiegui.overmek.item.CircuitBoardItem;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
        DeferredRegister.create(ForgeRegistries.ITEMS, OverMek.MODID);

    public static final RegistryObject<CircuitBoardItem> BASIC_CIRCUIT_BOARD =
        ITEMS.register("basic_circuit_board",
            () -> new CircuitBoardItem(0, 1));

    public static final RegistryObject<CircuitBoardItem> ADVANCED_CIRCUIT_BOARD =
        ITEMS.register("advanced_circuit_board",
            () -> new CircuitBoardItem(1, 2));

    public static final RegistryObject<CircuitBoardItem> ELITE_CIRCUIT_BOARD =
        ITEMS.register("elite_circuit_board",
            () -> new CircuitBoardItem(2, 3));

    public static final RegistryObject<CircuitBoardItem> ULTIMATE_CIRCUIT_BOARD =
        ITEMS.register("ultimate_circuit_board",
            () -> new CircuitBoardItem(3, 4));
}
