package net.coopfury.JukeItOut;

import net.coopfury.JukeItOut.helpers.node.RootGameNode;
import net.coopfury.JukeItOut.modules.GlobalFixes;
import net.coopfury.JukeItOut.modules.commands.CommandRegistrar;
import net.coopfury.JukeItOut.modules.config.ConfigLoading;
import org.bukkit.plugin.java.JavaPlugin;

public class Plugin extends JavaPlugin {
    // Service properties
    public static Plugin instance;
    private final RootGameNode root = new RootGameNode(false);

    // Modules
    public GlobalFixes globalFixes;
    public ConfigLoading config;
    public CommandRegistrar commands;

    // Module loading
    @Override
    public void onEnable() {
        instance = this;

        getLogger().info("JukeItOut enabling...");
        root.addChild(globalFixes = new GlobalFixes());
        root.addChild(config = new ConfigLoading());
        root.addChild(commands = new CommandRegistrar());
        root.mount();
        getLogger().info("JukeItOut enabled!");
    }

    @Override
    public void onDisable() {
        getLogger().info("JukeItOut disabling...");
        root.unmount();
        getLogger().info("JukeItOut disabled!");
        instance = null;
    }
}
