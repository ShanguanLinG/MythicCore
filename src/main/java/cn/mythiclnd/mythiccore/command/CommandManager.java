package cn.mythiclnd.mythiccore.command;

import cn.mythiclnd.mythiccore.PluginLoader;
import cn.mythiclnd.mythiccore.config.MessageConfig;
import cn.mythiclnd.mythiccore.config.key.MessageKey;
import cn.mythiclnd.mythiccore.util.CommandHelpTheme;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

public final class CommandManager implements CommandExecutor, TabCompleter {
    private final Map<String, AbstractSubCommand> commands = new LinkedHashMap<>();

    public void register(AbstractSubCommand command) {
        commands.put(command.getName().toLowerCase(Locale.ROOT), command);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        MessageConfig messages = PluginLoader.getConfigManager().getMessageConfig();
        if (args.length == 0) {
            sendHelp(sender, label);
            return true;
        }

        AbstractSubCommand subCommand = commands.get(args[0].toLowerCase(Locale.ROOT));
        if (subCommand == null) {
            messages.send(sender, MessageKey.COMMAND_UNKNOWN_SUB_COMMAND);
            sendHelp(sender, label);
            return true;
        }
        if (!subCommand.canExecute(sender)) {
            messages.send(sender, MessageKey.COMMAND_NO_PERMISSION);
            return true;
        }

        if (PluginLoader.getPluginLogger() != null) {
            PluginLoader.getPluginLogger().debug("Executing sub command: " + subCommand.getName());
        }
        return subCommand.execute(sender, label, args);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            String input = args[0] == null ? "" : args[0].toLowerCase(Locale.ROOT);
            List<String> out = new ArrayList<>();
            for (AbstractSubCommand subCommand : commands.values()) {
                if (!subCommand.canExecute(sender)) continue;
                if (!subCommand.getName().startsWith(input)) continue;
                out.add(subCommand.getName());
            }
            return out;
        }

        if (args.length > 1) {
            AbstractSubCommand subCommand = commands.get(args[0].toLowerCase(Locale.ROOT));
            if (subCommand != null && subCommand.canExecute(sender)) return subCommand.complete(sender, args);
        }
        return List.of();
    }

    private void sendHelp(CommandSender sender, String label) {
        CommandHelpTheme.sendDivider(sender);
        for (AbstractSubCommand subCommand : commands.values()) {
            CommandHelpTheme.sendCommand(
                    sender,
                    label + " " + subCommand.getName(),
                    subCommand.getArguments(),
                    subCommand.getDescription()
            );
        }
        CommandHelpTheme.sendDivider(sender);
    }
}
