package cn.mythiclnd.mythiccore.commands;

import cn.mythiclnd.mythiccore.MythicCore;
import cn.mythiclnd.mythiccore.messages.Messages;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class MythicCoreCommand implements CommandExecutor, TabCompleter {
    private final MythicCore plugin;

    public MythicCoreCommand(MythicCore plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        Messages messages = plugin.getMessages();
        if (args.length == 1 && "reload".equalsIgnoreCase(args[0])) {
            if (!sender.hasPermission("mythiccore.admin.reload")) {
                if (messages != null) {
                    messages.send(sender, "reload.no-permission");
                } else {
                    sender.sendMessage("§c你没有权限执行这个命令。");
                }
                return true;
            }
            try {
                plugin.reloadMythicCore();
                if (messages != null) {
                    messages.send(sender, "reload.success");
                } else {
                    sender.sendMessage("§a已重载配置。");
                }
            } catch (Throwable t) {
                if (messages != null) {
                    Map<String, String> ph = new HashMap<String, String>();
                    ph.put("%error%", t.getClass().getSimpleName());
                    messages.send(sender, "reload.failed", ph);
                } else {
                    sender.sendMessage("§c重载失败: " + t.getClass().getSimpleName());
                }
            }
            return true;
        }

        // Usage is intentionally NOT configurable (per project requirement).
        sender.sendMessage("§cUsage: /" + label + " reload");
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String cur = args[0] == null ? "" : args[0].toLowerCase();
            if ("reload".startsWith(cur)) {
                List<String> out = new ArrayList<String>(1);
                out.add("reload");
                return out;
            }
        }
        return Collections.emptyList();
    }
}
