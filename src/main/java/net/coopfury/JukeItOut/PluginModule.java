package net.coopfury.JukeItOut;

import org.bukkit.event.Listener;

// Done as a class because interfaces don't support default/optional implementations in the target version of Java.
public abstract class PluginModule implements Listener {
    protected void onEnable(Plugin pluginInstance) { }
    protected void onDisable(Plugin pluginInstance) { }
}
