package cn.mythiclnd.mythiccore.util;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public final class CommandHelpTheme {
    private static final String DIVIDER = "&7&m----------------------------------------";

    private CommandHelpTheme() {
    }

    public static void sendDivider(CommandSender sender) {
        sender.sendMessage(color(DIVIDER));
    }

    public static void sendCommand(CommandSender sender, String command, String arguments, String description) {
        StringBuilder message = new StringBuilder("&d/").append(command);
        appendUsageTokens(message, arguments);
        if (description != null && !description.isBlank()) message.append(" &7- ").append(description.trim());
        sender.sendMessage(color(message.toString()));
    }

    private static void appendUsageTokens(StringBuilder message, String arguments) {
        if (arguments == null) return;

        String trimmed = arguments.trim();
        if (trimmed.isEmpty()) return;

        for (String token : trimmed.split("\\s+")) {
            if (token.isEmpty()) continue;
            message.append(' ');
            message.append(isPlaceholderToken(token) ? "&f" + token : "&d" + token);
        }
    }

    private static boolean isPlaceholderToken(String token) {
        return token.startsWith("<") && token.endsWith(">")
                || token.startsWith("[") && token.endsWith("]");
    }

    private static String color(String message) {
        return ChatColor.translateAlternateColorCodes('&', message);
    }
}
