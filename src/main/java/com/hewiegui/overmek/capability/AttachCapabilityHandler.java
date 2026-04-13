package com.hewiegui.overmek.capability;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "overmek", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AttachCapabilityHandler {

    private static final ResourceLocation CIRCUIT_BOARD_CAP =
        ResourceLocation.fromNamespaceAndPath("overmek", "circuit_board");

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<BlockEntity> event) {
        BlockEntity be = event.getObject();

        // 加这行
        System.out.println("[OverMek] AttachCap called for: " + be.getClass().getName());

        if (be.getClass().getName().startsWith("mekanism.common.tile")) {
            System.out.println("[OverMek] Attaching circuit board cap to: " + be.getClass().getName());
            event.addCapability(CIRCUIT_BOARD_CAP, new CircuitBoardHolder(be));
        }
    }
}
