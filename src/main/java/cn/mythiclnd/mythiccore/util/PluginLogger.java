package cn.mythiclnd.mythiccore.util;

import java.util.logging.Level;
import java.util.logging.Logger;

public final class PluginLogger {
    private final Logger logger;
    private final String prefix;
    private boolean debug;

    public PluginLogger(Logger logger, String prefix) {
        this.logger = logger;
        this.prefix = "[" + prefix + "] ";
    }

    public void setDebug(boolean debug) {
        this.debug = debug;
    }

    public void info(String message) {
        logger.info(prefix + message);
    }

    public void warn(String message) {
        logger.warning(prefix + message);
    }

    public void debug(String message) {
        if (debug) logger.info(prefix + "[DEBUG] " + message);
    }

    public void error(String message, Throwable throwable) {
        logger.log(Level.SEVERE, prefix + message, throwable);
    }
}
