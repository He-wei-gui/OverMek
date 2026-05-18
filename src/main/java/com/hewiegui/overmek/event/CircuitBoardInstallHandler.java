package com.hewiegui.overmek.event;

import com.hewiegui.overmek.capability.ICircuitBoardHolder;
import com.hewiegui.overmek.item.CircuitBoardItem;
import com.hewiegui.overmek.util.BoardHostResolver;
import com.hewiegui.overmek.util.CircuitBoardProfileHelper;
import com.hewiegui.overmek.util.JerryAddonCompat;
import com.hewiegui.overmek.util.OverMekDebug;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.entity.player.PlayerInteractEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = "overmek", bus = Mod.EventBusSubscriber.Bus.FORGE)
public class CircuitBoardInstallHandler {

    @SubscribeEvent
    public static void onRightClickBlock(PlayerInteractEvent.RightClickBlock event) {
        if (event.getLevel().isClientSide()) {
            return;
        }

        var player = event.getEntity();
        if (!player.isCrouching()) {
            return;
        }

        Inventory inv = player.getInventory();
        int slot = inv.selected;
        ItemStack stack = inv.getItem(slot);
        if (stack.isEmpty() || !(stack.getItem() instanceof CircuitBoardItem)) {
            return;
        }

        BlockEntity be = event.getLevel().getBlockEntity(event.getPos());
        if (be == null) {
            return;
        }

        String className = be.getClass().getName();
        if (!className.startsWith("mekanism.common.tile") && !className.startsWith("mekanism.generators.common.tile") && !JerryAddonCompat.isJerryAddonTile(be)) {
            return;
        }

        boolean acceptsBoard = CircuitBoardProfileHelper.acceptsBoard(be, stack);
        BlockEntity host = BoardHostResolver.resolveHost(be);
        OverMekDebug.logInstallAttempt(be, host, acceptsBoard, null, stack, "clicked");
        if (!acceptsBoard) {
            return;
        }

        ICircuitBoardHolder holder = BoardHostResolver.resolveHolder(be);
        OverMekDebug.logInstallAttempt(be, host, true, holder, stack, "resolved-holder");
        if (holder == null) {
            return;
        }

        if (holder.hasCircuitBoard()) {
            OverMekDebug.logInstallAttempt(be, host, true, holder, stack, "skipped-existing-board");
            return;
        }

        ItemStack newBoard = stack.copy();
        newBoard.setCount(1);
        holder.setCircuitBoard(newBoard);
        OverMekDebug.logInstallAttempt(be, host, true, holder, newBoard, "installed");

        stack.shrink(1);
        inv.setChanged();

        be.setChanged();
        player.sendSystemMessage(Component.translatable("message.overmek.circuit_board_installed"));
        event.setCanceled(true);
    }
}
