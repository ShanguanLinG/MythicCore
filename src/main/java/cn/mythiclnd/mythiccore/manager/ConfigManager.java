package cn.mythiclnd.mythiccore.manager;

import cn.mythiclnd.mythiccore.config.MainConfig;
import cn.mythiclnd.mythiccore.config.MessageConfig;
import org.bukkit.plugin.Plugin;

public final class ConfigManager {
    private final MainConfig mainConfig;
    private final MessageConfig messageConfig;

    public ConfigManager(Plugin plugin) {
        this.mainConfig = new MainConfig(plugin);
        this.messageConfig = new MessageConfig(plugin);
    }

    public MainConfig getMainConfig() {
        return mainConfig;
    }

    public MessageConfig getMessageConfig() {
        return messageConfig;
    }

    public void reload() {
        mainConfig.reload();
        messageConfig.reload();
    }
}
