package cn.mythiclnd.mythiccore.listener;

import cn.mythiclnd.mythiccore.config.MessageConfig;
import cn.mythiclnd.mythiccore.config.model.FakeAntiCheatSettings;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import java.util.List;
import java.util.Locale;
import java.util.Set;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

public final class FakeAntiCheatBlocker {
    private static final Set<String> ROOTS = Set.of(
            "aac",
            "ncp",
            "verus",
            "spartan",
            "acr",
            "anticheat",
            "bac",
            "betteranticheat",
            "vulcan"
    );

    private final Plugin plugin;
    private final FakeAntiCheatSettings settings;
    private final MessageConfig messages;
    private PacketAdapter adapter;

    public FakeAntiCheatBlocker(Plugin plugin, FakeAntiCheatSettings settings, MessageConfig messages) {
        this.plugin = plugin;
        this.settings = settings;
        this.messages = messages;
    }

    public void register() {
        if (!settings.isEnabled()) return;

        ProtocolManager protocolManager = ProtocolLibrary.getProtocolManager();
        adapter = new PacketAdapter(plugin, ListenerPriority.HIGHEST, PacketType.Play.Client.CHAT) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                if (player == null) return;

                String text;
                try {
                    text = event.getPacket().getStrings().read(0);
                } catch (Throwable throwable) {
                    return;
                }

                if (text == null || text.length() < 2 || text.charAt(0) != '/') return;

                String root = parseRootCommandLower(text);
                if (root == null) return;

                int namespaceIndex = root.lastIndexOf(':');
                if (namespaceIndex != -1 && namespaceIndex + 1 < root.length()) {
                    root = root.substring(namespaceIndex + 1);
                }
                if (!ROOTS.contains(root)) return;

                event.setCancelled(true);
                String responseKey = root;
                if (event.isAsync()) {
                    Bukkit.getScheduler().runTask(plugin, () -> sendFake(player, responseKey));
                    return;
                }
                sendFake(player, responseKey);
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

    private void sendFake(Player player, String root) {
        List<String> lines = messages.getLines("fake-anti-cheat.responses." + root + ".lines");
        for (String line : lines) {
            if (!line.isEmpty()) player.sendMessage(line);
        }
    }

    private String parseRootCommandLower(String message) {
        int start = 1;
        int end = message.indexOf(' ');
        if (end == -1) end = message.length();
        if (end <= start) return null;
        return message.substring(start, end).toLowerCase(Locale.ROOT);
    }
}
