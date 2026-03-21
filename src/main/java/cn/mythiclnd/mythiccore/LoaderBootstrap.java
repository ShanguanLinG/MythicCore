package cn.mythiclnd.mythiccore;

import cn.mythiclnd.mythiccore.command.CommandManager;
import cn.mythiclnd.mythiccore.command.ReloadCommand;
import cn.mythiclnd.mythiccore.config.MessageConfig;
import cn.mythiclnd.mythiccore.config.model.MainSettings;
import cn.mythiclnd.mythiccore.listener.FakeAntiCheatBlocker;
import cn.mythiclnd.mythiccore.listener.PluginsCommandListener;
import cn.mythiclnd.mythiccore.listener.TabCompleteBlocker;
import cn.mythiclnd.mythiccore.manager.ConfigManager;
import cn.mythiclnd.mythiccore.util.PluginLogger;
import org.bukkit.command.PluginCommand;

final class LoaderBootstrap {
    private final PluginMain plugin;
    private final PluginLogger pluginLogger;
    private final ConfigManager configManager;
    private final CommandManager commandManager;

    private LoaderBootstrap(
            PluginMain plugin,
            PluginLogger pluginLogger,
            ConfigManager configManager,
            CommandManager commandManager
    ) {
        this.plugin = plugin;
        this.pluginLogger = pluginLogger;
        this.configManager = configManager;
        this.commandManager = commandManager;
    }

    static LoaderBootstrap create(PluginMain plugin) {
        PluginLogger pluginLogger = new PluginLogger(plugin.getLogger(), "MythicCore");
        ConfigManager configManager = new ConfigManager(plugin);
        CommandManager commandManager = new CommandManager();
        commandManager.register(new ReloadCommand());
        return new LoaderBootstrap(plugin, pluginLogger, configManager, commandManager);
    }

    void registerCommands() {
        PluginLoader loader = PluginLoader.access();
        if (loader.isCommandRegistered()) return;

        PluginCommand pluginCommand = plugin.getCommand("mythiccore");
        if (pluginCommand == null) {
            pluginLogger.warn("Main command is missing from plugin.yml.");
            return;
        }

        pluginCommand.setExecutor(commandManager);
        pluginCommand.setTabCompleter(commandManager);
        loader.markCommandRegistered();
    }

    PluginLoader.RuntimeSession startRuntime() {
        MainSettings settings = configManager.getMainConfig().getSettings();
        MessageConfig messageConfig = configManager.getMessageConfig();

        TabCompleteBlocker tabCompleteBlocker = null;
        if (settings.getTabComplete().isEnabled()) {
            tabCompleteBlocker = new TabCompleteBlocker(plugin, settings.getTabComplete());
            tabCompleteBlocker.register();
        }

        PluginsCommandListener pluginsCommandListener = null;
        if (settings.getPluginsCommand().isEnabled()) {
            pluginsCommandListener = new PluginsCommandListener(settings.getPluginsCommand(), messageConfig);
            plugin.getServer().getPluginManager().registerEvents(pluginsCommandListener, plugin);
        }

        FakeAntiCheatBlocker fakeAntiCheatBlocker = null;
        if (settings.getFakeAntiCheat().isEnabled()) {
            fakeAntiCheatBlocker = new FakeAntiCheatBlocker(plugin, settings.getFakeAntiCheat(), messageConfig);
            fakeAntiCheatBlocker.register();
        }

        return new PluginLoader.RuntimeSession(tabCompleteBlocker, pluginsCommandListener, fakeAntiCheatBlocker);
    }

    PluginLogger getPluginLogger() {
        return pluginLogger;
    }

    ConfigManager getConfigManager() {
        return configManager;
    }
}
