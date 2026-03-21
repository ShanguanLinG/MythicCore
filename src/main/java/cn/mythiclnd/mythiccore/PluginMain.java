package cn.mythiclnd.mythiccore;

import org.bukkit.plugin.java.JavaPlugin;

public final class PluginMain extends JavaPlugin {
    @Override
    public void onLoad() {
        PluginLoader.onLoad(this);
    }

    @Override
    public void onEnable() {
        PluginLoader.onEnable();
    }

    @Override
    public void onDisable() {
        PluginLoader.onDisable();
    }
}
