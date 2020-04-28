package net.coopfury.JukeItOut.modules.configLoading;

import net.coopfury.JukeItOut.Game;
import net.coopfury.JukeItOut.GameModule;
import net.coopfury.JukeItOut.helpers.java.CastUtils;
import net.coopfury.JukeItOut.modules.configLoading.schema.ConfigSchemaLocation;
import net.coopfury.JukeItOut.modules.configLoading.schema.ConfigSchemaTeam;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.serialization.ConfigurationSerialization;

import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.logging.Logger;

public class ConfigLoadingModule extends GameModule {
    static {
        ConfigurationSerialization.registerClass(ConfigSchemaTeam.class);
        ConfigurationSerialization.registerClass(ConfigSchemaLocation.class);
    }

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
        loadFromEnum(logger, config.getConfigurationSection("locations"),
                ConfiguredLocation.values(), ConfigSchemaLocation.class,
                Enum::name, (enumValue, object) -> {
                    if (object.hasValidLocation())
                        enumValue.location = object.deserializedLocation;
                });
        logger.info("Loaded locations!");

        logger.info("Loading teams...");
        loadFromEnum(logger, config.getConfigurationSection("teams"),
                ConfiguredTeam.values(), ConfigSchemaTeam.class,
                Enum::name, (enumValue, object) -> {
                    logger.info("We loaded team " + enumValue.name() + "!");
                });
        logger.info("Loaded teams!");
    }

    private void serializeConfig(Game pluginInstance) {

    }

    private static<TEnumValue, TObjValue> void loadFromEnum(
            Logger logger, ConfigurationSection section,
            TEnumValue[] enumValues, Class<TObjValue> objType,
            Function<TEnumValue, String> pathProvider, BiConsumer<TEnumValue, TObjValue> writer) {

        if (section == null) {
            logger.warning("Entire section is null. Skipping.");
            return;
        }

        for (TEnumValue enumValue: enumValues) {
            String pathName = pathProvider.apply(enumValue);
            Object objectUnCasted = section.get(pathName);
            if (objectUnCasted == null) {
                logger.warning(String.format("Failed to get enum value \"%s\" in config (path missing).", pathName));
                continue;
            }

            Optional<TObjValue> object = CastUtils.dynamicCast(objType, objectUnCasted);
            if (!object.isPresent()) {
                logger.warning(String.format("Failed to deserialize enum value \"%s\" from config.", pathName));
                continue;
            }

            writer.accept(enumValue, object.get());
        }
    }
}
