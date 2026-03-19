package cn.mythiclnd.mythiccore;

import cn.mythiclnd.mythiccore.commands.MythicCoreCommand;
import cn.mythiclnd.mythiccore.listeners.FakeAntiCheatBlocker;
import cn.mythiclnd.mythiccore.listeners.PluginsCommandListener;
import cn.mythiclnd.mythiccore.listeners.TabCompleteBlocker;
import cn.mythiclnd.mythiccore.messages.Messages;
import cn.mythiclnd.mythiccore.settings.MythicSettings;
import org.bukkit.command.PluginCommand;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public final class MythicCore extends JavaPlugin {
    private MythicSettings settings;
    private Messages messages;
    private TabCompleteBlocker tabCompleteBlocker;
    private PluginsCommandListener pluginsCommandListener;
    private FakeAntiCheatBlocker fakeAntiCheatBlocker;

    @Override
    public void onEnable() {
        saveDefaultConfig();

        this.messages = new Messages(this);
        this.messages.reload();
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
            MythicCoreCommand handler = new MythicCoreCommand(this);
            cmd.setExecutor(handler);
            cmd.setTabCompleter(handler);
        }
    }

    private void applySettings() {
        this.tabCompleteBlocker = new TabCompleteBlocker(this, this.settings);
        this.tabCompleteBlocker.register();

        this.pluginsCommandListener = new PluginsCommandListener(this.settings, this.messages);
        getServer().getPluginManager().registerEvents(this.pluginsCommandListener, this);

        this.fakeAntiCheatBlocker = new FakeAntiCheatBlocker(this, this.settings, this.messages);
        this.fakeAntiCheatBlocker.register();
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
        if (this.fakeAntiCheatBlocker != null) {
            this.fakeAntiCheatBlocker.unregister();
            this.fakeAntiCheatBlocker = null;
        }
    }

    public void reloadMythicCore() {
        reloadConfig();
        if (this.messages != null) {
            this.messages.reload();
        }
        this.settings = MythicSettings.load(this);

        unapplySettings();
        applySettings();
    }

    public Messages getMessages() {
        return this.messages;
    }
}
