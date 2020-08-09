package net.coopfury.JukeItOut.modules.configLoading;

import net.coopfury.JukeItOut.Plugin;
import net.coopfury.JukeItOut.PluginModule;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.logging.Logger;

public class ConfigLoadingModule implements PluginModule {
    public ConfigRoot root;

    private Logger getLogger() {
        return Plugin.instance.getLogger();
    }

    private FileConfiguration getConfig() {
        return Plugin.instance.getConfig();
    }

    public void reloadConfig() {
        getLogger().info("Reloaded config!");
        Plugin.instance.reloadConfig();
        root = new ConfigRoot(getConfig());
    }

    public void saveConfig() {
        getLogger().info("Saved config!");
        Plugin.instance.saveConfig();
    }

    @Override
    public void onEnable(Plugin pluginInstance) {
        reloadConfig();
    }

    @Override
    public void onDisable(Plugin pluginInstance) {
        saveConfig();
    }
}
