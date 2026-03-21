package cn.mythiclnd.mythiccore.config.key;

public enum MessageKey {
    PREFIX("prefix"),
    COMMAND_NO_PERMISSION("command.no-permission"),
    COMMAND_UNKNOWN_SUB_COMMAND("command.unknown-sub-command"),
    COMMAND_RELOAD_SUCCESS("command.reload.success"),
    COMMAND_RELOAD_FAILED("command.reload.failed"),
    PLUGINS_LIST_FORMAT("plugins.list-format"),
    PLUGINS_SEPARATOR("plugins.separator");

    private final String path;

    MessageKey(String path) {
        this.path = path;
    }

    public String getPath() {
        return path;
    }
}
