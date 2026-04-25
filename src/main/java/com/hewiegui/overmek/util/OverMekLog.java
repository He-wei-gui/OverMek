package com.hewiegui.overmek.util;

import com.mojang.logging.LogUtils;
import com.hewiegui.overmek.config.OverMekConfig;
import org.slf4j.Logger;

public final class OverMekLog {

    private static final Logger LOGGER = LogUtils.getLogger();

    private OverMekLog() {
    }

    public static void debug(String message, Object... args) {
        if (OverMekConfig.isDebugLoggingEnabled()) {
            LOGGER.info(message, args);
        }
    }
}
