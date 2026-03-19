package cn.mythiclnd.mythiccore.messages;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.util.Collections;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public final class Messages {
    private final JavaPlugin plugin;
    private File file;
    private FileConfiguration cfg;

    private final Map<String, String> defaults;

    public Messages(JavaPlugin plugin) {
        this.plugin = plugin;
        this.defaults = createDefaults();
    }

    public void reload() {
        this.file = new File(plugin.getDataFolder(), "message.yml");
        if (!this.file.exists()) {
            plugin.saveResource("message.yml", false);
        }
        this.cfg = YamlConfiguration.loadConfiguration(this.file);
    }

    public boolean has(String key) {
        if (cfg == null) return false;
        return cfg.isSet(key);
    }

    public String raw(String key) {
        String val = null;
        if (cfg != null) {
            val = cfg.getString(key);
        }
        if (val == null) {
            val = defaults.get(key);
        }
        if (val == null) {
            // Last resort: show the key itself (so it's obvious what's missing).
            val = key;
        }
        return val;
    }

    public String get(String key) {
        return color(raw(key));
    }

    public List<String> getStringList(String key) {
        if (cfg == null || !cfg.isList(key)) return Collections.emptyList();
        List<String> list = cfg.getStringList(key);
        if (list == null || list.isEmpty()) return Collections.emptyList();
        for (int i = 0; i < list.size(); i++) {
            list.set(i, color(list.get(i)));
        }
        return list;
    }

    /**
     * Reads a "lines" value that can be either a YAML list, or a single string (fallback/default) containing \n or \\n.
     */
    public List<String> getLines(String key) {
        List<String> list = getStringList(key);
        if (!list.isEmpty()) return list;

        String raw = raw(key);
        if (raw == null || raw.isEmpty() || raw.equals(key)) return Collections.emptyList();

        String normalized = raw.replace("\\\\n", "\n");
        String[] parts = normalized.split("\n");
        List<String> out = new ArrayList<String>(parts.length);
        for (String p : parts) {
            if (p == null) continue;
            String t = p;
            if (!t.isEmpty()) out.add(color(t));
        }
        return out;
    }

    public String format(String key, Map<String, String> placeholders) {
        String msg = raw(key);
        if (placeholders != null && !placeholders.isEmpty()) {
            for (Map.Entry<String, String> e : placeholders.entrySet()) {
                String ph = e.getKey();
                if (ph == null || ph.isEmpty()) continue;
                String v = e.getValue();
                msg = msg.replace(ph, v == null ? "" : v);
            }
        }
        return color(msg);
    }

    public void send(CommandSender sender, String key) {
        sender.sendMessage(get(key));
    }

    public void send(CommandSender sender, String key, Map<String, String> placeholders) {
        sender.sendMessage(format(key, placeholders));
    }

    public static String color(String s) {
        if (s == null) return "";
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    private static Map<String, String> createDefaults() {
        Map<String, String> m = new HashMap<String, String>();
        m.put("reload.no-permission", "&c你没有权限执行这个命令。");
        m.put("reload.success", "&a已重载配置。");
        m.put("reload.failed", "&c重载失败: %error%");

        m.put("plugins.list-format", "&fPlugins (%count%): %list%");
        m.put("plugins.separator", "&f, ");

        // FakeAntiCheat defaults (line-based; used when message.yml is missing keys).
        m.put("fakeac.aac.lines",
                "\u00A76 AAC\u00A7fLicenced to https://spigotmc.org/members/980462\\n" +
                        "\u00A76 AAC\u00A7fAAC\u00A774.4.2\u00A7f~konsolas"
        );
        m.put("fakeac.ncp.lines",
                "\u00A7fAdministrative commands overview:\\n" +
                        "\u00A7f/ncp top (entries) (check/s...) (sort by...) NEW\\n" +
                        "\u00A7f/ncp info (player: Violation summary for a player.)\\n" +
                        "\u00A7f/ncp inspect (player): Status info for a player.\\n" +
                        "\u00A7f/ncp notify on|off: In-game notifications per player.\\n" +
                        "\u00A7f/ncp removeplayer (player) [(check type)]: Remove data.\\n" +
                        "\u00A7f/ncp reload: Reload the configuration.\\n" +
                        "\u00A7f/ncp lag: Lag-related info.\\n" +
                        "\u00A7f/ncp version: Version information\\n" +
                        "\u00A7f/ncp commands: List all commands, adds rarely used ones."
        );
        m.put("fakeac.verus.lines", "\u00A7cYou do not have permission to perform this command.");
        m.put("fakeac.spartan.lines", "&4Spartan Anti-Cheat &8[&7(&fVersion: Build 442&7)&8] [&7(&fID: 980462/1132701857&7)&8]");
        m.put("fakeac.acr.lines", "\u00A77Running \u00A76AntiCheatReloaded \u00A77version 1.9.8");
        m.put("fakeac.anticheat.lines", "\u00A77Running \u00A76AntiCheatReloaded \u00A77version 1.9.8");
        m.put("fakeac.bac.lines", "\u00A7eBAC\u00A77 BetterAntiCheat v1.0.4\u00A7a by LiquidDev");
        m.put("fakeac.betteranticheat.lines", "\u00A7eBAC\u00A77 BetterAntiCheat v1.0.4\u00A7a by LiquidDev");
        m.put("fakeac.vulcan.lines", "\u00A7cYou don't have permission to execute this command!");
        return Collections.unmodifiableMap(m);
    }
}
