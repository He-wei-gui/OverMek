package com.hewiegui.overmek.util;

import com.hewiegui.overmek.config.OverMekConfig;
import java.util.List;
import java.util.regex.Pattern;
import mekanism.common.tile.base.TileEntityMekanism;
import mekanism.common.tile.factory.TileEntityFactory;
import mekanism.common.tile.multiblock.TileEntityInductionCasing;
import mekanism.common.tile.multiblock.TileEntityInductionCell;
import mekanism.common.tile.multiblock.TileEntityInductionProvider;
import mekanism.common.tile.multiblock.TileEntitySPSCasing;
import mekanism.common.tile.multiblock.TileEntitySuperchargedCoil;
import mekanism.common.tile.multiblock.TileEntityThermalEvaporationBlock;
import mekanism.common.tile.prefab.TileEntityRecipeMachine;
import mekanism.common.tile.prefab.TileEntityStructuralMultiblock;
import mekanism.generators.common.content.fission.FissionReactorMultiblockData;
import mekanism.generators.common.content.fusion.FusionReactorMultiblockData;
import mekanism.generators.common.tile.fission.TileEntityFissionAssembly;
import mekanism.generators.common.tile.fission.TileEntityFissionReactorCasing;
import mekanism.generators.common.tile.fusion.TileEntityFusionReactorBlock;
import mekanism.generators.common.tile.turbine.TileEntityElectromagneticCoil;
import mekanism.generators.common.tile.turbine.TileEntityRotationalComplex;
import mekanism.generators.common.tile.turbine.TileEntitySaturatingCondenser;
import mekanism.generators.common.tile.turbine.TileEntityTurbineCasing;
import mekanism.generators.common.tile.turbine.TileEntityTurbineRotor;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.BlockEntity;

public final class CircuitBoardProfileHelper {

    private CircuitBoardProfileHelper() {
    }

    public static CircuitBoardMachineProfile getMachineProfile(BlockEntity blockEntity) {
        return getSupportProfile(blockEntity).machineProfile();
    }

    public static MachineSupportProfile getSupportProfile(BlockEntity blockEntity) {
        if (!(blockEntity instanceof TileEntityMekanism tile)) {
            return MachineSupportProfile.unsupported();
        }
        CircuitBoardMachineProfile machineProfile;
        CircuitBoardMachineProfile structuralProfile = getStructuralMachineProfile(blockEntity);
        if (structuralProfile.isSupported()) {
            machineProfile = structuralProfile;
        } else if (blockEntity instanceof TileEntityFissionReactorCasing
            || blockEntity instanceof TileEntityFissionAssembly) {
            machineProfile = CircuitBoardMachineProfile.FISSION;
        } else if (blockEntity instanceof TileEntityFusionReactorBlock
            || blockEntity instanceof TileEntityTurbineCasing
            || blockEntity instanceof TileEntityTurbineRotor
            || blockEntity instanceof TileEntityElectromagneticCoil
            || blockEntity instanceof TileEntityRotationalComplex
            || blockEntity instanceof TileEntitySaturatingCondenser
            || blockEntity instanceof TileEntityInductionCasing
            || blockEntity instanceof TileEntityInductionCell
            || blockEntity instanceof TileEntityInductionProvider) {
            machineProfile = CircuitBoardMachineProfile.POWER_MULTIBLOCK;
        } else if (blockEntity instanceof TileEntityThermalEvaporationBlock) {
            machineProfile = CircuitBoardMachineProfile.EVAPORATION_MULTIBLOCK;
        } else if (blockEntity instanceof TileEntitySPSCasing
            || blockEntity instanceof TileEntitySuperchargedCoil) {
            machineProfile = CircuitBoardMachineProfile.SPS_MULTIBLOCK;
        } else if (JerryAddonCompat.isMoreGenerator(blockEntity)) {
            machineProfile = CircuitBoardMachineProfile.GENERATOR;
        } else if (GeneratorBoardService.isSupportedGenerator(tile)) {
            machineProfile = CircuitBoardMachineProfile.GENERATOR;
        } else if (JerryAddonCompat.isMoreMachineFactory(blockEntity)) {
            machineProfile = CircuitBoardMachineProfile.PROCESSING;
        } else if (blockEntity instanceof TileEntityRecipeMachine<?> || blockEntity instanceof TileEntityFactory<?>) {
            machineProfile = CircuitBoardMachineProfile.PROCESSING;
        } else {
            machineProfile = CircuitBoardMachineProfile.UNSUPPORTED;
        }
        if (!machineProfile.isSupported()) {
            return MachineSupportProfile.unsupported();
        }
        return new MachineSupportProfile(
            machineProfile,
            machineProfile.getAcceptedChannel(),
            toTooltipCategory(machineProfile),
            BoardSlotAnchorRegistry.resolve(blockEntity),
            isMultiblockProfile(machineProfile)
        );
    }

