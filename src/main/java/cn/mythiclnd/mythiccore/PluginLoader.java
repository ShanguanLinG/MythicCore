package cn.mythiclnd.mythiccore;

import cn.mythiclnd.mythiccore.listener.FakeAntiCheatBlocker;
import cn.mythiclnd.mythiccore.listener.PluginsCommandListener;
import cn.mythiclnd.mythiccore.listener.TabCompleteBlocker;
import cn.mythiclnd.mythiccore.manager.ConfigManager;
import cn.mythiclnd.mythiccore.util.PluginLogger;
import org.bukkit.event.HandlerList;

public final class PluginLoader {
    private static PluginMain instance;
    private static PluginLogger pluginLogger;
    private static ConfigManager configManager;
    private static LoaderBootstrap bootstrap;
    private static RuntimeSession runtimeSession;
    private static boolean commandRegistered;

    private PluginLoader() {
    }

    public static void onLoad(PluginMain plugin) {
        instance = plugin;
        bootstrap = LoaderBootstrap.create(plugin);
        pluginLogger = bootstrap.getPluginLogger();
        configManager = bootstrap.getConfigManager();
    }

    public static void onEnable() {
        if (bootstrap == null && instance != null) onLoad(instance);
        if (bootstrap == null || instance == null) return;

        bootstrap.registerCommands();
        startRuntime();
        pluginLogger.info("Plugin enabled.");
    }

    public static void onDisable() {
        stopRuntime();
        if (instance != null) HandlerList.unregisterAll(instance);
        if (pluginLogger != null) pluginLogger.info("Plugin disabled.");

        commandRegistered = false;
        runtimeSession = null;
        bootstrap = null;
        configManager = null;
        pluginLogger = null;
        instance = null;
    }

    public static void reload() {
        if (bootstrap == null || configManager == null) throw new IllegalStateException("Plugin bootstrap is not ready.");
        stopRuntime();
        configManager.reload();
        startRuntime();
        pluginLogger.info("Plugin reloaded.");
    }

    public static PluginMain getInstance() {
        return instance;
    }

    public static PluginLogger getPluginLogger() {
        return pluginLogger;
    }

    public static ConfigManager getConfigManager() {
        return configManager;
    }

    static PluginLoader access() {
        return Holder.INSTANCE;
    }

    boolean isCommandRegistered() {
        return commandRegistered;
    }

    void markCommandRegistered() {
        commandRegistered = true;
    }

    private static void startRuntime() {
        runtimeSession = bootstrap.startRuntime();
    }

    private static void stopRuntime() {
        if (runtimeSession == null || instance == null) return;
        runtimeSession.close(instance);
        runtimeSession = null;
    }

    private static final class Holder {
        private static final PluginLoader INSTANCE = new PluginLoader();
    }

    static final class RuntimeSession {
        private final TabCompleteBlocker tabCompleteBlocker;
        private final PluginsCommandListener pluginsCommandListener;
        private final FakeAntiCheatBlocker fakeAntiCheatBlocker;

        RuntimeSession(
                TabCompleteBlocker tabCompleteBlocker,
                PluginsCommandListener pluginsCommandListener,
                FakeAntiCheatBlocker fakeAntiCheatBlocker
        ) {
            this.tabCompleteBlocker = tabCompleteBlocker;
            this.pluginsCommandListener = pluginsCommandListener;
            this.fakeAntiCheatBlocker = fakeAntiCheatBlocker;
        }

        void close(PluginMain plugin) {
            if (tabCompleteBlocker != null) tabCompleteBlocker.unregister();
            if (pluginsCommandListener != null) HandlerList.unregisterAll(pluginsCommandListener);
            if (fakeAntiCheatBlocker != null) fakeAntiCheatBlocker.unregister();
            HandlerList.unregisterAll(plugin);
        }
    }
}
