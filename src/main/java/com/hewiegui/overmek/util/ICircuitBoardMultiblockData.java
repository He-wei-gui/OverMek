package com.hewiegui.overmek.util;

import mekanism.common.tile.base.TileEntityMekanism;
import org.jetbrains.annotations.Nullable;

public interface ICircuitBoardMultiblockData {

    @Nullable
    TileEntityMekanism overmek$getOwnerTile();
}
