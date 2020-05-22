package net.coopfury.JukeItOut;

import org.bukkit.event.Listener;

public abstract class PluginModule implements Listener {
    protected void onEnable(Plugin pluginInstance) { }
    protected void onDisable(Plugin pluginInstance) { }
}
