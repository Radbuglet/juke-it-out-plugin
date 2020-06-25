package net.coopfury.JukeItOut.modules.configLoading;

import net.coopfury.JukeItOut.Plugin;
import net.coopfury.JukeItOut.PluginModule;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.logging.Logger;

public class ConfigLoadingModule extends PluginModule {
    public ConfigRoot root;

    private Logger getLogger() {
        return Plugin.getGame().getLogger();
    }

    private FileConfiguration getConfig() {
        return Plugin.getGame().getConfig();
    }

    public void reloadConfig() {
        getLogger().info("Reloaded config!");
        Plugin.getGame().reloadConfig();
        root = new ConfigRoot(getConfig().getValues(false));
    }

    public void saveConfig() {
        getLogger().info("Saved config!");
        Plugin.getGame().saveConfig();
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
