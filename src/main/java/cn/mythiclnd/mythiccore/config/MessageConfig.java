package cn.mythiclnd.mythiccore.config;

import cn.mythiclnd.mythiccore.config.key.MessageKey;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

public final class MessageConfig extends AbstractYamlConfiguration {
    private final Map<String, String> defaults = createDefaults();

    public MessageConfig(Plugin plugin) {
        super(plugin);
    }

    @Override
    protected String getFileName() {
        return "messages.yml";
    }

    public boolean has(MessageKey key) {
        return getYamlConfiguration().isSet(key.getPath());
    }

    public String get(MessageKey key) {
        return color(resolveMessage(key.getPath()));
    }

    public String get(String key) {
        return color(resolveMessage(key));
    }

    public List<String> getLines(String key) {
        if (getYamlConfiguration().isList(key)) {
            List<String> lines = getYamlConfiguration().getStringList(key);
            List<String> output = new ArrayList<>(lines.size());
            for (String line : lines) {
                if (line == null || line.isEmpty()) continue;
                output.add(color(applyBuiltInPlaceholders(line, key)));
            }
            return output;
        }

        String raw = resolveMessage(key);
        if (raw.equals(key) || raw.isEmpty()) return List.of();

        String normalized = raw.replace("\\\\n", "\n");
        List<String> output = new ArrayList<>();
        for (String line : normalized.split("\n")) {
            if (line == null || line.isEmpty()) continue;
            output.add(color(line));
        }
        return output;
    }

    public String format(MessageKey key, Map<String, String> placeholders) {
        return format(key.getPath(), placeholders);
    }

    public String format(String key, Map<String, String> placeholders) {
        String message = resolveMessage(key);
        if (placeholders != null) {
            for (Map.Entry<String, String> entry : placeholders.entrySet()) {
                String placeholder = entry.getKey();
                if (placeholder == null || placeholder.isEmpty()) continue;
                String value = entry.getValue();
                message = message.replace(placeholder, value == null ? "" : value);
            }
        }
        return color(message);
    }

    public void send(CommandSender sender, MessageKey key) {
        sender.sendMessage(get(key));
    }

    public void send(CommandSender sender, MessageKey key, Map<String, String> placeholders) {
        sender.sendMessage(format(key, placeholders));
    }

    private String resolveMessage(String key) {
        return applyBuiltInPlaceholders(resolveRaw(key), key);
    }

    private String resolveRaw(String key) {
        String value = getYamlConfiguration().getString(key);
        if (value != null) return value;
        return defaults.getOrDefault(key, key);
    }

    private String applyBuiltInPlaceholders(String message, String key) {
        if (message == null || message.isEmpty()) return message == null ? "" : message;
        if (MessageKey.PREFIX.getPath().equals(key)) return message;
        return message.replace("{prefix}", resolveRaw(MessageKey.PREFIX.getPath()));
    }

    private String color(String text) {
        if (text == null) return "";
        return ChatColor.translateAlternateColorCodes('&', text);
    }

    private Map<String, String> createDefaults() {
        return Map.ofEntries(
                Map.entry(MessageKey.PREFIX.getPath(), "&8[&dMythicCore&8] &7"),
                Map.entry(MessageKey.COMMAND_NO_PERMISSION.getPath(), "&c你没有权限执行这个命令。"),
                Map.entry(MessageKey.COMMAND_UNKNOWN_SUB_COMMAND.getPath(), "&c未知子命令。"),
                Map.entry(MessageKey.COMMAND_RELOAD_SUCCESS.getPath(), "&a已重载配置。"),
                Map.entry(MessageKey.COMMAND_RELOAD_FAILED.getPath(), "&c重载失败: %error%"),
                Map.entry(MessageKey.PLUGINS_LIST_FORMAT.getPath(), "&fPlugins (%count%): %list%"),
                Map.entry(MessageKey.PLUGINS_SEPARATOR.getPath(), "&f, "),
                Map.entry("fake-anti-cheat.responses.aac.lines",
                        "&6 AAC&fLicenced to https://spigotmc.org/members/980462\\\\n" +
                                "&6 AAC&fAAC&74.4.2&f~konsolas"),
                Map.entry("fake-anti-cheat.responses.ncp.lines",
                        "&fAdministrative commands overview:\\\\n" +
                                "&f/ncp top (entries) (check/s...) (sort by...) NEW\\\\n" +
                                "&f/ncp info (player: Violation summary for a player.)\\\\n" +
                                "&f/ncp inspect (player): Status info for a player.\\\\n" +
                                "&f/ncp notify on|off: In-game notifications per player.\\\\n" +
                                "&f/ncp removeplayer (player) [(check type)]: Remove data.\\\\n" +
                                "&f/ncp reload: Reload the configuration.\\\\n" +
                                "&f/ncp lag: Lag-related info.\\\\n" +
                                "&f/ncp version: Version information\\\\n" +
                                "&f/ncp commands: List all commands, adds rarely used ones."),
                Map.entry("fake-anti-cheat.responses.verus.lines", "&cYou do not have permission to perform this command."),
                Map.entry("fake-anti-cheat.responses.spartan.lines",
                        "&4Spartan Anti-Cheat &8[&7(&fVersion: Build 442&7)&8] [&7(&fID: 980462/1132701857&7)&8]"),
                Map.entry("fake-anti-cheat.responses.acr.lines", "&7Running &6AntiCheatReloaded &7version 1.9.8"),
                Map.entry("fake-anti-cheat.responses.anticheat.lines", "&7Running &6AntiCheatReloaded &7version 1.9.8"),
                Map.entry("fake-anti-cheat.responses.bac.lines", "&eBAC&7 BetterAntiCheat v1.0.4&a by LiquidDev"),
                Map.entry("fake-anti-cheat.responses.betteranticheat.lines",
                        "&eBAC&7 BetterAntiCheat v1.0.4&a by LiquidDev"),
                Map.entry("fake-anti-cheat.responses.vulcan.lines", "&cYou don't have permission to execute this command!")
        );
    }
}
