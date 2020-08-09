package net.coopfury.JukeItOut;

import org.bukkit.event.Listener;

public interface PluginModule extends Listener {
    default void onEnable(Plugin pluginInstance) { }
    default void onDisable(Plugin pluginInstance) { }
}
