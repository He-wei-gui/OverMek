package com.hewiegui.overmek.capability;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "overmek", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AttachCapabilityHandler {

    private static final ResourceLocation CIRCUIT_BOARD_CAP =
        new ResourceLocation("overmek", "circuit_board");

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<BlockEntity> event) {
        BlockEntity be = event.getObject();

        // 只给 Mekanism 的机器附加
        if (be.getClass().getName().startsWith("mekanism.common.tile")) {
            event.addCapability(CIRCUIT_BOARD_CAP, new CircuitBoardHolder());
        }
    }
}
