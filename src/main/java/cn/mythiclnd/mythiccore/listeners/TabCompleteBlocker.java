package cn.mythiclnd.mythiccore.listeners;

import cn.mythiclnd.mythiccore.settings.MythicSettings;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.events.PacketContainer;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Locale;
import java.util.Set;

public final class TabCompleteBlocker {
    private final JavaPlugin plugin;
    private final MythicSettings settings;
    private PacketAdapter adapter;

    public TabCompleteBlocker(JavaPlugin plugin, MythicSettings settings) {
        this.plugin = plugin;
        this.settings = settings;
    }

    public void register() {
        if (!settings.tabCompleteEnabled) return;

        final ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        this.adapter = new PacketAdapter(plugin, ListenerPriority.HIGHEST, PacketType.Play.Client.TAB_COMPLETE) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                if (player == null) return;
                if (player.hasPermission(settings.tabCompleteBypassPermission)) return;

                String text;
                try {
                    text = event.getPacket().getStrings().read(0);
                } catch (Throwable t) {
                    return;
                }

                if (text == null || text.isEmpty()) return;
                if (text.charAt(0) != '/') return;

                String input = text.toLowerCase(Locale.ROOT);
                boolean shouldBlock = shouldBlock(input, settings);
                if (!shouldBlock) return;

                event.setCancelled(true);

                // Send an empty response immediately so the client doesn't keep waiting.
                try {
                    PacketContainer response = manager.createPacket(PacketType.Play.Server.TAB_COMPLETE);
                    response.getStringArrays().write(0, new String[0]);
                    manager.sendServerPacket(player, response);
                } catch (Throwable ignored) {
                    // If we fail to send, cancellation still prevents server-side completion.
                }
            }
        };
        manager.addPacketListener(this.adapter);
    }

    public void unregister() {
        if (this.adapter == null) return;
        try {
            ProtocolLibrary.getProtocolManager().removePacketListener(this.adapter);
        } catch (Throwable ignored) {
        } finally {
            this.adapter = null;
        }
    }

    private static boolean shouldBlock(String inputLower, MythicSettings settings) {
        // Always allow tab completion for MythicCore's own command so `/mythiccore reload` can be completed.
        if (startsWithRootCommand(inputLower, "mythiccore") || startsWithRootCommand(inputLower, "mc")) {
            return false;
        }
        if ("whitelist".equals(settings.tabCompleteModeLower)) {
            // Allow only whitelisted prefixes; block all other commands.
            return !matchesAnyPrefix(inputLower, settings.tabCompleteWhitelistPrefixesLower);
        }
        // blacklist (default): block only when a blacklist prefix matches.
        return matchesAnyPrefix(inputLower, settings.tabCompleteBlacklistPrefixesLower);
    }

    private static boolean startsWithRootCommand(String inputLower, String rootLower) {
        String prefix = "/" + rootLower;
        if (!inputLower.startsWith(prefix)) return false;
        if (inputLower.length() == prefix.length()) return true;
        char next = inputLower.charAt(prefix.length());
        return next == ' ' || next == ':';
    }

    private static boolean matchesAnyPrefix(String inputLower, Set<String> prefixesLower) {
        if (prefixesLower == null || prefixesLower.isEmpty()) return false;
        for (String p : prefixesLower) {
            if (p == null || p.isEmpty()) continue;
            if (inputLower.startsWith(p)) return true;
        }
        return false;
    }
}
