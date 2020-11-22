package net.coopfury.JukeItOut.modules.config;

import net.coopfury.JukeItOut.Plugin;
import net.coopfury.JukeItOut.helpers.java.signal.BaseSignal;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.logging.Logger;

public class ConfigLoading {
    private ConfigRoot root;

    public ConfigLoading() {
        Plugin.instance.onEnable.connect(e -> reloadConfig(), BaseSignal.Priority.Medium);
        Plugin.instance.onDisable.connect(e -> saveConfig(), BaseSignal.Priority.Medium);
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
