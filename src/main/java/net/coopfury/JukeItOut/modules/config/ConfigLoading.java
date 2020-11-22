package net.coopfury.JukeItOut.modules.config;

import net.coopfury.JukeItOut.Plugin;
import net.coopfury.JukeItOut.helpers.node.GameNode;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.logging.Logger;

public class ConfigLoading extends GameNode {
    private ConfigRoot root;

    public ConfigLoading() {
        bindEnterHandler(this::reloadConfig);
        bindExitHandler(this::saveConfig);
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
