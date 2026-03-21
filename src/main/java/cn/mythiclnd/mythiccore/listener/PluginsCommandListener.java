package cn.mythiclnd.mythiccore.listener;

import cn.mythiclnd.mythiccore.config.MessageConfig;
import cn.mythiclnd.mythiccore.config.key.MessageKey;
import cn.mythiclnd.mythiccore.config.model.PluginsCommandSettings;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;

public final class PluginsCommandListener implements Listener {
    private static final String COLOR_ENABLED = "\u00A7a";
    private static final String COLOR_DISABLED = "\u00A7c";

    private final PluginsCommandSettings settings;
    private final MessageConfig messages;

    public PluginsCommandListener(PluginsCommandSettings settings, MessageConfig messages) {
        this.settings = settings;
        this.messages = messages;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        Player player = event.getPlayer();
        if (player.hasPermission(settings.getBypassPermission())) return;

        String message = event.getMessage();
        if (message == null || message.length() < 2 || message.charAt(0) != '/') return;

        String root = parseRootCommandLower(message);
        if (root == null || !settings.getAliases().contains(root)) return;

        event.setCancelled(true);

        List<String> shown = new ArrayList<>();
        Map<String, String> replacements = settings.getReplacements();
        for (Plugin plugin : Bukkit.getPluginManager().getPlugins()) {
            if (plugin == null || plugin.getDescription() == null) continue;

            String realName = plugin.getDescription().getName();
            if (realName == null || realName.isEmpty()) continue;

            String displayName = realName;
            String mappedName = replacements.get(realName.toLowerCase(Locale.ROOT));
            if (mappedName != null) {
                String trimmed = mappedName.trim();
                if (trimmed.isEmpty() || "-".equals(trimmed)) continue;
                displayName = ChatColor.translateAlternateColorCodes('&', trimmed);
            }

            shown.add((plugin.isEnabled() ? COLOR_ENABLED : COLOR_DISABLED) + displayName);
        }

        String formatted = messages.format(
                MessageKey.PLUGINS_LIST_FORMAT,
                Map.of(
                        "%count%", String.valueOf(shown.size()),
                        "%list%", String.join(messages.get(MessageKey.PLUGINS_SEPARATOR), shown)
                )
        );
        player.sendMessage(formatted);
    }

    private String parseRootCommandLower(String message) {
        int start = 1;
        int end = message.indexOf(' ');
        if (end == -1) end = message.length();
        if (end <= start) return null;
        return message.substring(start, end).toLowerCase(Locale.ROOT);
    }
}
