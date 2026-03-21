package cn.mythiclnd.mythiccore.listener;

import cn.mythiclnd.mythiccore.config.model.TabCompleteSettings;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import java.util.Locale;
import java.util.Set;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class TabCompleteBlocker {
    private final Plugin plugin;
    private final TabCompleteSettings settings;
    private PacketAdapter adapter;

    public TabCompleteBlocker(Plugin plugin, TabCompleteSettings settings) {
        this.plugin = plugin;
        this.settings = settings;
    }

    public void register() {
        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        adapter = new PacketAdapter(plugin, ListenerPriority.HIGHEST, PacketType.Play.Client.TAB_COMPLETE) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                if (player == null) return;
                if (player.hasPermission(settings.getBypassPermission())) return;

                String input;
                try {
                    input = event.getPacket().getStrings().read(0);
                } catch (Throwable throwable) {
                    return;
                }

                if (input == null || input.isEmpty() || input.charAt(0) != '/') return;
                if (!shouldBlock(input.toLowerCase(Locale.ROOT), settings)) return;

                event.setCancelled(true);
                try {
                    PacketContainer response = protocolManager.createPacket(PacketType.Play.Server.TAB_COMPLETE);
                    response.getStringArrays().write(0, new String[0]);
                    protocolManager.sendServerPacket(player, response);
                } catch (Throwable ignored) {
                }
            }
        };
        protocolManager.addPacketListener(adapter);
    }

    public void unregister() {
        if (adapter == null) return;
        try {
            ProtocolLibrary.getProtocolManager().removePacketListener(adapter);
        } catch (Throwable ignored) {
        } finally {
            adapter = null;
        }
    }

    private boolean shouldBlock(String inputLower, TabCompleteSettings settings) {
        if (startsWithRootCommand(inputLower, "mythiccore") || startsWithRootCommand(inputLower, "mcr")) return false;
        if ("whitelist".equals(settings.getMode())) {
            return !matchesAnyPrefix(inputLower, settings.getWhitelistPrefixes());
        }
        return matchesAnyPrefix(inputLower, settings.getBlacklistPrefixes());
    }

    private boolean startsWithRootCommand(String inputLower, String rootLower) {
        String prefix = "/" + rootLower;
        if (!inputLower.startsWith(prefix)) return false;
        if (inputLower.length() == prefix.length()) return true;
        char next = inputLower.charAt(prefix.length());
        return next == ' ' || next == ':';
    }

    private boolean matchesAnyPrefix(String inputLower, Set<String> prefixesLower) {
        for (String prefix : prefixesLower) {
            if (prefix != null && !prefix.isEmpty() && inputLower.startsWith(prefix)) return true;
        }
        return false;
    }
}
