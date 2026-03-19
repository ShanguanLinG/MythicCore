package cn.mythiclnd.mythiccore.commands;

import cn.mythiclnd.mythiccore.MythicCore;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public final class MythicCoreCommand implements CommandExecutor {
    private final MythicCore plugin;

    public MythicCoreCommand(MythicCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length == 1 && "reload".equalsIgnoreCase(args[0])) {
            if (!sender.hasPermission("mythiccore.admin.reload")) {
                sender.sendMessage("§c你没有权限执行这个命令。");
                return true;
            }
            try {
                plugin.reloadMythicCore();
                sender.sendMessage("§a已重载配置。");
            } catch (Throwable t) {
                sender.sendMessage("§c重载失败: " + t.getClass().getSimpleName());
            }
            return true;
        }

        sender.sendMessage("§cUsage: /" + label + " reload");
        return true;
    }
}

