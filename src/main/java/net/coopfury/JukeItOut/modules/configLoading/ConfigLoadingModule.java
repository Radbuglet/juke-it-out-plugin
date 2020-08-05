package net.coopfury.JukeItOut.modules.configLoading;

import net.coopfury.JukeItOut.Plugin;
import net.coopfury.JukeItOut.PluginModule;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.logging.Logger;

public class ConfigLoadingModule extends PluginModule {
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
    protected void onEnable(Plugin pluginInstance) {
        reloadConfig();
    }

    @Override
    protected void onDisable(Plugin pluginInstance) {
        saveConfig();
    }
}
