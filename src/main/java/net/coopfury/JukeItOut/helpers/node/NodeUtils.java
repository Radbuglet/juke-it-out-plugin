package net.coopfury.JukeItOut.helpers.node;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class NodeUtils {
    private NodeUtils() {}

    public static<T extends GameNode & Listener> void bindListener(T node, JavaPlugin plugin) {
        PluginManager manager = plugin.getServer().getPluginManager();

        node.bindEnterHandler(() -> manager.registerEvents(node, plugin), true);
        node.bindExitHandler(() -> HandlerList.unregisterAll(node));
    }
}
