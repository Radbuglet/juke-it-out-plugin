package net.coopfury.JukeItOut.modules.configLoading;

import net.coopfury.JukeItOut.Constants;
import net.coopfury.JukeItOut.Game;
import net.coopfury.JukeItOut.GameModule;
import net.coopfury.JukeItOut.modules.configLoading.schema.ConfigSchemaLocation;
import org.bukkit.configuration.file.FileConfiguration;

import java.util.logging.Logger;

public class ConfigLoadingModule extends GameModule {
    // Config entry-points
    public void loadConfig(Game pluginInstance) {
        pluginInstance.reloadConfig();
        deserializeConfig(pluginInstance);
    }

    public void saveConfig(Game pluginInstance) {
        serializeConfig(pluginInstance);
        pluginInstance.saveConfig();
    }

    @Override
    protected void onEnable(Game pluginInstance) {
        deserializeConfig(pluginInstance);
    }

    @Override
    protected void onDisable(Game pluginInstance) {
        serializeConfig(pluginInstance);
    }

    // Shared serialization code
    private void deserializeConfig(Game pluginInstance) {
        Logger logger = pluginInstance.getLogger();
        FileConfiguration config = pluginInstance.getConfig();

        logger.info("Loading locations...");
        for (ConfiguredLocation deserializedLocation: ConfiguredLocation.values()) {
            Object maybeConfigLocation = config.get(Constants.locations_root);
            if (!(maybeConfigLocation instanceof ConfigSchemaLocation)) {
                logger.info(String.format("Location \"%s\" was not parsed as a location.", deserializedLocation.name()));
                continue;
            }

            ConfigSchemaLocation configLocation = (ConfigSchemaLocation) maybeConfigLocation;
            if (configLocation.deserializedLocation == null) {
                logger.info(String.format("Location \"%s\" is not a valid location.", deserializedLocation.name()));
                continue;
            }
            deserializedLocation.location = configLocation.deserializedLocation;
            logger.info(String.format("Loaded location \"%s\".", deserializedLocation.name()));
        }
        logger.info("Loaded locations!");
    }

    private void serializeConfig(Game pluginInstance) {

    }
}
