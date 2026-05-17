package com.hewiegui.overmek.command;

import com.hewiegui.overmek.OverMek;
import com.hewiegui.overmek.capability.CircuitBoardHolder;
import com.hewiegui.overmek.util.BoardHostResolver;
import com.hewiegui.overmek.util.CircuitBoardOverclockHelper;
import com.hewiegui.overmek.util.OverMekDebug;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.Set;
import mekanism.common.tile.base.TileEntityMekanism;
import net.minecraft.commands.arguments.coordinates.BlockPosArgument;
import net.minecraft.core.BlockPos;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = OverMek.MODID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public final class OverMekCommands {

    private OverMekCommands() {
    }

    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        event.getDispatcher().register(
            Commands.literal("overmek")
                .requires(source -> source.hasPermission(2))
                .then(Commands.literal("warmup")
                    .then(Commands.literal("all")
                        .executes(context -> warmupAll(context.getSource()))))
                .then(Commands.literal("debug")
                    .then(Commands.literal("tile")
                        .then(Commands.argument("pos", BlockPosArgument.blockPos())
                            .executes(context -> debugTile(context.getSource(), BlockPosArgument.getLoadedBlockPos(context, "pos"))))))
        );
    }

    private static int warmupAll(CommandSourceStack source) {
        int warmed = 0;
        int skipped = 0;
        Set<BlockEntity> visitedHosts = Collections.newSetFromMap(new IdentityHashMap<>());

        for (CircuitBoardHolder holder : CircuitBoardHolder.getTrackedHoldersSnapshot()) {
            BlockEntity blockEntity = holder.getBlockEntity();
            if (!isLoadedServerTile(blockEntity)) {
                skipped++;
                continue;
            }
            BlockEntity host = BoardHostResolver.resolveHost(blockEntity);
            if (!isLoadedServerTile(host) || !visitedHosts.add(host)) {
                continue;
            }
            if (host instanceof TileEntityMekanism tile && CircuitBoardOverclockHelper.completeWarmup(tile)) {
                warmed++;
            } else {
                skipped++;
            }
        }

        int finalWarmed = warmed;
        int finalSkipped = skipped;
        source.sendSuccess(
            () -> Component.literal("OverMek warmed up " + finalWarmed + " loaded machine(s), skipped " + finalSkipped + "."),
            true
        );
        return warmed;
    }

    private static int debugTile(CommandSourceStack source, BlockPos pos) {
        BlockEntity blockEntity = source.getLevel().getBlockEntity(pos);
        var lines = OverMekDebug.diagnose(blockEntity);
        for (String line : lines) {
            source.sendSuccess(() -> Component.literal(line), false);
        }
        return lines.size();
    }

    private static boolean isLoadedServerTile(BlockEntity blockEntity) {
        if (blockEntity == null || blockEntity.isRemoved()) {
            return false;
        }
        Level level = blockEntity.getLevel();
        return level != null && !level.isClientSide;
    }
}