    public static boolean isSupportedMachine(BlockEntity blockEntity) {
        return isAllowedByConfig(blockEntity) && getSupportProfile(blockEntity).isSupported();
    }

    public static boolean acceptsBoard(BlockEntity blockEntity, ItemStack stack) {
        CircuitBoardChannel channel = CircuitBoardOverclockHelper.getCircuitBoardChannel(stack);
        if (channel == null) {
            return false;
        }
        MachineSupportProfile profile = getSupportProfile(blockEntity);
        return profile.isSupported() && profile.acceptedChannel() == channel && isAllowedByConfig(blockEntity);
    }

    public static boolean isMultiblockMachine(BlockEntity blockEntity) {
        return getSupportProfile(blockEntity).multiblock();
    }

    public static boolean isAllowedByConfig(BlockEntity blockEntity) {
        String className = blockEntity.getClass().getName();
        if (matchesAnyRule(className, OverMekConfig.getBlockedMachineClasses())) {
            return false;
        }
        List<? extends String> allowList = OverMekConfig.getAllowedMachineClasses();
        return allowList.isEmpty() || matchesAnyRule(className, allowList);
    }

    private static boolean matchesAnyRule(String className, List<? extends String> rules) {
        String simpleName = className.substring(className.lastIndexOf('.') + 1);
        for (String rawRule : rules) {
            if (rawRule == null) {
                continue;
            }
            String rule = rawRule.trim();
            if (rule.isEmpty()) {
                continue;
            }
            if (matchesRule(className, rule) || matchesRule(simpleName, rule)) {
                return true;
            }
        }
        return false;
    }

    private static boolean matchesRule(String value, String rule) {
        if (!rule.contains("*")) {
            return value.equals(rule);
        }
        String regex = "^" + Pattern.quote(rule).replace("\\*", "\\E.*\\Q") + "$";
        return Pattern.compile(regex).matcher(value).matches();
    }

    private static boolean isMultiblockProfile(CircuitBoardMachineProfile profile) {
        return switch (profile) {
            case FISSION, POWER_MULTIBLOCK, EVAPORATION_MULTIBLOCK, SPS_MULTIBLOCK -> true;
            default -> false;
        };
    }

    private static BoardTooltipCategory toTooltipCategory(CircuitBoardMachineProfile profile) {
        return switch (profile) {
            case PROCESSING -> BoardTooltipCategory.PROCESSING;
            case GENERATOR -> BoardTooltipCategory.GENERATOR;
            case FISSION -> BoardTooltipCategory.FISSION;
            case POWER_MULTIBLOCK -> BoardTooltipCategory.POWER_MULTIBLOCK;
            case EVAPORATION_MULTIBLOCK -> BoardTooltipCategory.EVAPORATION_MULTIBLOCK;
            case SPS_MULTIBLOCK -> BoardTooltipCategory.SPS_MULTIBLOCK;
            case UNSUPPORTED -> BoardTooltipCategory.UNSUPPORTED;
        };
    }

    private static CircuitBoardMachineProfile getStructuralMachineProfile(BlockEntity blockEntity) {
        if (!(blockEntity instanceof TileEntityStructuralMultiblock structuralMultiblock)) {
            return CircuitBoardMachineProfile.UNSUPPORTED;
        }
        for (var structure : structuralMultiblock.getStructureMap().values()) {
            if (structure == null || !structure.isValid()) {
                continue;
            }
            var data = structure.getMultiblockData();
            if (data instanceof FissionReactorMultiblockData) {
                return CircuitBoardMachineProfile.FISSION;
            }
            if (data instanceof FusionReactorMultiblockData) {
                return CircuitBoardMachineProfile.POWER_MULTIBLOCK;
            }
        }
        return CircuitBoardMachineProfile.UNSUPPORTED;
    }
}
