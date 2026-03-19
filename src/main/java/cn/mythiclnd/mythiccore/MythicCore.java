package cn.mythiclnd.mythiccore;

import cn.mythiclnd.mythiccore.commands.MythicCoreCommand;
import cn.mythiclnd.mythiccore.listeners.PluginsCommandListener;
import cn.mythiclnd.mythiccore.listeners.TabCompleteBlocker;
import cn.mythiclnd.mythiccore.settings.MythicSettings;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public final class MythicCore extends JavaPlugin {
    private MythicSettings settings;
    private TabCompleteBlocker tabCompleteBlocker;
    private PluginsCommandListener pluginsCommandListener;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.settings = MythicSettings.load(this);
        registerCommands();
        applySettings();
    }

    @Override
    public void onDisable() {
        unapplySettings();
    }

    private void registerCommands() {
        PluginCommand cmd = getCommand("mythiccore");
        if (cmd != null) {
            cmd.setExecutor(new MythicCoreCommand(this));
        }
    }

    private void applySettings() {
        this.tabCompleteBlocker = new TabCompleteBlocker(this, this.settings);
        this.tabCompleteBlocker.register();

        this.pluginsCommandListener = new PluginsCommandListener(this.settings);
        getServer().getPluginManager().registerEvents(this.pluginsCommandListener, this);
    }

    private void unapplySettings() {
        if (this.tabCompleteBlocker != null) {
            this.tabCompleteBlocker.unregister();
            this.tabCompleteBlocker = null;
        }
        if (this.pluginsCommandListener != null) {
            HandlerList.unregisterAll(this.pluginsCommandListener);
            this.pluginsCommandListener = null;
        }
    }

    public void reloadMythicCore() {
        reloadConfig();
        this.settings = MythicSettings.load(this);

        unapplySettings();
        applySettings();
    }
}
