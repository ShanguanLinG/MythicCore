package cn.mythiclnd.mythiccore.settings;

import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

public final class MythicSettings {
    public final boolean tabCompleteEnabled;
    public final String tabCompleteBypassPermission;
    public final String tabCompleteModeLower; // "blacklist" or "whitelist"
    public final Set<String> tabCompleteBlacklistPrefixesLower;
    public final Set<String> tabCompleteWhitelistPrefixesLower;

    public final boolean fakeAntiCheatEnabled;

    public final boolean pluginsCommandEnabled;
    public final String pluginsCommandBypassPermission;
    public final Set<String> pluginsCommandsLower;
    public final Map<String, String> pluginReplacementsLower;
    public final String pluginsPrefix;

    private MythicSettings(
            boolean tabCompleteEnabled,
            String tabCompleteBypassPermission,
            String tabCompleteModeLower,
            Set<String> tabCompleteBlacklistPrefixesLower,
            Set<String> tabCompleteWhitelistPrefixesLower,
            boolean fakeAntiCheatEnabled,
            boolean pluginsCommandEnabled,
            String pluginsCommandBypassPermission,
            Set<String> pluginsCommandsLower,
            Map<String, String> pluginReplacementsLower,
            String pluginsPrefix
    ) {
        this.tabCompleteEnabled = tabCompleteEnabled;
        this.tabCompleteBypassPermission = tabCompleteBypassPermission;
        this.tabCompleteModeLower = tabCompleteModeLower;
        this.tabCompleteBlacklistPrefixesLower = tabCompleteBlacklistPrefixesLower;
        this.tabCompleteWhitelistPrefixesLower = tabCompleteWhitelistPrefixesLower;
        this.fakeAntiCheatEnabled = fakeAntiCheatEnabled;
        this.pluginsCommandEnabled = pluginsCommandEnabled;
        this.pluginsCommandBypassPermission = pluginsCommandBypassPermission;
        this.pluginsCommandsLower = pluginsCommandsLower;
        this.pluginReplacementsLower = pluginReplacementsLower;
        this.pluginsPrefix = pluginsPrefix;
    }

    public static MythicSettings load(JavaPlugin plugin) {
        FileConfiguration cfg = plugin.getConfig();

        boolean tabEnabled = cfg.getBoolean("tab-complete.enabled", true);
        String tabBypass = cfg.getString("tab-complete.bypass-permission", "mythiccore.bypass.tabcomplete");
        String tabMode = cfg.getString("tab-complete.mode", "blacklist");
        String tabModeLower = tabMode == null ? "blacklist" : tabMode.trim().toLowerCase(Locale.ROOT);
        if (!"blacklist".equals(tabModeLower) && !"whitelist".equals(tabModeLower)) {
            tabModeLower = "blacklist";
        }

        Set<String> tabBlacklist = normalizePrefixList(cfg.getStringList("tab-complete.blacklist"));
        Set<String> tabWhitelist = normalizePrefixList(cfg.getStringList("tab-complete.whitelist"));

        boolean fakeAntiCheatEnabled = cfg.getBoolean("fakeanticheat", false);

        boolean plEnabled = cfg.getBoolean("plugins-command.enabled", true);
        String plBypass = cfg.getString("plugins-command.bypass-permission", "mythiccore.bypass.pluginslist");

        Set<String> cmdLower = new HashSet<>();
        for (String cmd : cfg.getStringList("plugins-command.commands")) {
            if (cmd == null) continue;
            String t = cmd.trim();
            if (!t.isEmpty()) cmdLower.add(t.toLowerCase());
        }
        if (cmdLower.isEmpty()) {
            Collections.addAll(cmdLower, "pl", "plugins", "bukkit:pl", "bukkit:plugins");
        }

        Map<String, String> replLower = new HashMap<>();
        ConfigurationSection repl = cfg.getConfigurationSection("plugins-command.replacements");
        if (repl != null) {
            for (String key : repl.getKeys(false)) {
                if (key == null) continue;
                String k = key.trim();
                if (k.isEmpty()) continue;
                String v = repl.getString(key, "");
                replLower.put(k.toLowerCase(), v == null ? "" : v);
            }
        }

        String prefix = color(cfg.getString("plugins-command.prefix", "&fPlugins (%count%): "));

        return new MythicSettings(
                tabEnabled,
                tabBypass,
                tabModeLower,
                tabBlacklist,
                tabWhitelist,
                fakeAntiCheatEnabled,
                plEnabled,
                plBypass,
                Collections.unmodifiableSet(cmdLower),
                Collections.unmodifiableMap(replLower),
                prefix
        );
    }

    public static String color(String s) {
        if (s == null) return "";
        return ChatColor.translateAlternateColorCodes('&', s);
    }

    private static Set<String> normalizePrefixList(Iterable<String> raw) {
        if (raw == null) return Collections.emptySet();
        LinkedHashSet<String> out = new LinkedHashSet<String>();
        for (String v : raw) {
            if (v == null) continue;
            String t = v.trim();
            if (t.isEmpty()) continue;
            if (t.charAt(0) != '/') t = "/" + t;
            out.add(t.toLowerCase(Locale.ROOT));
        }
        return Collections.unmodifiableSet(out);
    }
}
