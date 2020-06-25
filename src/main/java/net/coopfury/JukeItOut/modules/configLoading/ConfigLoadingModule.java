package net.coopfury.JukeItOut.modules.configLoading;

import net.coopfury.JukeItOut.Plugin;
import net.coopfury.JukeItOut.PluginModule;
import net.coopfury.JukeItOut.helpers.config.ConfigDictionary;
import net.coopfury.JukeItOut.helpers.config.ConfigWrapperUtils;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.logging.Logger;

public class ConfigLoadingModule extends PluginModule {
    public final ConfigDictionary<ConfigTeam> teams = new ConfigDictionary<>(
            ConfigWrapperUtils.createStructWrapper(ConfigTeam::new)
    );

    private Logger getLogger() {
        return Plugin.getGame().getLogger();
    }

    private FileConfiguration getConfig() {
        return Plugin.getGame().getConfig();
    }

    public void reloadConfig() {
        getLogger().info("Reloaded config!");
        Plugin.getGame().reloadConfig();
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
