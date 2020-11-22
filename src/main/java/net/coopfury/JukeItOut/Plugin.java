package net.coopfury.JukeItOut;

import net.coopfury.JukeItOut.helpers.java.signal.EventSignal;
import net.coopfury.JukeItOut.modules.GlobalFixes;
import net.coopfury.JukeItOut.modules.commands.CommandRegistrar;
import net.coopfury.JukeItOut.modules.config.ConfigLoading;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {
    // Core
    public static Plugin instance;
    public final EventSignal<Plugin> onEnable = new EventSignal<>();
    public final EventSignal<Plugin> onDisable = new EventSignal<>();

    // Modules
    public GlobalFixes globalFixes;
    public ConfigLoading config;
    public CommandRegistrar commands;

    // Module loading
    @Override
    public void onEnable() {
        getLogger().info("JukeItOut enabling...");
        instance = this;

        // Build modules
        globalFixes = new GlobalFixes();
        config = new ConfigLoading();
        commands = new CommandRegistrar();

        // Fire them up!
        onEnable.fire(this);
        getLogger().info("JukeItOut enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("JukeItOut disabling...");
        onDisable.fire(this);
        getLogger().info("JukeItOut disabled!");
        instance = null;
    }
}
