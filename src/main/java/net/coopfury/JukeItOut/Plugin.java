package net.coopfury.JukeItOut;

import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {
    // Properties
    public static Plugin instance;

    // Module loading
    @Override
    public void onEnable() {
        getLogger().info("JukeItOut enabling...");
        instance = this;
        getLogger().info("JukeItOut enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("JukeItOut disabling...");
        instance = null;
        getLogger().info("JukeItOut disabled!");
    }

    public void registerHandler(Listener listener) {
        getServer().getPluginManager().registerEvents(listener, this);
    }

    public void unregisterHandler(Listener listener) {
        HandlerList.unregisterAll(listener);
    }
}
