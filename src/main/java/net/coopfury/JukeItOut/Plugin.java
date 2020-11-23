package net.coopfury.JukeItOut;

import net.coopfury.JukeItOut.helpers.java.signal.EventSignal;
import net.coopfury.JukeItOut.modules.GlobalFixes;
import net.coopfury.JukeItOut.modules.commands.CommandRegistrar;
import net.coopfury.JukeItOut.modules.config.ConfigLoading;
import net.coopfury.JukeItOut.modules.game.GameManager;
import org.bukkit.event.Listener;
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
    public GameManager gameManager;

    // Module loading
    @Override
    public void onEnable() {
        getLogger().info("JukeItOut enabling...");
        instance = this;

        // Build modules
        globalFixes = new GlobalFixes();
        config = new ConfigLoading();
        commands = new CommandRegistrar();
        gameManager = new GameManager();

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

    public void registerListener(Listener listener) {

    }

    public void unregisterListener(Listener listener) {

    }
}
