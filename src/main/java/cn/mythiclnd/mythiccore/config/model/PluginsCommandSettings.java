package cn.mythiclnd.mythiccore.config.model;

import java.util.Map;
import java.util.Set;

public record PluginsCommandSettings(
        boolean enabled,
        String bypassPermission,
        Set<String> aliases,
        Map<String, String> replacements
) {
    public boolean isEnabled() {
        return enabled;
    }

    public String getBypassPermission() {
        return bypassPermission;
    }

    public Set<String> getAliases() {
        return aliases;
    }

    public Map<String, String> getReplacements() {
        return replacements;
    }
}
