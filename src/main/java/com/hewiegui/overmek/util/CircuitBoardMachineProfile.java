package com.hewiegui.overmek.util;

import org.jetbrains.annotations.Nullable;

public enum CircuitBoardMachineProfile {
    UNSUPPORTED(null),
    PROCESSING(CircuitBoardChannel.STANDARD),
    GENERATOR(CircuitBoardChannel.STANDARD),
    FISSION(CircuitBoardChannel.FISSION),
    POWER_MULTIBLOCK(CircuitBoardChannel.POWER_MULTIBLOCK),
    EVAPORATION_MULTIBLOCK(CircuitBoardChannel.EVAPORATION_MULTIBLOCK),
    SPS_MULTIBLOCK(CircuitBoardChannel.SPS_MULTIBLOCK);

    @Nullable
    private final CircuitBoardChannel acceptedChannel;

    CircuitBoardMachineProfile(@Nullable CircuitBoardChannel acceptedChannel) {
        this.acceptedChannel = acceptedChannel;
    }

    @Nullable
    public CircuitBoardChannel getAcceptedChannel() {
        return acceptedChannel;
    }

    public boolean isSupported() {
        return acceptedChannel != null;
    }
}
