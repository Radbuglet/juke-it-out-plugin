package net.coopfury.JukeItOut;

import org.bukkit.event.Listener;

public abstract class GameModule implements Listener {
    protected void onEnable(Game pluginInstance) { }
    protected void onDisable(Game pluginInstance) { }
}
