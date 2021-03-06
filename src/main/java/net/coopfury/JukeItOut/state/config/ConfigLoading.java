package net.coopfury.JukeItOut.state.config;

import net.coopfury.JukeItOut.Plugin;
import net.coopfury.JukeItOut.utils.java.signal.SignalPriority;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.logging.Logger;

public class ConfigLoading {
    private ConfigRoot root;

    public ConfigLoading() {
        Plugin.instance.onEnable.connect(e -> reloadConfig(), SignalPriority.Medium);
        Plugin.instance.onDisable.connect(e -> saveConfig(), SignalPriority.Medium);
    }

    private Logger getLogger() {
        return Plugin.instance.getLogger();
    }

    private FileConfiguration getConfig() {
        return Plugin.instance.getConfig();
    }

    public void reloadConfig() {
        getLogger().info("Reloaded config!");
        Plugin.instance.reloadConfig();
        root = new ConfigRoot(getConfig());  // We need to remake the object as the old instance is invalid.
    }

    public void saveConfig() {
        getLogger().info("Saved config!");
        Plugin.instance.saveConfig();
    }

    public ConfigRoot getRoot() {
        return root;
    }
}
