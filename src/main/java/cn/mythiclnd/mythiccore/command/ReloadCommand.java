package cn.mythiclnd.mythiccore.command;

import cn.mythiclnd.mythiccore.PluginLoader;
import cn.mythiclnd.mythiccore.config.MessageConfig;
import cn.mythiclnd.mythiccore.config.key.MessageKey;
import java.util.Map;
import org.bukkit.command.CommandSender;

public final class ReloadCommand extends AbstractSubCommand {
    public ReloadCommand() {
        super("reload", "mythiccore.command.reload", "", "重载插件配置");
    }

    @Override
    public boolean execute(CommandSender sender, String label, String[] args) {
        MessageConfig messages = PluginLoader.getConfigManager().getMessageConfig();
        try {
            PluginLoader.reload();
            PluginLoader.getConfigManager().getMessageConfig().send(sender, MessageKey.COMMAND_RELOAD_SUCCESS);
        } catch (Throwable throwable) {
            if (PluginLoader.getPluginLogger() != null) {
                PluginLoader.getPluginLogger().error("Failed to reload plugin.", throwable);
            }
            messages.send(
                    sender,
                    MessageKey.COMMAND_RELOAD_FAILED,
                    Map.of("%error%", throwable.getClass().getSimpleName())
            );
        }
        return true;
    }
}
