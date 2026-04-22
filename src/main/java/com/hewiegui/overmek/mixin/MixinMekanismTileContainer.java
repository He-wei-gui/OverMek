package com.hewiegui.overmek.mixin;

import com.hewiegui.overmek.capability.CircuitBoardHolder;
import com.hewiegui.overmek.inventory.CircuitBoardInventorySlot;
import com.mojang.logging.LogUtils;
import mekanism.common.inventory.container.tile.MekanismTileContainer;
import mekanism.common.tile.factory.TileEntityFactory;
import mekanism.common.tier.FactoryTier;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.slf4j.Logger;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = MekanismTileContainer.class, remap = false)
public abstract class MixinMekanismTileContainer extends AbstractContainerMenu {

    private static final int OVERMEK_DEFAULT_GUI_WIDTH = 176;
    private static final int OVERMEK_ULTIMATE_FACTORY_GUI_WIDTH = 210;
    private static final int OVERMEK_EXTERNAL_SLOT_X_OFFSET = 1;
    private static final int OVERMEK_EXTERNAL_SLOT_Y = 34;
    private static final Logger overmek$logger = LogUtils.getLogger();

    protected MixinMekanismTileContainer() {
        super(null, 0);
    }

    @Inject(method = "addSlots", at = @At("TAIL"))
    private void addCircuitBoardSlot(CallbackInfo ci) {
        MekanismTileContainer<?> self = (MekanismTileContainer<?>) (Object) this;
        BlockEntity be = self.getTileEntity();
        if (be == null) {
            overmek$logger.debug("OverMek skipped circuit board slot because tile entity is null for container {}", self.getClass().getName());
            return;
        }
        boolean mekanismTile = be.getClass().getName().startsWith("mekanism.common.tile");
        boolean supportsUpgrades = self.getTileEntity().supportsUpgrades();
        if (!mekanismTile || !supportsUpgrades) {
            overmek$logger.debug(
                "OverMek skipped circuit board slot for {}. mekanismTile={}, supportsUpgrades={}",
                be.getClass().getName(),
                mekanismTile,
                supportsUpgrades
            );
            return;
        }

        var holder = be.getCapability(CircuitBoardHolder.CIRCUIT_BOARD_CAPABILITY).resolve().orElse(null);
        if (holder == null) {
            overmek$logger.debug(
                "OverMek could not resolve circuit board capability for {}. registered={}",
                be.getClass().getName(),
                CircuitBoardHolder.CIRCUIT_BOARD_CAPABILITY.isRegistered()
            );
            return;
        }

        int slotX = overmek$getCircuitBoardSlotX(be);
        int slotY = overmek$getCircuitBoardSlotY(be);
        CircuitBoardInventorySlot inventorySlot = new CircuitBoardInventorySlot(holder, be, slotX, slotY);
        var containerSlot = inventorySlot.createContainerSlot();
        if (containerSlot == null) {
            overmek$logger.debug("OverMek failed to create a container slot for {}", be.getClass().getName());
            return;
        }
        addSlot(containerSlot);
        overmek$logger.debug(
            "OverMek added circuit board slot to container {} for {}. slotIndex={}, totalSlots={}, pos=({}, {}), layout=external_right",
            self.getClass().getName(),
            be.getClass().getName(),
            containerSlot.index,
            slots.size(),
            slotX,
            slotY
        );
    }

    private static int overmek$getCircuitBoardSlotX(BlockEntity be) {
        return overmek$getGuiWidth(be) + OVERMEK_EXTERNAL_SLOT_X_OFFSET;
    }

    private static int overmek$getCircuitBoardSlotY(BlockEntity be) {
        return OVERMEK_EXTERNAL_SLOT_Y;
    }

    private static int overmek$getGuiWidth(BlockEntity be) {
        if (be instanceof TileEntityFactory<?> factory && factory.tier == FactoryTier.ULTIMATE) {
            return OVERMEK_ULTIMATE_FACTORY_GUI_WIDTH;
        }
        return OVERMEK_DEFAULT_GUI_WIDTH;
    }
}
