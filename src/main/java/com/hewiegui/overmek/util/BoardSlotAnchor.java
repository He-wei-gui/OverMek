package com.hewiegui.overmek.util;

public enum BoardSlotAnchor {
    DEFAULT_RIGHT(176, 66, false, "external_right"),
    DEFAULT_LEFT(176, 66, true, "external_left"),
    ULTIMATE_FACTORY_RIGHT(210, 66, false, "external_right"),
    REACTOR_LEFT(176, 66, true, "reactor_left");

    private static final int EXTERNAL_SLOT_GAP = 1;
    private static final int EXTERNAL_SLOT_SIZE = 18;

    private final int guiWidth;
    private final int slotY;
    private final boolean leftSide;
    private final String layoutName;

    BoardSlotAnchor(int guiWidth, int slotY, boolean leftSide, String layoutName) {
        this.guiWidth = guiWidth;
        this.slotY = slotY;
        this.leftSide = leftSide;
        this.layoutName = layoutName;
    }

    public int getSlotX() {
        return leftSide ? -(EXTERNAL_SLOT_SIZE + EXTERNAL_SLOT_GAP) : guiWidth + EXTERNAL_SLOT_GAP;
    }

    public int getSlotY() {
        return slotY;
    }

    public int getWarmupBarX(int slotX) {
        return leftSide ? slotX - 4 : slotX + EXTERNAL_SLOT_SIZE;
    }

    public boolean isLeftSide() {
        return leftSide;
    }

    public String getLayoutName() {
        return layoutName;
    }
}
