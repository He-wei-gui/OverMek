package com.hewiegui.overmek.capability;

import com.hewiegui.overmek.util.BoardHostResolver;
import com.hewiegui.overmek.util.OverMekLog;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.Containers;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.level.BlockEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "overmek", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class AttachCapabilityHandler {

    private static final ResourceLocation CIRCUIT_BOARD_CAP =
        ResourceLocation.fromNamespaceAndPath("overmek", "circuit_board");
    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<BlockEntity> event) {
        BlockEntity be = event.getObject();
        if (be.getClass().getName().startsWith("mekanism.common.tile")
            || be.getClass().getName().startsWith("mekanism.generators.common.tile")) {
            OverMekLog.debug("OverMek attaching circuit board capability to {}", be.getClass().getName());
            event.addCapability(CIRCUIT_BOARD_CAP, new CircuitBoardHolder(be));
        }
    }

    @SubscribeEvent(priority = EventPriority.LOWEST)
    public static void onBlockBreak(BlockEvent.BreakEvent event) {
        if (event.isCanceled() || event.getPlayer().isCreative()) {
            return;
        }

        Level level = event.getPlayer().level();
        if (level.isClientSide()) {
            return;
        }

        BlockEntity be = level.getBlockEntity(event.getPos());
        if (be == null || !(be.getClass().getName().startsWith("mekanism.common.tile")
            || be.getClass().getName().startsWith("mekanism.generators.common.tile"))) {
            return;
        }

        BlockEntity host = BoardHostResolver.resolveHost(be);
        var holder = BoardHostResolver.resolveHolder(be);
        if (holder == null || !holder.hasCircuitBoard()) {
            return;
        }

        var stack = holder.getCircuitBoard();
        holder.setCircuitBoard(net.minecraft.world.item.ItemStack.EMPTY);
        Containers.dropItemStack(level, event.getPos().getX(), event.getPos().getY(), event.getPos().getZ(), stack);
        if (host != be) {
            host.setChanged();
        }
    }
}
