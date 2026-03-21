package cn.mythiclnd.mythiccore.config;

import cn.mythiclnd.mythiccore.config.key.MainConfigKey;
import cn.mythiclnd.mythiccore.config.model.FakeAntiCheatSettings;
import cn.mythiclnd.mythiccore.config.model.MainSettings;
import cn.mythiclnd.mythiccore.config.model.PluginsCommandSettings;
import cn.mythiclnd.mythiccore.config.model.TabCompleteSettings;
import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

public final class MainConfig extends AbstractYamlConfiguration {
    private MainSettings settings;

    public MainConfig(Plugin plugin) {
        super(plugin);
    }

    @Override
    protected String getFileName() {
        return "config.yml";
    }

    @Override
    protected void onReload(YamlConfiguration yamlConfiguration) {
        settings = new MainSettings(
                loadTabComplete(yamlConfiguration),
                loadPluginsCommand(yamlConfiguration),
                loadFakeAntiCheat(yamlConfiguration)
        );
    }

    public MainSettings getSettings() {
        return settings;
    }

    private TabCompleteSettings loadTabComplete(YamlConfiguration yamlConfiguration) {
        boolean enabled = yamlConfiguration.getBoolean(MainConfigKey.TAB_COMPLETE_ENABLED.getPath(), true);
        String bypassPermission = yamlConfiguration.getString(
                MainConfigKey.TAB_COMPLETE_BYPASS_PERMISSION.getPath(),
                "mythiccore.bypass.tab-complete"
        );
        String mode = normalizeMode(yamlConfiguration.getString(MainConfigKey.TAB_COMPLETE_MODE.getPath(), "blacklist"));
        Set<String> blacklist = normalizePrefixes(
                yamlConfiguration.getStringList(MainConfigKey.TAB_COMPLETE_BLACKLIST_PREFIXES.getPath())
        );
        Set<String> whitelist = normalizePrefixes(
                yamlConfiguration.getStringList(MainConfigKey.TAB_COMPLETE_WHITELIST_PREFIXES.getPath())
        );
        return new TabCompleteSettings(enabled, bypassPermission, mode, blacklist, whitelist);
    }

    private PluginsCommandSettings loadPluginsCommand(YamlConfiguration yamlConfiguration) {
        boolean enabled = yamlConfiguration.getBoolean(MainConfigKey.PLUGINS_COMMAND_ENABLED.getPath(), true);
        String bypassPermission = yamlConfiguration.getString(
                MainConfigKey.PLUGINS_COMMAND_BYPASS_PERMISSION.getPath(),
                "mythiccore.bypass.plugins-command"
        );
        Set<String> aliases = normalizeAliases(
                yamlConfiguration.getStringList(MainConfigKey.PLUGINS_COMMAND_ALIASES.getPath())
        );
        Map<String, String> replacements = loadReplacements(yamlConfiguration);
        return new PluginsCommandSettings(enabled, bypassPermission, aliases, replacements);
    }

    private FakeAntiCheatSettings loadFakeAntiCheat(YamlConfiguration yamlConfiguration) {
        boolean enabled = yamlConfiguration.getBoolean(MainConfigKey.FAKE_ANTI_CHEAT_ENABLED.getPath(), false);
        return new FakeAntiCheatSettings(enabled);
    }

    private Map<String, String> loadReplacements(YamlConfiguration yamlConfiguration) {
        ConfigurationSection replacementsSection = yamlConfiguration.getConfigurationSection(
                MainConfigKey.PLUGINS_COMMAND_REPLACEMENTS.getPath()
        );
        if (replacementsSection == null) return Map.of();

        return replacementsSection.getKeys(false).stream()
                .map(String::trim)
                .filter(key -> !key.isEmpty())
                .collect(Collectors.toUnmodifiableMap(
                        key -> key.toLowerCase(Locale.ROOT),
                        key -> {
                            String value = replacementsSection.getString(key, "");
                            return value == null ? "" : value;
                        },
                        (left, right) -> right
                ));
    }

    private Set<String> normalizeAliases(Iterable<String> rawAliases) {
        LinkedHashSet<String> aliases = new LinkedHashSet<>();
        for (String rawAlias : rawAliases) {
            if (rawAlias == null) continue;
            String alias = rawAlias.trim();
            if (alias.isEmpty()) continue;
            aliases.add(alias.toLowerCase(Locale.ROOT));
        }
        if (aliases.isEmpty()) aliases.addAll(Set.of("pl", "plugins", "bukkit:pl", "bukkit:plugins"));
        return Set.copyOf(aliases);
    }

    private Set<String> normalizePrefixes(Iterable<String> rawPrefixes) {
        LinkedHashSet<String> prefixes = new LinkedHashSet<>();
        for (String rawPrefix : rawPrefixes) {
            if (rawPrefix == null) continue;
            String prefix = rawPrefix.trim();
            if (prefix.isEmpty()) continue;
            if (prefix.charAt(0) != '/') prefix = "/" + prefix;
            prefixes.add(prefix.toLowerCase(Locale.ROOT));
        }
        return Set.copyOf(prefixes);
    }

    private String normalizeMode(String rawMode) {
        String mode = rawMode == null ? "blacklist" : rawMode.trim().toLowerCase(Locale.ROOT);
        if (!"whitelist".equals(mode)) return "blacklist";
        return mode;
    }
}
