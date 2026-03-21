package cn.mythiclnd.mythiccore.config.key;

public enum MainConfigKey {
    TAB_COMPLETE_ENABLED("tab-complete.enabled"),
    TAB_COMPLETE_BYPASS_PERMISSION("tab-complete.bypass-permission"),
    TAB_COMPLETE_MODE("tab-complete.mode"),
    TAB_COMPLETE_BLACKLIST_PREFIXES("tab-complete.blacklist-prefixes"),
    TAB_COMPLETE_WHITELIST_PREFIXES("tab-complete.whitelist-prefixes"),
    FAKE_ANTI_CHEAT_ENABLED("fake-anti-cheat.enabled"),
    PLUGINS_COMMAND_ENABLED("plugins-command.enabled"),
    PLUGINS_COMMAND_BYPASS_PERMISSION("plugins-command.bypass-permission"),
    PLUGINS_COMMAND_ALIASES("plugins-command.aliases"),
    PLUGINS_COMMAND_REPLACEMENTS("plugins-command.replacements");

    private final String path;

    MainConfigKey(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
