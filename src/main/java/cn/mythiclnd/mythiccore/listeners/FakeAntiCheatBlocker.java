package cn.mythiclnd.mythiccore.listeners;

import cn.mythiclnd.mythiccore.messages.Messages;
import cn.mythiclnd.mythiccore.settings.MythicSettings;
import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

public final class FakeAntiCheatBlocker {
    private static final Set<String> ROOTS = new HashSet<String>(Arrays.asList(
            "aac", "ncp", "verus", "spartan", "acr", "anticheat", "bac", "betteranticheat", "vulcan"
    ));

    private final JavaPlugin plugin;
    private final MythicSettings settings;
    private final Messages messages;
    private PacketAdapter adapter;

    public FakeAntiCheatBlocker(JavaPlugin plugin, MythicSettings settings, Messages messages) {
        this.plugin = plugin;
        this.settings = settings;
        this.messages = messages;
    }

    public void register() {
        if (!settings.fakeAntiCheatEnabled) return;

        final ProtocolManager manager = ProtocolLibrary.getProtocolManager();
        this.adapter = new PacketAdapter(plugin, ListenerPriority.HIGHEST, PacketType.Play.Client.CHAT) {
            @Override
            public void onPacketReceiving(PacketEvent event) {
                Player player = event.getPlayer();
                if (player == null) return;

                String text;
                try {
                    text = event.getPacket().getStrings().read(0);
                } catch (Throwable t) {
                    return;
                }
                if (text == null || text.length() < 2 || text.charAt(0) != '/') return;

                String root = parseRootCommandLower(text);
                if (root == null) return;

                int idx = root.lastIndexOf(':');
                if (idx != -1 && idx + 1 < root.length()) {
                    root = root.substring(idx + 1);
                }
                if (!ROOTS.contains(root)) return;

                // Enabled: swallow the command so it doesn't reach Bukkit, then respond.
                event.setCancelled(true);

                final String rootFinal = root;
                // Packet listeners can run off the main thread; send messages safely on the server thread.
                if (event.isAsync()) {
                    Bukkit.getScheduler().runTask(plugin, () -> sendFake(player, rootFinal));
                } else {
                    sendFake(player, rootFinal);
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

    private void sendFake(Player player, String rootLower) {
        List<String> lines = messages.getLines("fakeac." + rootLower + ".lines");
        if (lines.isEmpty()) return;
        for (String line : lines) {
            if (line != null && !line.isEmpty()) player.sendMessage(line);
        }
    }

    private static String parseRootCommandLower(String message) {
        int start = 1;
        int end = message.indexOf(' ');
        if (end == -1) end = message.length();
        if (end <= start) return null;
        return message.substring(start, end).toLowerCase(Locale.ROOT);
    }
}
