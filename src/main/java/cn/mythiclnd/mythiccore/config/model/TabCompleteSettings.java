package cn.mythiclnd.mythiccore.config.model;

import java.util.Set;

public record TabCompleteSettings(
        boolean enabled,
        String bypassPermission,
        String mode,
        Set<String> blacklistPrefixes,
        Set<String> whitelistPrefixes
) {
    public boolean isEnabled() {
        return enabled;
    }

    public String getBypassPermission() {
        return bypassPermission;
    }

    public String getMode() {
        return mode;
    }

    public Set<String> getBlacklistPrefixes() {
        return blacklistPrefixes;
    }

    public Set<String> getWhitelistPrefixes() {
        return whitelistPrefixes;
    }
}
