package cn.mythiclnd.mythiccore.command;

import java.util.Collections;
import java.util.List;
import org.bukkit.command.CommandSender;

public abstract class AbstractSubCommand {
    private final String name;
    private final String permission;
    private final String arguments;
    private final String description;

    protected AbstractSubCommand(
            String name,
            String permission,
            String arguments,
            String description
    ) {
        this.name = name;
        this.permission = permission;
        this.arguments = arguments;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getPermission() {
        return permission;
    }

    public String getArguments() {
        return arguments;
    }

    public String getDescription() {
        return description;
    }

    public boolean canExecute(CommandSender sender) {
        return permission == null || permission.isEmpty() || sender.hasPermission(permission);
    }

    public List<String> complete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }

    public abstract boolean execute(CommandSender sender, String label, String[] args);
}
