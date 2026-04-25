package com.hewiegui.overmek.registry;

import com.hewiegui.overmek.OverMek;
import com.hewiegui.overmek.item.CircuitBoardItem;
import com.hewiegui.overmek.util.CircuitBoardChannel;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.RegistryObject;

public class ModItems {

    public static final DeferredRegister<Item> ITEMS =
        DeferredRegister.create(ForgeRegistries.ITEMS, OverMek.MODID);

    public static final RegistryObject<CircuitBoardItem> BASIC_CIRCUIT_BOARD =
        ITEMS.register("basic_circuit_board",
            () -> new CircuitBoardItem(CircuitBoardChannel.STANDARD, 0, 1));

    public static final RegistryObject<CircuitBoardItem> ADVANCED_CIRCUIT_BOARD =
        ITEMS.register("advanced_circuit_board",
            () -> new CircuitBoardItem(CircuitBoardChannel.STANDARD, 1, 2));

    public static final RegistryObject<CircuitBoardItem> ELITE_CIRCUIT_BOARD =
        ITEMS.register("elite_circuit_board",
            () -> new CircuitBoardItem(CircuitBoardChannel.STANDARD, 2, 3));

    public static final RegistryObject<CircuitBoardItem> ULTIMATE_CIRCUIT_BOARD =
        ITEMS.register("ultimate_circuit_board",
            () -> new CircuitBoardItem(CircuitBoardChannel.STANDARD, 3, 4));

    public static final RegistryObject<CircuitBoardItem> FISSION_REACTOR_CIRCUIT_BOARD =
        ITEMS.register("fission_reactor_circuit_board",
            () -> new CircuitBoardItem(CircuitBoardChannel.FISSION, 0, 0));

    public static final RegistryObject<CircuitBoardItem> POWER_MULTIBLOCK_CIRCUIT_BOARD =
        ITEMS.register("power_multiblock_circuit_board",
            () -> new CircuitBoardItem(CircuitBoardChannel.POWER_MULTIBLOCK, 0, 0));

    public static final RegistryObject<CircuitBoardItem> EVAPORATION_TOWER_CIRCUIT_BOARD =
        ITEMS.register("evaporation_tower_circuit_board",
            () -> new CircuitBoardItem(CircuitBoardChannel.EVAPORATION_MULTIBLOCK, 0, 0));

    public static final RegistryObject<CircuitBoardItem> SPS_CIRCUIT_BOARD =
        ITEMS.register("sps_circuit_board",
            () -> new CircuitBoardItem(CircuitBoardChannel.SPS_MULTIBLOCK, 0, 0));
}
