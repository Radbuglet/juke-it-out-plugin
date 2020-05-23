package net.coopfury.JukeItOut.modules.configLoading;

import net.coopfury.JukeItOut.Plugin;
import net.coopfury.JukeItOut.PluginModule;
import net.coopfury.JukeItOut.helpers.config.ConfigSchema;
import net.coopfury.JukeItOut.modules.configLoading.schema.SchemaLocation;
import net.coopfury.JukeItOut.modules.configLoading.schema.SchemaTeam;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.logging.Logger;

public class ConfigLoadingModule extends PluginModule {
    static {
        ConfigSchema.registerSchema(SchemaTeam.class);
        ConfigSchema.registerSchema(SchemaLocation.class);
    }

    private FileConfiguration getConfig() {
        return Plugin.getGame().getConfig();
    }

    private Logger getLogger() {
        return Plugin.getGame().getLogger();
    }

    public void loadConfig() {
        FileConfiguration conf = getConfig();
        Logger logger = getLogger();
        logger.info("Loading config...");
    }

    public void saveConfig() {
        FileConfiguration conf = getConfig();
        Logger logger = getLogger();
        logger.info("Saving config...");
    }

    @Override
    protected void onEnable(Plugin pluginInstance) {
        loadConfig();
    }

    @Override
    protected void onDisable(Plugin pluginInstance) {
        saveConfig();
    }
}
