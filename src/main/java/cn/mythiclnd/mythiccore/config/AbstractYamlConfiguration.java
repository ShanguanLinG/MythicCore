package cn.mythiclnd.mythiccore.config;

import java.io.File;
import java.io.IOException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public abstract class AbstractYamlConfiguration {
    private final Plugin plugin;
    private final File file;
    private YamlConfiguration yamlConfiguration;

    protected AbstractYamlConfiguration(Plugin plugin) {
        this.plugin = plugin;
        this.file = new File(plugin.getDataFolder(), getFileName());
        reload();
    }

    protected abstract String getFileName();

    public final void reload() {
        saveDefaultConfig();
        yamlConfiguration = YamlConfiguration.loadConfiguration(file);
        onReload(yamlConfiguration);
    }

    public final void save() {
        try {
            yamlConfiguration.save(file);
        } catch (IOException exception) {
            throw new RuntimeException("Failed to save " + getFileName(), exception);
        }
    }

    public final YamlConfiguration getYamlConfiguration() {
        return yamlConfiguration;
    }

    protected void onReload(YamlConfiguration yamlConfiguration) {
    }

    protected final Plugin getPlugin() {
        return plugin;
    }

    private void saveDefaultConfig() {
        if (file.exists()) return;
        File dataFolder = plugin.getDataFolder();
        if (!dataFolder.exists() && !dataFolder.mkdirs()) {
            throw new IllegalStateException("Failed to create plugin data folder.");
        }
        plugin.saveResource(getFileName(), false);
    }
}
