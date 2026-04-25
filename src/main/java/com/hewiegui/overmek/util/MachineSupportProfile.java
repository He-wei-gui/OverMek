package com.hewiegui.overmek.util;

import org.jetbrains.annotations.Nullable;

public record MachineSupportProfile(
    CircuitBoardMachineProfile machineProfile,
    @Nullable CircuitBoardChannel acceptedChannel,
    BoardTooltipCategory tooltipCategory,
    BoardSlotAnchor slotAnchor,
    boolean multiblock
) {

    public static MachineSupportProfile unsupported() {
        return new MachineSupportProfile(CircuitBoardMachineProfile.UNSUPPORTED, null, BoardTooltipCategory.UNSUPPORTED, BoardSlotAnchor.DEFAULT_RIGHT, false);
    }

    public boolean isSupported() {
        return acceptedChannel != null && machineProfile.isSupported();
    }
}
