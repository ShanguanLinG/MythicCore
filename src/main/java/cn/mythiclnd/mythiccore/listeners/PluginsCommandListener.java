package cn.mythiclnd.mythiccore.listeners;

import cn.mythiclnd.mythiccore.messages.Messages;
import cn.mythiclnd.mythiccore.settings.MythicSettings;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public final class PluginsCommandListener implements Listener {
    private final MythicSettings settings;
    private final Messages messages;
    private static final String COLOR_ENABLED = "\u00A7a";
    private static final String COLOR_DISABLED = "\u00A7c";

    public PluginsCommandListener(MythicSettings settings, Messages messages) {
        this.settings = settings;
        this.messages = messages;
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerCommand(PlayerCommandPreprocessEvent event) {
        if (!settings.pluginsCommandEnabled) return;

        Player player = event.getPlayer();
        if (player.hasPermission(settings.pluginsCommandBypassPermission)) return;

        String msg = event.getMessage();
        if (msg == null || msg.length() < 2 || msg.charAt(0) != '/') return;

        String root = parseRootCommandLower(msg);
        if (root == null) return;
        if (!settings.pluginsCommandsLower.contains(root)) return;

        event.setCancelled(true);

        List<String> shown = new ArrayList<>();
        Plugin[] plugins = Bukkit.getPluginManager().getPlugins();
        Map<String, String> repl = settings.pluginReplacementsLower;

        for (Plugin p : plugins) {
            if (p == null || p.getDescription() == null) continue;

            String realName = p.getDescription().getName();
            if (realName == null) continue;

            String display = realName;
            String mapped = repl.get(realName.toLowerCase());
            if (mapped != null) {
                String trimmed = mapped.trim();
                if (trimmed.isEmpty() || "-".equals(trimmed)) {
                    continue; // hidden
                }
                display = MythicSettings.color(mapped);
            }

            String color = p.isEnabled() ? COLOR_ENABLED : COLOR_DISABLED;
            shown.add(color + display);
        }

        String separator = messages.get("plugins.separator");
        String list = join(shown, separator);

        String out;
        if (messages.has("plugins.list-format")) {
            Map<String, String> ph = new HashMap<String, String>();
            ph.put("%count%", String.valueOf(shown.size()));
            ph.put("%list%", list);
            out = messages.format("plugins.list-format", ph);
        } else {
            // Backward-compat fallback to config.yml prefix if message.yml lacks plugins.list-format.
            out = settings.pluginsPrefix.replace("%count%", String.valueOf(shown.size())) + list;
        }
        player.sendMessage(out);
    }

    private static String parseRootCommandLower(String message) {
        // message starts with '/'
        int start = 1;
        int end = message.indexOf(' ');
        if (end == -1) end = message.length();
        if (end <= start) return null;
        return message.substring(start, end).toLowerCase();
    }

    private static String join(List<String> parts, String sep) {
        if (parts.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < parts.size(); i++) {
            if (i > 0) sb.append(sep);
            sb.append(parts.get(i));
        }
        return sb.toString();
    }
}
